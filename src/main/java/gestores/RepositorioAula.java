package gestores;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import excepciones.AulaNoEncontradaException;
import excepciones.EscrituraException;
import interfaces.JsonRepository;
import org.example.model.Aula;


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
            System.err.println("Error al guardar el aula. Error: " + e.getMessage());
            throw e;
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
        throw new AulaNoEncontradaException("No se encontró el aula de número " + id);
    }

    @Override
    public void actualizar(Aula entidad) throws Exception {
        FileReader fr = new FileReader(FILE_PATH);
        boolean existe = false;
        List<Aula> lista = gson.fromJson(fr, new TypeToken<List<Aula>>() {}.getType());
        fr.close();

        for (int i = 0; i < lista.size(); i++) {
            if(lista.get(i).getNumero() == entidad.getNumero()){
                lista.set(i, entidad);
                existe = true;
                break;
            }
        }

        if(!existe) throw new AulaNoEncontradaException("No existe un aula de número " + entidad.getNumero());

        try(FileWriter fw = new FileWriter(FILE_PATH)){
            gson.toJson(lista, fw);
        }catch(EscrituraException e){
            System.err.println("Error de escritura: " + e.getMessage());
        }
    }

    @Override
    public void eliminar(int id) throws Exception {
        FileReader fr = new FileReader(FILE_PATH);
        List<Aula> lista = gson.fromJson(fr, new TypeToken<List<Aula>>() {}.getType());
        fr.close();
        for (Aula a : lista) {
            if(a.getNumero() == id){
                lista.remove(a);
            }
        }
        try(FileWriter fw = new FileWriter(FILE_PATH)){
            gson.toJson(lista, fw);
        }catch(EscrituraException e){
            System.err.println("Error de escritura: " + e.getMessage());
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
