package gestores;

import adaptadores.AdaptadorLocalDate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import excepciones.InscripcionNoEncontradaException;
import excepciones.EscrituraException;
import interfaces.JsonRepository;
import model.Inscripcion;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RepositorioInscripcion implements JsonRepository<Inscripcion> {
    private final String FILE_PATH = "inscripciones.json";
    private final Gson gson;

    public RepositorioInscripcion() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new AdaptadorLocalDate())
                .create();
    }

    @Override
    public void guardar(Inscripcion entidad) throws Exception {
        List<Inscripcion> lista;

        try (FileReader fr = new FileReader(FILE_PATH)) {
            lista = gson.fromJson(fr, new TypeToken<List<Inscripcion>>() {}.getType());
        } catch (FileNotFoundException e) {
            lista = new ArrayList<>();
        }

        if (lista == null) {
            lista = new ArrayList<>();
        }

        lista.add(entidad);

        try (FileWriter fw = new FileWriter(FILE_PATH)) {
            gson.toJson(lista, fw);
            fw.flush();
        } catch (IOException e) {
            System.err.println("Error al guardar la inscripción. Error: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Inscripcion leer(int id) throws Exception {
        try (FileReader fr = new FileReader(FILE_PATH)) {
            List<Inscripcion> lista = gson.fromJson(fr, new TypeToken<List<Inscripcion>>() {}.getType());
            for (Inscripcion inscripcion : lista) {
                if(inscripcion.getId() == id) {
                    return inscripcion;
                }
            }
        }
        throw new InscripcionNoEncontradaException("No se encontró la inscripción con el ID " + id);
    }

    @Override
    public void actualizar(Inscripcion entidad) throws Exception {
        FileReader fr = new FileReader(FILE_PATH);
        boolean existe = false;
        List<Inscripcion> lista = gson.fromJson(fr, new TypeToken<List<Inscripcion>>() {}.getType());
        fr.close();

        for (int i = 0; i < lista.size(); i++) {
            if(lista.get(i).getId() == entidad.getId()) {
                lista.set(i, entidad);
                existe = true;
                break;
            }
        }

        if(!existe) throw new InscripcionNoEncontradaException("No existe una inscripción con el ID " + entidad.getId());

        try (FileWriter fw = new FileWriter(FILE_PATH)) {
            gson.toJson(lista, fw);
        } catch (EscrituraException e) {
            System.err.println("Error de escritura: " + e.getMessage());
        }
    }

    @Override
    public void eliminar(int id) throws Exception {
        FileReader fr = new FileReader(FILE_PATH);
        List<Inscripcion> lista = gson.fromJson(fr, new TypeToken<List<Inscripcion>>() {}.getType());
        fr.close();

        lista.removeIf(inscripcion -> inscripcion.getId() == id);

        try (FileWriter fw = new FileWriter(FILE_PATH)) {
            gson.toJson(lista, fw);
        } catch (EscrituraException e) {
            System.err.println("Error de escritura: " + e.getMessage());
        }
    }

    @Override
    public List<Inscripcion> listar() {
        try (FileReader fr = new FileReader(FILE_PATH)) {
            return gson.fromJson(fr, new TypeToken<List<Inscripcion>>() {}.getType());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public void clear() {
        List<Inscripcion> lista = new ArrayList<>();

        try (FileWriter fw = new FileWriter(FILE_PATH)) {
            gson.toJson(lista, fw);
        } catch (IOException e) {
            System.err.println("Error de IO: " + e.getMessage());
        }
    }
}
