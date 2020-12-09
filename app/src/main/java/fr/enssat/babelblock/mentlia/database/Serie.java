package fr.enssat.babelblock.mentlia.database;

public class Serie {
    private long id;
    private String name;
    private int favori;

    public Serie(long id, String name, int favori) {
        super();
        this.id = id;
        this.name = name;
        this.favori = favori;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String nom) {
        this.name = nom;
    }

    public int getFavori() {
        return favori;
    }

    public void setFavori(int favori) {
        this.favori = favori;
    }
}
