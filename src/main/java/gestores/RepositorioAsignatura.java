package gestores;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import excepciones.AsignaturaNoEncontradaException;
import excepciones.EscrituraException;
import interfaces.JsonRepository;
import org.example.model.Asignatura;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RepositorioAsignatura implements JsonRepository<Asignatura> {
    private final String FILE_PATH = "asignaturas.json";
    private final Gson gson;

    public RepositorioAsignatura() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    public void guardar(Asignatura entidad) throws Exception {
        List<Asignatura> lista;

        try (FileReader fr = new FileReader(FILE_PATH)) {
            lista = gson.fromJson(fr, new TypeToken<List<Asignatura>>() {}.getType());
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
        } catch(IOException e) {
            System.err.println("Error al guardar la asignatura. Error: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Asignatura leer(int codigo) throws Exception {
        try (FileReader fr = new FileReader(FILE_PATH)) {
            List<Asignatura> lista = gson.fromJson(fr, new TypeToken<List<Asignatura>>() {}.getType());
            for (Asignatura a : lista) {
                if(a.getCodigo() == codigo){
                    return a;
                }
            }
        }
        throw new AsignaturaNoEncontradaException("No se encontró la asignatura con el código " + codigo);
    }

    @Override
    public void actualizar(Asignatura entidad) throws Exception {
        FileReader fr = new FileReader(FILE_PATH);
        boolean existe = false;
        List<Asignatura> lista = gson.fromJson(fr, new TypeToken<List<Asignatura>>() {}.getType());
        fr.close();

        for (int i = 0; i < lista.size(); i++) {
            if(lista.get(i).getCodigo() == entidad.getCodigo()) {
                lista.set(i, entidad);
                existe = true;
                break;
            }
        }

        if(!existe) throw new AsignaturaNoEncontradaException("No existe una asignatura con el código " + entidad.getCodigo());

        try(FileWriter fw = new FileWriter(FILE_PATH)){
            gson.toJson(lista, fw);
        } catch(EscrituraException e) {
            System.err.println("Error de escritura: " + e.getMessage());
        }
    }

    @Override
    public void eliminar(int codigo) throws Exception {
        FileReader fr = new FileReader(FILE_PATH);
        List<Asignatura> lista = gson.fromJson(fr, new TypeToken<List<Asignatura>>() {}.getType());
        fr.close();
        for (Asignatura a : lista) {
            if(a.getCodigo() == codigo){
                lista.remove(a);
            }
        }
        try(FileWriter fw = new FileWriter(FILE_PATH)){
            gson.toJson(lista, fw);
        }catch(EscrituraException e) {
            System.err.println("Error de escritura: " + e.getMessage());
        }
    }

    @Override
    public List<Asignatura> listar() {
        try(FileReader fr = new FileReader(FILE_PATH)){
            return gson.fromJson(fr, new TypeToken<List<Asignatura>>() {}.getType());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public void clear() {
        List<Asignatura> lista = new ArrayList<>();

        try (FileWriter fw = new FileWriter(FILE_PATH)) {
            gson.toJson(lista, fw);
        } catch (IOException e) {
            System.err.println("Error de IO: " + e.getMessage());
        }
    }
}
