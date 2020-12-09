package fr.enssat.babelblock.mentlia.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;

public class ElementDAO extends DAOBase {
    public static final String KEY = "id";
    public static final String BLOC = "bloc";
    public static final String SERIE = "serie";
    public static final String TABLE_NAME = "element";

    public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + " (" + KEY +
            " INTEGER PRIMARY KEY AUTOINCREMENT, " + BLOC + " TEXT, " + SERIE + " INTEGER);";

    public ElementDAO(Context pContext) {
        super(pContext);
    }

    /**
     * @param e l'element à ajouter à la base
     */
    public void ajouter(Element e) {
        ContentValues value = new ContentValues();
        value.put(ElementDAO.BLOC, e.getBloc());
        value.put(ElementDAO.SERIE, e.getSerie());
        mDb.insert(ElementDAO.TABLE_NAME, null, value);
    }

    /**
     * @param id l'identifiant de l'element à supprimer
     */
    public void supprimer(long id) {
        mDb.delete(TABLE_NAME, KEY + " = ?", new String[]{String.valueOf(id)});
    }

    /**
     * @param e l'element modifié
     */
    public void modifier(Element e) {
        ContentValues value = new ContentValues();
        value.put(SERIE, e.getSerie());
        mDb.update(TABLE_NAME, value, KEY + " = ?", new String[]{String.valueOf(e.getId())});
    }

    /**
     * @param id l'identifiant de l'element à récupérer
     */
    public Element selectionner(long id) {
        Element element = null;
        Cursor cursor = mDb.rawQuery("select " + BLOC + " from " + TABLE_NAME +
                " where serie > ?", new String[]{"1"});
        while (cursor.moveToNext()) {
            String bloc = cursor.getString(1);
            int serie = cursor.getInt(2);
            element = new Element (id, bloc, serie);
        }
        cursor.close();
        return element;
    }
}


