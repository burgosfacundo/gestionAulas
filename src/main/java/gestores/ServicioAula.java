package gestores;

import enums.BloqueHorario;
import excepciones.AulaNoEncontradaException;
import excepciones.AulaYaExisteException;
import interfaces.Service;
import model.Aula;
import model.Reserva;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServicioAula implements Service<Aula> {
    RepositorioAula repositorioAula;

    public ServicioAula() {
        this.repositorioAula = new RepositorioAula();
    }

    @Override
    public void agregar(Aula entidad) throws Exception {
        List<Aula> lista = repositorioAula.listar();
        for (Aula a : lista) {
            if(a.getNumero() == entidad.getNumero()) {
                throw new AulaYaExisteException("El aula de número " + entidad.getNumero() + " ya existe.");
            }
        }
        repositorioAula.guardar(entidad);
    }

    @Override
    public Aula obtener(int id) throws Exception {
        Aula a;
        try{
            a = repositorioAula.leer(id);
        }catch(AulaNoEncontradaException e){
            System.err.println("Error: " + e.getMessage());
            throw e;
        }catch(Exception e){
            System.err.println("Error inesperado: " + e.getMessage());
            throw e;
        }
        return a;
    }

    @Override
    public void modificar(Aula entidad) throws Exception {
        try{
            repositorioAula.actualizar(entidad);
        }catch (AulaNoEncontradaException e){
            System.err.println("Error: " + e.getMessage());
            throw e;
        }catch(Exception e){
            System.err.println("Error inesperado: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void eliminar(int id) throws Exception {
        try{
            repositorioAula.eliminar(id);
        }catch (AulaNoEncontradaException e){
            System.err.println("Error: " + e.getMessage());
            throw e;
        }catch(Exception e){
            System.err.println("Error inesperado: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Aula> obtenerTodos(){
        return repositorioAula.listar();
    }

    public void clear(){
        repositorioAula.clear();
    }

    public List<Aula> filtrarPorCapacidad(int capacidad){
        List<Aula> lista = repositorioAula.listar();
        return lista.stream()
                .filter(aula -> aula.getCapacidad() >= capacidad)
                .toList();
    }

    public List<Aula> filtrarPorProyector(boolean condicion){
        List<Aula> lista = repositorioAula.listar();
        return lista.stream()
                .filter(aula -> aula.isTieneProyector() == condicion)
                .toList();
    }

    public List<Aula> filtrarPorTv(boolean condicion){
        List<Aula> lista = repositorioAula.listar();
        return lista.stream()
                .filter(aula -> aula.isTieneTV() == condicion)
                .toList();
    }

    public List<Aula> filtrarPorCondiciones(int capacidad, boolean tieneProyector, boolean tieneTV){
        List<Aula> lista = repositorioAula.listar();
        return lista.stream()
                .filter(aula -> aula.getCapacidad() >= capacidad)
                .filter(aula -> aula.isTieneProyector() == tieneProyector)
                .filter(aula -> aula.isTieneTV() == tieneTV)
                .toList();
    }

    public List<Aula> listarAulasDisponibles (LocalDate fechaInicio1, LocalDate fechaFin1, BloqueHorario bloqueHorario) {
        ServicioReserva servicioReserva = new ServicioReserva();

        List<Aula> aulas = obtenerTodos();
        List<Reserva> reservasSolapadas = servicioReserva.obtenerTodos()
                .stream()
                .filter(r -> r.getBloque() == bloqueHorario)
                .filter(r -> servicioReserva.seSolapan(fechaInicio1, fechaFin1, r.getFechaInicio(), r.getFechaFin()))
                .toList();

        List<Aula> aulasSolapadas = new ArrayList<>();

        for (Reserva r : reservasSolapadas) {
            aulasSolapadas.add(r.getAula());
        }

        //Sout con el único propósito de pruebas

        System.out.println("Aulas solapadas: \n " + aulasSolapadas);

        aulasSolapadas.forEach(aula -> aulas.remove(aula));
        return aulas;
    }



}