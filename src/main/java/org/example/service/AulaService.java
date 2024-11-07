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
public class AulaService{
    private final AulaRepository repositorio = new AulaRepository();
    private final ReservaRepository reservaRepository = new ReservaRepository();

    /**
     * Método para listar todas las aulas
     * @return List<Aula>
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
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
    public Aula guardar(Aula aula) throws JsonNotFoundException, BadRequestException {
        // Verificamos que no existe esa aula y si existe lanzamos la excepción
        var optional = repositorio.findByNumero(aula.getNumero());
        if (optional.isPresent()){
            throw new BadRequestException(STR."Ya existe el aula o laboratorio \{aula.getNumero()}");
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
    public void eliminar(Integer numero) throws JsonNotFoundException, NotFoundException {
        // Verificamos que existe una asignatura con ese número y si no lanzamos la excepción
        var aula = validarAulaExistenteByNumero(numero);

        // Borramos esa aula por ID
        repositorio.deleteById(aula.getId());
    }

    /**
     * Método para obtener un aula por numero
     * @param numero del aula que queremos obtener
     * @return Aula con ese numero
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     * @throws NotFoundException si no se encuentra un aula con ese ID
     */
    public Aula obtener(Integer numero) throws JsonNotFoundException, NotFoundException {
        // Validamos y retornamos el aula por número
        return validarAulaExistenteByNumero(numero);
    }

    /**
     * Método para modificar un aula
     * @param aula modificada
     * @throws JsonNotFoundException si no encuentra el archivo JSON
     * @throws NotFoundException si no encuentra el aula
     */
    public void modificar(Aula aula) throws JsonNotFoundException, NotFoundException {
        //Verificamos que el aula con ese ID exista
        validarAulaExistenteById(aula.getId());

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
        return listar().stream()
                .filter(aula -> aula.getCapacidad() >= capacidad)
                .toList();
    }

    /**
     * Método para filtrar Aulas que tengan proyector
     * @return List<Aula> lista de aulas que tienen proyector
     * @throws JsonNotFoundException Sí hay un problema con el archivo JSON
     */
    public List<Aula> filtrarPorProyector() throws JsonNotFoundException {
        return listar().stream()
                .filter(Aula::isTieneProyector)
                .toList();
    }

    /**
     * Método para filtrar Aulas que tengan TV
     * @return List<Aula> lista de aulas que tienen TV
     * @throws JsonNotFoundException Sí hay un problema con el archivo JSON
     */
    public List<Aula> filtrarPorTv() throws JsonNotFoundException {
        return listar().stream()
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
     * Método para filtrar Aulas por fecha, período y días de la semana.
     * @param fechaInicio desde qué fecha debe estar disponible.
     * @param fechaFin hasta qué fecha debe estar disponible.
     * @param bloqueHorario horario en el que se necesita el Aula.
     * @param diasSemana conjunto con los días de la semana en los que se necesita el Aula.
     * @return List<Aula> lista de aulas que cumplen con las condiciones.
     * @throws JsonNotFoundException sí existe un problema con el archivo JSON.
     */
    public List<Aula> listarAulasDisponibles(LocalDate fechaInicio, LocalDate fechaFin,
                                             BloqueHorario bloqueHorario, Set<DayOfWeek> diasSemana)
            throws JsonNotFoundException {

        // Obtenemos todas las aulas y laboratorios del repositorio
        var aulas = listar();

        // Filtramos las reservas existentes que coinciden en bloque horario, fechas y días de la semana
        var idsAulasSolapadas = reservaRepository.getAll()
                .stream()
                .filter(r -> r.bloque() == bloqueHorario)
                .filter(r -> seSolapanFechas(fechaInicio, fechaFin, r.fechaInicio(), r.fechaFin()))
                .filter(r -> tieneDiasSolapados(r.diasSemana(), diasSemana)) // Nueva condición para días de la semana
                .map(ReservaDTO::idAula)
                .toList();

        // Filtramos las aulas disponibles que no están en las reservas solapadas
        return aulas.stream()
                .filter(aula -> !idsAulasSolapadas.contains(aula.getId()))
                .toList();
    }

    /**
     * Método que verifica si dos períodos de fechas se solapan.
     * @param fechaInicio1 inicio del primer período.
     * @param fechaFin1 fin del primer período.
     * @param fechaInicio2 inicio del segundo período.
     * @param fechaFin2 fin del segundo período.
     * @return boolean si los períodos se solapan o no.
     */
    public boolean seSolapanFechas(LocalDate fechaInicio1, LocalDate fechaFin1, LocalDate fechaInicio2, LocalDate fechaFin2) {
        return !fechaFin1.isBefore(fechaInicio2) && !fechaInicio1.isAfter(fechaFin2);
    }

    /**
     * Método que verifica si hay coincidencia en los días de la semana entre dos listas de días.
     * @param diasReserva días de la semana reservados.
     * @param diasSolicitados días de la semana solicitados.
     * @return boolean si existe al menos un día común.
     */
    private boolean tieneDiasSolapados(Set<DayOfWeek> diasReserva, Set<DayOfWeek> diasSolicitados) {
        for (DayOfWeek dia : diasSolicitados) {
            if (diasReserva.contains(dia)) {
                return true; // Se encontró un día en común
            }
        }
        return false; // No hay días en común
    }


    // Validaciones
    /**
     * Método para validar la existencia de un Aula por número
     * @param numeroAula del aula que se quiere verificar
     * @return Aula si existe
     * @throws NotFoundException Si no se encuentra el aula con ese número
     * @throws JsonNotFoundException Sí ocurre un error con el archivo JSON
     */
    private Aula validarAulaExistenteByNumero(Integer numeroAula) throws NotFoundException, JsonNotFoundException {
        return repositorio.findByNumero(numeroAula)
                .orElseThrow(() -> new NotFoundException(STR."No existe un aula con el número: \{numeroAula}"));
    }

    /**
     * Método para validar la existencia de un Aula por ID
     * @param idAula del aula que se quiere verificar
     * @return Aula si existe
     * @throws NotFoundException Si no se encuentra el aula con ese ID
     * @throws JsonNotFoundException Sí ocurre un error con el archivo JSON
     */
    private Aula validarAulaExistenteById(Integer idAula) throws NotFoundException, JsonNotFoundException {
        return repositorio.findById(idAula)
                .orElseThrow(() -> new NotFoundException(STR."No existe un aula con el id: \{idAula}"));
    }
}