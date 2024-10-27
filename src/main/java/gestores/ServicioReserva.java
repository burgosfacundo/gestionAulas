package gestores;

import excepciones.ReservaNoEncontradaException;
import excepciones.ReservaYaExisteException;
import interfaces.Service;
import org.example.model.Reserva;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class ServicioReserva implements Service<Reserva> {
    RepositorioReserva repositorioReserva;

    public ServicioReserva() {
        this.repositorioReserva = new RepositorioReserva();
    }

    @Override
    public void agregar(Reserva entidad) throws Exception {
        List<Reserva> lista = repositorioReserva.listar();
        //deberia chequear si la reserva a añadir choca de alguna forma con otra
        repositorioReserva.guardar(entidad);
    }

    @Override
    public Reserva obtener(int id) throws Exception {
        Reserva r;
        try {
            r = repositorioReserva.leer(id);
        } catch (ReservaNoEncontradaException e) {
            System.err.println("Error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            throw e;
        }
        return r;
    }

    @Override
    public void modificar(Reserva entidad) throws Exception {
        try {
            repositorioReserva.actualizar(entidad);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void eliminar(int id) throws Exception {
        try {
            repositorioReserva.eliminar(id);
        } catch (ReservaNoEncontradaException e) {
            System.err.println("Error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Reserva> obtenerTodos() {
        return repositorioReserva.listar();
    }

    public void clear() {
        repositorioReserva.clear();
    }

    public boolean seSolapan(LocalDate fechaInicio1, LocalDate fechaFin1, LocalDate fechaInicio2, LocalDate fechaFin2) {
        // Un periodo se solapa con otro si:
        // - El inicio del primer periodo está entre las fechas del segundo periodo
        // - El fin del primer periodo está entre las fechas del segundo periodo
        // - El inicio del segundo periodo está entre las fechas del primer periodo
        // - El fin del segundo periodo está entre las fechas del primer periodo

        return (fechaInicio1.isAfter(fechaInicio2) && fechaInicio1.isBefore(fechaFin2)) ||
                (fechaFin1.isAfter(fechaInicio2) && fechaFin1.isBefore(fechaFin2)) ||
                (fechaInicio2.isAfter(fechaInicio1) && fechaInicio2.isBefore(fechaFin1)) ||
                (fechaFin2.isAfter(fechaInicio1) && fechaFin2.isBefore(fechaFin1));
    }
}
