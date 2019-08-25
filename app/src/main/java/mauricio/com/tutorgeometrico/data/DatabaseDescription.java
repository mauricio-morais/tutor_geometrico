package mauricio.com.tutorgeometrico.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class DatabaseDescription {

    // ContentProvider's name
    public static final String AUTHORITY = "mauricio.com.tutorgeometrico.provider";

    private  static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Class for description of the figure's table
    public static final class FigureDesc implements BaseColumns {

        // Table name within database
        public static final String TABLE_NAME = "figures";

        // Uri for the figures table
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        // Column names
        public static final String COLUMN_CODE = "code_fig";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_OTHERS = "others";
        public static final String COLUMN_FIG_BYTES = "figure_bytes";
        public static final String COLUMN_DATE_CREATED = "created_date";
        public static final String COLUMN_CREATED_BY = "created_by";

        public static Uri buildFigureUri(long idFigure){
            return ContentUris.withAppendedId(CONTENT_URI, idFigure);
        }

    }
}
