package gestores;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import excepciones.AsignaturaNoEncontradaException;
import excepciones.EscrituraException;
import interfaces.JsonRepository;
import model.Inscripcion;
import org.example.model.Asignatura;
import org.example.model.Reserva;

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

    public String getFILE_PATH() {
        return FILE_PATH;
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
            throw new EscrituraException("Error al guardar la asignatura.", e);
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
        throw new AsignaturaNoEncontradaException("No se encontró la asignatura con el código " + codigo, codigo);
    }

    @Override
    public void actualizar(Asignatura entidad) throws IOException {
        List<Asignatura> lista;

        try(FileReader fr = new FileReader(FILE_PATH)){
            lista = gson.fromJson(fr, new TypeToken<List<Asignatura>>(){}.getType());
        }catch (IOException e){
            throw new FileNotFoundException("No se encontró el archivo " + FILE_PATH);
        }

        for (int i = 0; i < lista.size(); i++) {
            if(lista.get(i).getCodigo() == entidad.getCodigo()){
                lista.set(i, entidad);
                break;
            }
        }

        try(FileWriter fw = new FileWriter(FILE_PATH)){
            gson.toJson(lista, fw);
            fw.flush();
            System.out.println("Asignatura actualizada.");
        }catch(IOException e){
            throw new IOException("Error al actualizar la Asignatura.", e);
        }
    }

    @Override
    public void eliminar(int id) throws IOException {
        List<Asignatura> lista;
        try(FileReader fr = new FileReader(FILE_PATH)){
            lista = gson.fromJson(fr, new TypeToken<List<Asignatura>>() {}.getType());
        }catch (IOException e){
            throw new FileNotFoundException("No se encontró el archivo " + FILE_PATH);
        }

        lista.removeIf(asignatura -> asignatura.getCodigo() == id);

        try (FileWriter fw = new FileWriter(FILE_PATH)) {
            gson.toJson(lista, fw);
        } catch (IOException e) {
            throw  new EscrituraException("Error de escritura.", e);
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
