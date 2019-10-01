package mauricio.com.tutorgeometrico.activities;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import mauricio.com.tutorgeometrico.R;
import mauricio.com.tutorgeometrico.data.DatabaseDescription.FigureDesc;


public class EditFigureActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static Uri editFigureUri;

    private FloatingActionButton saveFigure;
    private TextInputLayout codeInputLayout;
    private TextInputLayout nameInputLayout;
    private TextInputLayout descInputLayout;
    private TextInputLayout othersInputLayout;


    private static final int EDIT_FIGURE_LOADER = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.admin_new_figure_fragment);

        saveFigure = findViewById(R.id.admin_edit_figure_fab_save);
        saveFigure.setVisibility(View.VISIBLE);

        codeInputLayout = findViewById(R.id.admin_new_figure_form_code);
        nameInputLayout = findViewById(R.id.admin_new_figure_form_name);
        descInputLayout = findViewById(R.id.admin_new_figure_form_description);
        othersInputLayout = findViewById(R.id.admin_new_figure_form_others);

        editFigureUri = getIntent().getParcelableExtra(MainActivity.FIGURE_KEY);

        saveFigure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int updatedRows = getContentResolver().update(editFigureUri, getFigureFromForm(), null, null);

                if (updatedRows > 0){
                    Toast.makeText(getApplicationContext(), R.string.updated_figure, Toast.LENGTH_LONG)
                            .show();
                }else{
                    Toast.makeText(getApplicationContext(), R.string.not_updated_figure, Toast.LENGTH_LONG)
                            .show();
                }

                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (editFigureUri != null){

            getSupportLoaderManager().initLoader(EDIT_FIGURE_LOADER, null, this);
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id){
            case EDIT_FIGURE_LOADER:

                return new CursorLoader(this,
                        editFigureUri,
                        null,
                        null,
                        null,
                        null);

            default:
                return null;

        }
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data != null && data.moveToFirst()){

            int codeIndex = data.getColumnIndex(FigureDesc.COLUMN_CODE);
            int nameIndex = data.getColumnIndex(FigureDesc.COLUMN_NAME);
            int descIndex = data.getColumnIndex(FigureDesc.COLUMN_DESCRIPTION);
            int othersIndex = data.getColumnIndex(FigureDesc.COLUMN_OTHERS);


            codeInputLayout.getEditText().setText(data.getString(codeIndex));
            nameInputLayout.getEditText().setText(data.getString(nameIndex));
            descInputLayout.getEditText().setText(data.getString(descIndex));
            othersInputLayout.getEditText().setText(data.getString(othersIndex));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }

    public ContentValues getFigureFromForm(){

        String code = codeInputLayout.getEditText().getText().toString().toLowerCase();
        String name = nameInputLayout.getEditText().getText().toString();
        String desc = descInputLayout.getEditText().getText().toString();
        String others = othersInputLayout.getEditText().getText().toString();

        ContentValues contentValues = new ContentValues();

        contentValues.put(FigureDesc.COLUMN_CODE, code);
        contentValues.put(FigureDesc.COLUMN_NAME, name);
        contentValues.put(FigureDesc.COLUMN_DESCRIPTION, desc);
        contentValues.put(FigureDesc.COLUMN_OTHERS, others);

        return contentValues;
    }
}
