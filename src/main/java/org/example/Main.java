package org.example;


import org.example.service.AsignaturaService;
import org.example.service.AulaService;
import org.example.service.InscripcionService;
import org.example.service.ReservaService;



public class Main {
    public static void main(String[] args) {

        ReservaService reservaService = new ReservaService();
        AsignaturaService asignaturaService = new AsignaturaService();
        AulaService aulaService = new AulaService();
        InscripcionService inscripcionService = new InscripcionService();

        /*
        try {
            servicioAula.agregar(new Aula(101, 30, true, false));
            servicioAula.agregar(new Aula(102, 25, false, true));
            servicioAula.agregar(new Aula(103, 35, true, true));
            servicioAula.agregar(new Aula(104, 40, false, false));
            servicioAula.agregar(new Aula(105, 50, true, false));
            servicioAula.agregar(new Aula(106, 20, false, true));
            servicioAula.agregar(new Aula(107, 15, true, false));
            servicioAula.agregar(new Aula(108, 25, true, true));
            servicioAula.agregar(new Aula(109, 30, false, false));
            servicioAula.agregar(new Aula(110, 45, true, false));
            servicioAula.agregar(new Aula(111, 35, false, true));
            servicioAula.agregar(new Aula(112, 20, true, true));
            servicioAula.agregar(new Aula(113, 30, false, false));
            servicioAula.agregar(new Aula(114, 25, true, false));
            servicioAula.agregar(new Aula(115, 50, true, true));
            servicioAula.agregar(new Aula(116, 30, false, true));
            servicioAula.agregar(new Aula(117, 45, true, false));
            servicioAula.agregar(new Aula(118, 35, false, false));
            servicioAula.agregar(new Aula(119, 25, true, true));
            servicioAula.agregar(new Aula(120, 40, false, false));
            servicioAula.agregar(new Aula(121, 30, true, false));
            servicioAula.agregar(new Aula(122, 20, false, true));
            servicioAula.agregar(new Aula(123, 35, true, true));
            servicioAula.agregar(new Aula(124, 25, false, false));
            servicioAula.agregar(new Aula(125, 40, true, false));
            servicioAula.agregar(new Aula(126, 45, false, true));
            servicioAula.agregar(new Aula(127, 50, true, false));
            servicioAula.agregar(new Aula(128, 30, false, false));
            servicioAula.agregar(new Aula(129, 35, true, true));
            servicioAula.agregar(new Aula(130, 40, false, true));
        } catch (Exception e) {
            System.err.println("Error al agregar aula: " + e.getMessage());
        }


        List<Asignatura> asignaturas = List.of(
                new Asignatura("Matemática", 101, false),
                new Asignatura("Física", 102, true),
                new Asignatura("Química", 103, true),
                new Asignatura("Literatura", 104, false),
                new Asignatura("Historia", 105, false)
        );

        asignaturas.forEach(asignatura -> {
            try {
                servicioAsignatura.agregar(asignatura);
            } catch (Exception e) {
                System.out.println("Error al agregar asignatura: " + e.getMessage());
            }
        });

        // Crear y agregar Inscripciones
        List<Inscripcion> inscripciones = List.of(
                new Inscripcion(1, 20, 5, LocalDate.of(2024, 12, 15), asignaturas.get(0), "A1", Perfil.ESTUDIANTE),
                new Inscripcion(2, 25, 5, LocalDate.of(2024, 12, 20), asignaturas.get(1), "B1", Perfil.ESTUDIANTE),
                new Inscripcion(3, 30, 8, LocalDate.of(2024, 12, 10), asignaturas.get(2), "C1", Perfil.ESTUDIANTE),
                new Inscripcion(4, 15, 3, LocalDate.of(2024, 12, 25), asignaturas.get(3), "D1", Perfil.ESTUDIANTE),
                new Inscripcion(5, 18, 4, LocalDate.of(2024, 12, 18), asignaturas.get(4), "E1", Perfil.ESTUDIANTE)
        );

        inscripciones.forEach(inscripcion -> {
            try {
                servicioInscripcion.agregar(inscripcion);
            } catch (Exception e) {
                System.out.println("Error al agregar inscripción: " + e.getMessage());
            }
        });

        // Crear y agregar Reservas
        Aula aula1 = null;
        Aula aula2 = null;
        Aula aula3 = null;
        Aula aula4 = null;
        Aula aula5 = null;

        try{
            aula1 = servicioAula.obtener(111);
            aula2 = servicioAula.obtener(104);
            aula3 = servicioAula.obtener(101);
            aula4 = servicioAula.obtener(102);
            aula5 = servicioAula.obtener(103);
        }catch (Exception e){
            System.out.println("Error al obtener el aula: " + e.getMessage());
        }

        List<Reserva> reservas = List.of(
                new Reserva(LocalDate.of(2024, 10, 20), LocalDate.of(2024, 11, 15), BloqueHorario.MANIANA_PRIMER_BLOQUE, aula1, inscripciones.get(0)),
                new Reserva(LocalDate.of(2024, 10, 25), LocalDate.of(2024, 11, 20), BloqueHorario.TARDE_PRIMER_BLOQUE, aula2, inscripciones.get(1)),
                new Reserva(LocalDate.of(2024, 10, 22), LocalDate.of(2024, 11, 10), BloqueHorario.NOCHE_PRIMER_BLOQUE, aula3, inscripciones.get(2)),
                new Reserva(LocalDate.of(2024, 11, 5), LocalDate.of(2024, 11, 18), BloqueHorario.MANIANA_SEGUNDO_BLOQUE, aula4, inscripciones.get(3)),
                new Reserva(LocalDate.of(2024, 11, 10), LocalDate.of(2024, 11, 30), BloqueHorario.TARDE_SEGUNDO_BLOQUE, aula5, inscripciones.get(4))
        );

        reservas.forEach(reserva -> {
            try {
                servicioReserva.agregar(reserva);
            } catch (Exception e) {
                System.out.println("Error al agregar reserva: " + e.getMessage());
            }
        });



        List<Aula> lista = aulaService.listarAulasDisponibles(
                LocalDate.of(2024, 3, 10),
                LocalDate.of(2024, 12, 25),
                BloqueHorario.MANIANA_PRIMER_BLOQUE);

        System.out.println(lista);
         */
    }
}