package fr.enssat.babelblock.mentlia.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class SerieDAO extends DAOBase {
    public static final String KEY = "id";
    public static final String NAME = "nom";
    public static final String FAVORI = "favori";
    public static final String TABLE_NAME = "serie";

    public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + " (" + KEY +
            " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME + " TEXT, " + FAVORI + " INTEGER);";

    public SerieDAO(Context pContext) {
        super(pContext);
    }

    /**
     * @param s la série à ajouter à la base
     */
    public void ajouter(Serie s) {
        ContentValues value = new ContentValues();
        value.put(SerieDAO.NAME, s.getName());
        value.put(SerieDAO.FAVORI, s.getFavori());
        mDb.insert(SerieDAO.TABLE_NAME, null, value);
    }

    /**
     * @param id l'identifiant de la série à supprimer
     */
    public void supprimer(long id) {
        mDb.delete(TABLE_NAME, KEY + " = ?", new String[]{String.valueOf(id)});
    }

    /**
     * @param s la série modifié
     */
    public void modifier(Serie s) {
        ContentValues value = new ContentValues();
        value.put(FAVORI, s.getFavori());
        mDb.update(TABLE_NAME, value, KEY + " = ?", new String[]{String.valueOf(s.getId())});
    }

    /**
     * @param id l'identifiant de la série à récupérer
     */
    public Serie selectionner(long id) {
        Serie serie = null;
        Cursor cursor = mDb.rawQuery("select " + NAME + " from " + TABLE_NAME +
                " where serie > ?", new String[]{"1"});
        while (cursor.moveToNext()) {
            String name = cursor.getString(1);
            int favori = cursor.getInt(2);
            serie = new Serie(id, name, favori);
        }
        cursor.close();
        return serie;
    }
}
