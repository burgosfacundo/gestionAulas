package org.example.service;

import org.example.enums.BloqueHorario;
import org.example.exception.BadRequestException;
import org.example.exception.JsonNotFoundException;
import org.example.exception.NotFoundException;
import org.example.model.Aula;
import org.example.model.Laboratorio;
import org.example.model.dto.ReservaDTO;
import org.example.repository.AulaRepository;
import org.example.repository.ReservaRepository;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.StringTemplate.STR;


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
     * Método para eliminar un aula por ID
     * @param id del aula que queremos eliminar
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     * @throws NotFoundException si no se encuentra un aula con ese numero
     */
    public void eliminar(Integer id) throws JsonNotFoundException, NotFoundException {
        // Verificamos que existe una asignatura con ese ID y si no lanzamos la excepción
        var aula = validarAulaExistenteById(id);

        // Borramos esa aula por ID
        repositorio.deleteById(aula.getId());
    }

    /**
     * Método para obtener un aula por ID
     * @param id del aula que queremos obtener
     * @return Aula con ese ID
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     * @throws NotFoundException si no se encuentra un aula con ese ID
     */
    public Aula obtener(Integer id) throws JsonNotFoundException, NotFoundException {
        // Validamos y retornamos el aula por ID
        return validarAulaExistenteById(id);
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
     * Método para filtrar los laboratorios
     * @return List<Laboratorio> laboratorios encontrados
     * @throws JsonNotFoundException si ocurre un problema con el archivo JSON
     */
    public List<Laboratorio> listarLaboratorios() throws JsonNotFoundException {
        return listar().stream()
                .filter(aula -> aula instanceof Laboratorio)
                .map(aula -> (Laboratorio) aula)// Filtra si es instancia de Laboratorio
                .collect(Collectors.toList());
    }

    /**
     * Método para filtrar solo las Aulas estándar, excluyendo los Laboratorios.
     * @return List<Aula> lista de aulas estándar (sin incluir laboratorios)
     * @throws JsonNotFoundException sí existe un problema con el archivo JSON
     */
    public List<Aula> listarAulas() throws JsonNotFoundException {
        return listar().stream()
                .filter(aula -> !(aula instanceof Laboratorio)) // Filtra si no es instancia de Laboratorio
                .toList();
    }

    /**
     * Método para filtrar Espacios por capacidad, proyector y TV.
     * Permite que cualquiera de los parámetros sea null, y solo aplicará el filtro correspondiente si no es null.
     * @param capacidad la capacidad mínima requerida (puede ser null).
     * @param tieneProyector sí debe tener o no proyector (puede ser null).
     * @param tieneTV sí debe tener o no TV (puede ser null).
     * @return List<Aula> lista de espacios que cumplan con las condiciones.
     * @throws JsonNotFoundException Sí existe un problema con el archivo JSON.
     */
    public List<Aula> filtrarEspaciosPorCondiciones(Integer capacidad, Boolean tieneProyector, Boolean tieneTV) throws JsonNotFoundException {
        return listar().stream()
                .filter(aula -> capacidad == null || aula.getCapacidad() >= capacidad)
                .filter(aula -> tieneProyector == null || aula.isTieneProyector() == tieneProyector)
                .filter(aula -> tieneTV == null || aula.isTieneTV() == tieneTV)
                .toList();
    }

    /**
     * Método para filtrar Aulas estándar por capacidad, proyector y TV.
     * Permite que cualquiera de los parámetros sea null, y solo aplicará el filtro correspondiente si no es null.
     * @param capacidad la capacidad mínima requerida (puede ser null).
     * @param tieneProyector sí debe tener o no proyector (puede ser null).
     * @param tieneTV sí debe tener o no TV (puede ser null).
     * @return List<Aula> lista de aulas estándar que cumplan con las condiciones.
     * @throws JsonNotFoundException Sí existe un problema con el archivo JSON.
     */
    public List<Aula> filtrarAulasPorCondiciones(Integer capacidad, Boolean tieneProyector, Boolean tieneTV) throws JsonNotFoundException {
        return listarAulas().stream()
                .filter(aula -> capacidad == null || aula.getCapacidad() >= capacidad)
                .filter(aula -> tieneProyector == null || aula.isTieneProyector() == tieneProyector)
                .filter(aula -> tieneTV == null || aula.isTieneTV() == tieneTV)
                .toList();
    }

    /**
     * Método para filtrar Laboratorios por capacidad, proyector y TV.
     * Permite que cualquiera de los parámetros sea null, y solo aplicará el filtro correspondiente si no es null.
     * @param capacidad la capacidad mínima requerida (puede ser null).
     * @param tieneProyector sí debe tener o no proyector (puede ser null).
     * @param tieneTV sí debe tener o no TV (puede ser null).
     * @param computadoras las computadoras mínimas requeridas (puede ser null).
     * @return List<Laboratorio> lista de laboratorios que cumplan con las condiciones.
     * @throws JsonNotFoundException Sí existe un problema con el archivo JSON.
     */
    public List<Laboratorio> filtrarLaboratoriosPorCondiciones(Integer capacidad, Boolean tieneProyector, Boolean tieneTV,
                                                               Integer computadoras) throws JsonNotFoundException {
        return listarLaboratorios().stream()
                .filter(laboratorio -> capacidad == null || laboratorio.getCapacidad() >= capacidad)
                .filter(laboratorio -> tieneProyector == null || laboratorio.isTieneProyector() == tieneProyector)
                .filter(laboratorio -> tieneTV == null || laboratorio.isTieneTV() == tieneTV)
                .filter(laboratorio -> computadoras == null || laboratorio.getComputadoras() >= computadoras)
                .toList();
    }



    /**
     * Método para filtrar Aulas por fecha, período y días/bloques de la semana.
     * @param fechaInicio desde qué fecha debe estar disponible.
     * @param fechaFin hasta qué fecha debe estar disponible.
     * @param diasYBloques mapa con los días de la semana y sus respectivos bloques horarios.
     * @return List<Aula> lista de aulas que cumplen con las condiciones.
     * @throws JsonNotFoundException sí existe un problema con el archivo JSON.
     */
    public List<Aula> listarEspaciosDisponibles(LocalDate fechaInicio, LocalDate fechaFin,
                                             Map<DayOfWeek, Set<BloqueHorario>> diasYBloques)
            throws JsonNotFoundException {

        // Obtenemos todas las aulas y laboratorios del repositorio
        var aulas = listar();

        // Filtramos las reservas existentes que coinciden en los días y bloques horarios
        var idsAulasSolapadas = reservaRepository.getAll()
                .stream()
                .filter(r -> seSolapanFechas(fechaInicio, fechaFin, r.fechaInicio(), r.fechaFin()))
                .filter(r -> tieneSolapamientoEnDiasYBloques(r.diasYBloques(), diasYBloques))
                .map(ReservaDTO::idAula)
                .toList();

        // Filtramos las aulas disponibles que no están en las reservas solapadas
        return aulas.stream()
                .filter(aula -> !idsAulasSolapadas.contains(aula.getId()))
                .toList();
    }


    /**
     * Método para filtrar solo Aulas disponibles por fecha, período y días/bloques de la semana.
     * @param fechaInicio desde qué fecha debe estar disponible.
     * @param fechaFin hasta qué fecha debe estar disponible.
     * @param diasYBloques mapa con los días de la semana y sus respectivos bloques horarios.
     * @return List<Aula> lista de aulas disponibles que cumplen con las condiciones.
     * @throws JsonNotFoundException sí existe un problema con el archivo JSON.
     */
    public List<Aula> listarAulasDisponibles(LocalDate fechaInicio, LocalDate fechaFin,
                                             Map<DayOfWeek, Set<BloqueHorario>> diasYBloques) throws JsonNotFoundException {

        // Filtramos primero las Aulas
        var aulas = listarAulas();

        // Filtramos las reservas existentes que coinciden en los días y bloques horarios
        var idsAulasSolapadas = reservaRepository.getAll()
                .stream()
                .filter(r -> seSolapanFechas(fechaInicio, fechaFin, r.fechaInicio(), r.fechaFin()))
                .filter(r -> tieneSolapamientoEnDiasYBloques(r.diasYBloques(), diasYBloques))
                .map(ReservaDTO::idAula)
                .toList();

        // Filtramos las Aulas disponibles que no están en las reservas solapadas
        return aulas.stream()
                .filter(aula -> !idsAulasSolapadas.contains(aula.getId()))
                .toList();
    }


    /**
     * Método para filtrar solo Laboratorios disponibles por fecha, período y días/bloques de la semana.
     * @param fechaInicio desde qué fecha debe estar disponible.
     * @param fechaFin hasta qué fecha debe estar disponible.
     * @param diasYBloques mapa con los días de la semana y sus respectivos bloques horarios.
     * @return List<Laboratorio> lista de laboratorios disponibles que cumplen con las condiciones.
     * @throws JsonNotFoundException sí existe un problema con el archivo JSON.
     */
    public List<Laboratorio> listarLaboratoriosDisponibles(LocalDate fechaInicio, LocalDate fechaFin,
                                                           Map<DayOfWeek, Set<BloqueHorario>> diasYBloques) throws JsonNotFoundException {

        // Filtramos primero los Laboratorios
        var laboratorios = listarLaboratorios();

        // Filtramos las reservas existentes que coinciden en los días y bloques horarios
        var idsLaboratoriosSolapados = reservaRepository.getAll()
                .stream()
                .filter(r -> seSolapanFechas(fechaInicio, fechaFin, r.fechaInicio(), r.fechaFin()))
                .filter(r -> tieneSolapamientoEnDiasYBloques(r.diasYBloques(), diasYBloques))
                .map(ReservaDTO::idAula)
                .toList();

        // Filtramos los Laboratorios disponibles que no están en las reservas solapadas
        return laboratorios.stream()
                .filter(laboratorio -> !idsLaboratoriosSolapados.contains(laboratorio.getId()))
                .toList();
    }


    // Metodo para listar aulas segun los datos obtenidos por el cambio solicitud profesor
    public List<Aula> listarAulasDisponiblesPorSolicitud(Integer capacidad, Boolean tieneProyector, Boolean tieneTV, LocalDate fechaInicio, LocalDate fechaFin,
                                                         Map<DayOfWeek, Set<BloqueHorario>> diasYBloques) throws JsonNotFoundException {


        return listarAulasDisponibles(fechaInicio, fechaFin, diasYBloques).stream()
                .filter(aula -> capacidad == null || aula.getCapacidad() >= capacidad)
                .filter(aula -> tieneProyector == null || aula.isTieneProyector() == tieneProyector)
                .filter(aula -> tieneTV == null || aula.isTieneTV() == tieneTV)
                .toList();
    }

    // Metodo para listar aulas segun los datos obtenidos por el cambio solicitud profesor
    public List<Laboratorio> listarLaboratoriosDisponiblesPorSolicitud(Integer computadoras, Integer capacidad, Boolean tieneProyector, Boolean tieneTV, LocalDate fechaInicio, LocalDate fechaFin,
                                                         Map<DayOfWeek, Set<BloqueHorario>> diasYBloques) throws JsonNotFoundException {


        return listarLaboratoriosDisponibles(fechaInicio, fechaFin, diasYBloques).stream()
                .filter(aula -> capacidad == null || aula.getCapacidad() >= capacidad)
                .filter(aula -> tieneProyector == null || aula.isTieneProyector() == tieneProyector)
                .filter(aula -> tieneTV == null || aula.isTieneTV() == tieneTV)
                .filter(aula -> computadoras == null || aula.getComputadoras() >= computadoras)
                .toList();
    }



    // Validaciones

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
     * Método que verifica si hay solapamiento en los días y bloques horarios entre dos mapas.
     * @param diasYBloquesReserva días y bloques horarios de una reserva existente.
     * @param diasYBloquesSolicitados días y bloques horarios solicitados para disponibilidad.
     * @return boolean si existe al menos un día y bloque horario común.
     */
    private boolean tieneSolapamientoEnDiasYBloques(Map<DayOfWeek, Set<BloqueHorario>> diasYBloquesReserva,
                                                    Map<DayOfWeek, Set<BloqueHorario>> diasYBloquesSolicitados) {
        for (var entry : diasYBloquesSolicitados.entrySet()) {
            var diaSolicitado = entry.getKey();
            var bloquesSolicitados = entry.getValue();

            if (diasYBloquesReserva.containsKey(diaSolicitado)) {
                var bloquesReservados = diasYBloquesReserva.get(diaSolicitado);
                // Verificamos si hay al menos un bloque horario en común
                if (bloquesReservados.stream().anyMatch(bloquesSolicitados::contains)) {
                    return true;
                }
            }
        }
        return false;
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