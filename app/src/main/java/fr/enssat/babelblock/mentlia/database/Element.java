package fr.enssat.babelblock.mentlia.database;

public class Element {
    private long id;
    private String bloc;
    private int serie;

    public Element(long id, String bloc, int serie){
        super();
        this.id = id;
        this.bloc = bloc;
        this.serie = serie;
    }

    public long getId(){
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBloc() {
        return bloc;
    }

    public void setBloc(String bloc) {
        this.bloc = bloc;
    }

    public int getSerie() {
        return serie;
    }

    public void setSerie(int serie) {
        this.serie = serie;
    }
}
