package mauricio.com.tutorgeometrico.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import mauricio.com.tutorgeometrico.R;

public class FiguresContentProvider extends ContentProvider {

    private FiguresDatabaseHelper dbHelper;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int ONE_FIGURE = 1;
    private static final int FIGURES = 2;

    static {

        sUriMatcher.addURI(DatabaseDescription.AUTHORITY,
                DatabaseDescription.FigureDesc.TABLE_NAME + "/#", ONE_FIGURE);

        sUriMatcher.addURI(DatabaseDescription.AUTHORITY,
                DatabaseDescription.FigureDesc.TABLE_NAME, FIGURES);

    }

    @Override
    public boolean onCreate() {

        dbHelper = new FiguresDatabaseHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        queryBuilder.setTables(DatabaseDescription.FigureDesc.TABLE_NAME);

        switch (sUriMatcher.match(uri)){

            case ONE_FIGURE:

                    queryBuilder.appendWhere(
                            DatabaseDescription.FigureDesc._ID + " = " + uri.getLastPathSegment()
                    );

                break;

            case FIGURES:
                break;

            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.invalid_query_uri) + ": " + uri);

        }

        Cursor cursor = queryBuilder.query(dbHelper.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;

    }


    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        Uri newFigureUri = null;

        switch (sUriMatcher.match(uri)){

            case FIGURES:

                long rowId = dbHelper.getWritableDatabase().
                        insert(DatabaseDescription.FigureDesc.TABLE_NAME, null, values);

                if (rowId > 0){

                    newFigureUri = DatabaseDescription.FigureDesc.buildFigureUri(rowId);

                    getContext().getContentResolver().notifyChange(uri, null);

                } else{

                    throw new UnsupportedOperationException(
                            getContext().getString(R.string.invalid_insert_uri) + ": " + uri);
                }

                break;

            default:

                throw new UnsupportedOperationException(
                        getContext().getString(R.string.invalid_insert_uri) + ": " + uri);

        }

        return newFigureUri;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        int numRowsUpdated;

        switch (sUriMatcher.match(uri)){

            case ONE_FIGURE:

                String id = uri.getLastPathSegment();

                numRowsUpdated = dbHelper.getWritableDatabase().
                        update(DatabaseDescription.FigureDesc.TABLE_NAME, values,
                                DatabaseDescription.FigureDesc._ID + "=" + id, selectionArgs);

                break;

        default:
            throw new UnsupportedOperationException(
                    getContext().getString(R.string.invalid_update_uri) + ": " + uri);

        }

        if (numRowsUpdated != 0){

            getContext().getContentResolver().notifyChange(uri, null);

        }

        return numRowsUpdated;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        int numRowsDeleted;

        switch (sUriMatcher.match(uri)){

            case ONE_FIGURE:

                String id = uri.getLastPathSegment();

                numRowsDeleted = dbHelper.getWritableDatabase().
                        delete(DatabaseDescription.FigureDesc.TABLE_NAME,
                                DatabaseDescription.FigureDesc._ID + "=" + id, selectionArgs);

                break;

            default:
                throw new UnsupportedOperationException(
                        getContext().getString(R.string.invalid_delete_uri) + ": " + uri);

        }

        if (numRowsDeleted != 0){

            getContext().getContentResolver().notifyChange(uri, null);

        }

        return numRowsDeleted;
    }
}
