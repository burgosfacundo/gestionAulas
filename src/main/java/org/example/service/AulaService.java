package org.example.service;

import org.example.enums.BloqueHorario;
import org.example.exception.BadRequestException;
import org.example.exception.JsonNotFoundException;
import org.example.exception.NotFoundException;
import org.example.model.Aula;
import org.example.model.dto.ReservaDTO;
import org.example.repository.AulaRepository;
import org.example.repository.ReservaRepository;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;


/**
 * Clase que se encarga de comunicarse con el repositorio
 * y aplicar la lógica de negocio para manipular aulas
 */
public class AulaService implements Service<Integer,Aula> {
    AulaRepository repositorio = new AulaRepository();
    ReservaRepository reservaRepository = new ReservaRepository();

    /**
     * Método para listar todas las aulas
     * @return List<Aula>
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public List<Aula> listar() throws JsonNotFoundException {
        return repositorio.getAll();
    }


    /**
     * Método parar guardar un aula
     * @param aula que queremos guarde
     * @return Aula que se guarda
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     * @throws BadRequestException si existe un aula con ese código
     */
    @Override
    public Aula guardar(Aula aula) throws JsonNotFoundException, BadRequestException {
        // Verificamos que no existe esa aula y si existe lanzamos la excepción
        var optional = repositorio.findByNumero(aula.getNumero());
        if (optional.isPresent()){
            throw new BadRequestException(STR."Ya existe el aula \{aula.getNumero()}");
        }

        repositorio.save(aula);
        return aula;
    }

    /**
     * Método para eliminar un aula por número
     * @param numero del aula que queremos eliminar
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     * @throws NotFoundException si no se encuentra un aula con ese numero
     */
    @Override
    public void eliminar(Integer numero) throws JsonNotFoundException, NotFoundException {
        // Verificamos que existe una asignatura con ese ID y si no lanzamos la excepción
        var optional = repositorio.findByNumero(numero);
        if (optional.isEmpty()){
            throw new NotFoundException(STR."No existe el aula \{numero}");
        }

        repositorio.deleteById(optional.get().getId());
    }

    /**
     * Método para obtener un aula por numero
     * @param numero del aula que queremos obtener
     * @return Aula con ese numero
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     * @throws NotFoundException si no se encuentra un aula con ese ID
     */
    @Override
    public Aula obtener(Integer numero) throws JsonNotFoundException, NotFoundException {
        var optional = repositorio.findByNumero(numero);
        if (optional.isEmpty()){
            throw new NotFoundException(STR."No existe el aula \{numero}");
        }
        return optional.get();
    }

    /**
     * Método para modificar un aula
     * @param aula modificada
     * @throws JsonNotFoundException si no encuentra el archivo JSON
     * @throws NotFoundException si no encuentra el aula
     */
    @Override
    public void modificar(Aula aula) throws JsonNotFoundException, NotFoundException {
        //Verificamos que el aula con ese ID exista
        var optional = repositorio.findById(aula.getId());
        if (optional.isEmpty()){
            throw new NotFoundException(STR."No existe una aula con el id: \{aula.getId()}");
        }

        //la modificamos
        repositorio.modify(aula);
    }

    //Filtros

    /**
     * Método para filtrar Aulas por su capacidad(Si tiene la capacidad o más)
     * @param capacidad que se necesita
     * @return List<Aula> la lista de aulas que tienen esa capacidad
     * @throws JsonNotFoundException Sí existe un problema con el archivo JSON
     */
    public List<Aula> filtrarPorCapacidad(int capacidad) throws JsonNotFoundException {
        List<Aula> lista = repositorio.getAll();
        return lista.stream()
                .filter(aula -> aula.getCapacidad() >= capacidad)
                .toList();
    }

    /**
     * Método para filtrar Aulas que tengan proyector
     * @return List<Aula> lista de aulas que tienen proyector
     * @throws JsonNotFoundException Sí hay un problema con el archivo JSON
     */
    public List<Aula> filtrarPorProyector() throws JsonNotFoundException {
        List<Aula> lista = repositorio.getAll();
        return lista.stream()
                .filter(Aula::isTieneProyector)
                .toList();
    }

    /**
     * Método para filtrar Aulas que tengan TV
     * @return List<Aula> lista de aulas que tienen TV
     * @throws JsonNotFoundException Sí hay un problema con el archivo JSON
     */
    public List<Aula> filtrarPorTv() throws JsonNotFoundException {
        List<Aula> lista = repositorio.getAll();
        return lista.stream()
                .filter(Aula::isTieneTV)
                .toList();
    }

    /**
     * Método que para filtrar Aulas por capacidad, proyector y TV
     * @param capacidad la capacidad que se necesita
     * @param tieneProyector sí debe tener o no proyector
     * @param tieneTV sí debe tener o no TV
     * @return List<Aula> lista de aulas que cumplan con las condiciones
     * @throws JsonNotFoundException Sí existe un problema con el archivo JSON
     */
    public List<Aula> filtrarPorCondiciones(int capacidad, boolean tieneProyector, boolean tieneTV) throws JsonNotFoundException {
        List<Aula> lista = repositorio.getAll();
        return lista.stream()
                .filter(aula -> aula.getCapacidad() >= capacidad)
                .filter(aula -> aula.isTieneProyector() == tieneProyector)
                .filter(aula -> aula.isTieneTV() == tieneTV)
                .toList();
    }


    /**
     * Método para filtrar Aulas por fecha y periodo
     * @param fechaInicio desde que fecha debe estar disponible
     * @param fechaFin hasta que fecha debe estar disponible
     * @param bloqueHorario horario que se necesita el Aula
     * @return List<Aula> lista de aulas que cumplan con las condiciones
     * @throws JsonNotFoundException Sí existe un problema con el archivo JSON
     */
    public List<Aula> listarAulasDisponibles(LocalDate fechaInicio, LocalDate fechaFin,
                                             BloqueHorario bloqueHorario, Set<DayOfWeek> diasSemana)
                                            throws JsonNotFoundException {

        /**
         * todo: hay que agregar dia de la semana a la logica
         */

        // Obtenemos todas las aulas del repositorio
        List<Aula> aulas = repositorio.getAll();

        // Filtramos las reservas existentes que coinciden con el bloque horario y se solapan en fechas
        List<Integer> aulasSolapadasIds = reservaRepository.getAll()
                .stream()
                .filter(r -> r.bloque() == bloqueHorario)
                .filter(r -> seSolapan(fechaInicio, fechaFin, r.fechaInicio(), r.fechaFin()))
                .map(ReservaDTO::idAula)
                .toList();

        // Excluimos las aulas solapadas de la lista de todas las aulas y las devolvemos como disponibles
        return aulas.stream()
                .filter(aula -> !aulasSolapadasIds.contains(aula.getId()))
                .toList();
    }


    /**
     * Método para saber si dos períodos de tiempo se solapan
     * @param fechaInicio1 inicio del primer período
     * @param fechaFin1 fin del primer período
     * @param fechaInicio2 inicio del segundo período
     * @param fechaFin2 fin del segundo período
     * @return boolean si los períodos se solapan o no
     */
    public boolean seSolapan(LocalDate fechaInicio1, LocalDate fechaFin1, LocalDate fechaInicio2, LocalDate fechaFin2) {
        // Un periodo se solapa con otro si:
        // - El inicio del primer periodo está entre las fechas del segundo periodo
        // - El fin del primer periodo está entre las fechas del segundo periodo
        // - El inicio del segundo periodo está entre las fechas del primer periodo
        // - El fin del segundo periodo está entre las fechas del primer periodo
        return !fechaFin1.isBefore(fechaInicio2) && !fechaInicio1.isAfter(fechaFin2);
    }

}