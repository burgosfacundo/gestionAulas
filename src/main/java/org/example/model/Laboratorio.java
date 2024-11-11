package org.example.model;

public class Laboratorio extends Aula {
    private int computadoras;

    public Laboratorio(Integer id, int numero, int capacidad, boolean tieneProyector, boolean tieneTV,int computadoras) {
        super(id, numero, capacidad, tieneProyector, tieneTV);
        this.computadoras = computadoras;
    }

    public int getComputadoras() {
        return computadoras;
    }

    public void setComputadoras(int computadoras) {
        this.computadoras = computadoras;
    }

    /**
     * Sobre escritura de método de clase padre Aula
     * @param aula con la nueva información
     */
    @Override
    public void actualizar(Aula aula){
        //Llamo a método de aula
        super.actualizar(aula);

        //Verifico que sea una instancia de Laboratorio
        if (aula instanceof Laboratorio){
            //Modifico la cantidad de computadoras
            this.computadoras = ((Laboratorio) aula ).getComputadoras();
        }
    }

    @Override
    public String toString() {
        return String.format(
                """
                
                    Laboratorio (ID: %d)
                    Número: %d
                    Capacidad: %d
                    Tiene Proyector: %s
                    Tiene TV: %s
                    Computadoras: %d
                """,
                super.getId(),
                super.getNumero(),
                super.getCapacidad(),
                super.isTieneProyector() ? "Sí" : "No",
                super.isTieneTV() ? "Sí" : "No",
                this.getComputadoras()
        );
    }


}
