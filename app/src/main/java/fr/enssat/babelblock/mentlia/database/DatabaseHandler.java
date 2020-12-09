package fr.enssat.babelblock.mentlia.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {
    public static final String ELEMENT_KEY = "id";
    public static final String ELEMENT_BLOC = "bloc";
    public static final String ELEMENT_SERIE = "serie";
    public static final String ELEMENT_TABLE_NAME = "element";
    public static final String ELEMENT_TABLE_CREATE =
            "CREATE TABLE " + ELEMENT_TABLE_NAME + " (" +
                    ELEMENT_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ELEMENT_BLOC + " TEXT, " +
                    ELEMENT_SERIE + " INTEGER);";
    public static final String METIER_TABLE_DROP = "DROP TABLE IF EXISTS " + ELEMENT_TABLE_NAME + ";";


    public static final String SERIE_KEY = "id";
    public static final String SERIE_NAME = "nom";
    public static final String SERIE_FAVORITES = "favoris";
    public static final String SERIE_TABLE_NAME = "serie";
    public static final String SERIE_TABLE_CREATE =
            "CREATE TABLE " + SERIE_TABLE_NAME + " (" +
                    SERIE_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SERIE_NAME + " TEXT, " +
                    SERIE_FAVORITES + " INTEGER);";
    public static final String SERIE_TABLE_DROP = "DROP TABLE IF EXISTS " + SERIE_TABLE_NAME + ";";

    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ELEMENT_TABLE_CREATE);
        db.execSQL(SERIE_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(METIER_TABLE_DROP);
        db.execSQL(SERIE_TABLE_DROP);
        onCreate(db);
    }

}

