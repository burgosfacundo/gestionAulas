package gestores;

import adaptadores.AdaptadorLocalDate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import excepciones.EscrituraException;
import interfaces.JsonRepository;
import org.example.model.Reserva;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RepositorioReserva implements JsonRepository<Reserva> {
    private final String FILE_PATH = "reservas.json";
    private final Gson gson;

    public RepositorioReserva() {
        gson = new GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new AdaptadorLocalDate())
                .create();
    }

    public String getFILE_PATH() {
        return FILE_PATH;
    }

    @Override
    public void guardar(Reserva entidad) throws Exception {
        List<Reserva> lista;

        try(FileReader fr = new FileReader(FILE_PATH)) {
            lista = gson.fromJson(fr, new TypeToken<List<Reserva>>(){}.getType());
        }catch (FileNotFoundException e){
            lista = new ArrayList<>();
        }

        lista.add(entidad);

        try(FileWriter fw = new FileWriter(FILE_PATH)) {
            gson.toJson(lista, fw);
            fw.flush();
        }catch(IOException e){
            throw new EscrituraException("Error al guardar la asignatura.", e);

        }
    }

    @Override
    public Reserva leer(int id) throws Exception {

        List<Reserva> lista;

        try(FileReader fr = new FileReader(FILE_PATH)){
            lista = gson.fromJson(fr, new TypeToken<List<Reserva>>(){}.getType());
        }catch (FileNotFoundException e){
            System.err.println("No existe archivo del cual leer.");
            return null;
        }

        if(lista.isEmpty()){
            System.err.println("El archivo no tiene registros.");
            return null;
        }

        for(Reserva r : lista){
            if(r.getId() == id){
                return r;
            }
        }
        System.out.println("No se encontró la reserva con id " + id);
        return null;
    }

    @Override
    public void actualizar(Reserva entidad) throws IOException {
        List<Reserva> lista;

        try(FileReader fr = new FileReader(FILE_PATH)){
            lista = gson.fromJson(fr, new TypeToken<List<Reserva>>(){}.getType());
        }catch (IOException e){
            throw new FileNotFoundException("No se encontró el archivo " + FILE_PATH);
        }

        for (int i = 0; i < lista.size(); i++) {
            if(lista.get(i).getId() == entidad.getId()){
                lista.set(i, entidad);
                break;
            }
        }

        try(FileWriter fw = new FileWriter(FILE_PATH)){
            gson.toJson(lista, fw);
            fw.flush();
            System.out.println("Reserva actualizada.");
        }catch(IOException e){
            throw new IOException("Error al actualizar la reserva.", e);
        }
    }

    @Override
    public void eliminar(int id) throws Exception {
        List<Reserva> lista;
        try(FileReader fr = new FileReader(FILE_PATH)){
            lista = gson.fromJson(fr, new TypeToken<List<Reserva>>(){}.getType());
        }catch (FileNotFoundException e){
            System.err.println("No existe archivo del cual leer.");
            throw e;
        }

        lista.removeIf(r -> r.getId() == id);

        try(FileWriter fw = new FileWriter(FILE_PATH)){
            gson.toJson(lista, fw);
            fw.flush();
            System.out.println("Reserva eliminada.");
        }catch(IOException e){
            System.err.println("Error al eliminar la reserva. Error: " + e.getMessage());
        }
    }

    @Override
    public List<Reserva> listar() {
        try(FileReader fr = new FileReader(FILE_PATH)){
            return gson.fromJson(fr, new TypeToken<List<Reserva>>(){}.getType());
        }catch (IOException e){
            System.err.println("Error al listar la reserva. Error: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void clear() {
        List<Reserva> lista = new ArrayList<>();

        try(FileWriter fw = new FileWriter(FILE_PATH)){
            gson.toJson(lista, fw);
        }catch (IOException e){
            System.err.println("Error al cerrar el archivo. Error: " + e.getMessage());
        }
    }
}
