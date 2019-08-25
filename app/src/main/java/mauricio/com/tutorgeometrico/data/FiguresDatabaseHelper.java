package mauricio.com.tutorgeometrico.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import mauricio.com.tutorgeometrico.data.DatabaseDescription.FigureDesc;

public class FiguresDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "figures.db";
    private static  final int DATABASE_VERSION = 1;

    public FiguresDatabaseHelper(Context context){

        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_FIGURE_TABLE =
                "CREATE TABLE IF NOT EXISTS " + FigureDesc.TABLE_NAME + " ( " +
                        FigureDesc._ID + " INTEGER PRIMARY KEY, " +
                        FigureDesc.COLUMN_NAME  + " TEXT, " +
                        FigureDesc.COLUMN_DESCRIPTION + " TEXT, " +
                        FigureDesc.COLUMN_OTHERS + " TEXT, " +
                        FigureDesc.COLUMN_CODE + " TEXT, " +
                        FigureDesc.COLUMN_FIG_BYTES + " BLOB, " +
                        FigureDesc.COLUMN_DATE_CREATED + " TEXT, " +
                        FigureDesc.COLUMN_CREATED_BY + " INTEGER " + " ); ";

        db.execSQL(SQL_CREATE_FIGURE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
