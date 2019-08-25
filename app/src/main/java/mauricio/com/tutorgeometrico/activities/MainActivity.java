package mauricio.com.tutorgeometrico.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.Locale;

import mauricio.com.tutorgeometrico.R;
import mauricio.com.tutorgeometrico.adapters.FigureRVAdapter;
import mauricio.com.tutorgeometrico.data.DatabaseDescription.FigureDesc;
import mauricio.com.tutorgeometrico.interfaces.FigureClickListener;
import mauricio.com.tutorgeometrico.interfaces.FigureLongClickListener;


public class MainActivity extends AppCompatActivity implements
        ActionMode.Callback, LoaderManager.LoaderCallbacks<Cursor> {

    private ActionMode mActionMode = null;
    private ActionBar actionBar = null;
    private Toolbar toolbar = null;
    private FloatingActionButton fabNewFigure = null;
    private RecyclerView rvFigures;
    private LinearLayoutCompat panelEmpty;

    private TextToSpeech tts;
    private FigureRVAdapter rvAdapter;

    private static final int FIGURE_LOADER = 0;

    public static  final String FIGURE_KEY = "figure_uri";
    public static  final String DEFAULT_LANG = "pt_BR";

    private Uri deleteFigureUri = null;
    private Uri uriSelectedFigure;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_main);

        toolbar = findViewById(R.id.action_bar_admin);
        rvFigures = findViewById(R.id.admin_main_rv_figures);
        fabNewFigure = findViewById(R.id.admin_btn_new_figure);
        panelEmpty = findViewById(R.id.admin_main_panel_data_empty);

        rvFigures.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        rvFigures.setHasFixedSize(true);

        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);


        FigureClickListener clickListener = new FigureClickListener() {
            @Override
            public void onClick(Uri figureUri) {

                Intent intentToEditFigure = new Intent(MainActivity.this, EditFigureActivity.class);
                intentToEditFigure.putExtra(FIGURE_KEY, figureUri);

                startActivity(intentToEditFigure);
            }
        };

        FigureLongClickListener longClickListener = new FigureLongClickListener() {
            @Override
            public boolean onLongClick(Uri uri) {

                deleteFigureUri = uri;

                if (mActionMode != null)
                    return false;

                mActionMode = startSupportActionMode(MainActivity.this);

                return  true;
            }
        };

        // When is clicked on item of the RecyclerView (Edite mode)
        rvAdapter = new FigureRVAdapter(clickListener, longClickListener);

        // When is clicked on new figure button (New figure mode)
        fabNewFigure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, NewFigureActivity.class));
            }
        });

        rvFigures.setAdapter(rvAdapter);
    }


    @Override
    protected void onPause() {

        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        rvAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {

        MenuInflater inflater = mode.getMenuInflater();

        inflater.inflate(R.menu.action_bar_admin_menu, menu);

        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case R.id.admin_menu_btn_delete:

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.Dialog);

                builder = builder.setMessage(R.string.confirm_deletion);
                builder = builder.setPositiveButton(R.string.user_btn_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        getContentResolver().delete(deleteFigureUri, null, null);
                        rvAdapter.selectedItemPosition = -1;
                        rvAdapter.notifyDataSetChanged();

                    }});

                builder.setNegativeButton(R.string.user_btn_no, null);

                builder.create()
                        .show();

                return true;
        }

        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

        mActionMode = null;

        rvAdapter.selectedItemPosition = -1;
        rvAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onStart() {

        super.onStart();

        getSupportLoaderManager().initLoader(FIGURE_LOADER, null, this);


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        CursorLoader cursorLoader;

        switch (id){

            case FIGURE_LOADER:
                cursorLoader = new CursorLoader(getApplicationContext(),
                        FigureDesc.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
                break;

            default:

                cursorLoader = null;

                break;
        }

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        rvAdapter.swapCursor(data);
        showEmptyPanelIfDataEmpty();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        rvAdapter.swapCursor(null);

    }

    public void showEmptyPanelIfDataEmpty(){

        if (rvAdapter.getItemCount() == 0){

            panelEmpty.setVisibility(View.VISIBLE);

        } else{

            panelEmpty.setVisibility(View.GONE);
        }
    }

}
