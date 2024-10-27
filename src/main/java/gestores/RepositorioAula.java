package gestores;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import excepciones.AulaNoEncontradaException;
import excepciones.EscrituraException;
import interfaces.JsonRepository;
import model.Inscripcion;
import org.example.model.Aula;
import org.example.model.Reserva;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RepositorioAula implements JsonRepository<Aula> {
    private final String FILE_PATH = "aulas.json";
    private final Gson gson;

    public RepositorioAula() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public String getFILE_PATH() {
        return FILE_PATH;
    }

    @Override
    public void guardar(Aula entidad) throws Exception {
        List<Aula> lista;

        try (FileReader fr = new FileReader(FILE_PATH)) {
            lista = gson.fromJson(fr, new TypeToken<List<Aula>>() {}.getType());
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
        }catch(IOException e){
            throw new EscrituraException("Error al guardar la asignatura.", e);

        }
    }

    @Override
    public Aula leer(int id) throws Exception {
        try (FileReader fr = new FileReader(FILE_PATH)) {
            List<Aula> lista = gson.fromJson(fr, new TypeToken<List<Aula>>() {}.getType());
            for (Aula a : lista) {
                if(a.getNumero() == id){
                    return a;
                }
            }
        }
        throw new AulaNoEncontradaException("No se encontró el aula de número " + id, id);
    }

    @Override
    public void actualizar(Aula entidad) throws IOException {
        List<Aula> lista;

        try(FileReader fr = new FileReader(FILE_PATH)){
            lista = gson.fromJson(fr, new TypeToken<List<Aula>>(){}.getType());
        }catch (IOException e){
            throw new FileNotFoundException("No se encontró el archivo " + FILE_PATH);
        }

        for (int i = 0; i < lista.size(); i++) {
            if(lista.get(i).getNumero() == entidad.getNumero()){
                lista.set(i, entidad);
                break;
            }
        }

        try(FileWriter fw = new FileWriter(FILE_PATH)){
            gson.toJson(lista, fw);
            fw.flush();
            System.out.println("Aula actualizada.");
        }catch(IOException e){
            throw new IOException("Error al actualizar el aula.", e);
        }
    }

    @Override
    public void eliminar(int id) throws IOException {
        List<Aula> lista;
        try(FileReader fr = new FileReader(FILE_PATH)){
            lista = gson.fromJson(fr, new TypeToken<List<Aula>>() {}.getType());
        }catch (IOException e){
            throw new FileNotFoundException("No se encontró el archivo " + FILE_PATH);
        }

        lista.removeIf(aula -> aula.getNumero() == id);

        try (FileWriter fw = new FileWriter(FILE_PATH)) {
            gson.toJson(lista, fw);
        } catch (IOException e) {
            throw  new EscrituraException("Error de escritura.", e);
        }
    }

    @Override
    public List<Aula> listar() {
        try(FileReader fr = new FileReader(FILE_PATH)){
            return gson.fromJson(fr, new TypeToken<List<Aula>>() {}.getType());
        }catch (Exception e){
            return new ArrayList<>();
        }
    }

    @Override
    public void clear(){
        List<Aula> lista = new ArrayList<>();

        try(FileWriter fw = new FileWriter(FILE_PATH)) {
            gson.toJson(lista, fw);
        }catch (IOException e){
            System.err.println("Error de IO: " + e.getMessage());
        }
    }
}
