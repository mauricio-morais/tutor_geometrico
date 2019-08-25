package mauricio.com.tutorgeometrico.activities;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import mauricio.com.tutorgeometrico.R;
import mauricio.com.tutorgeometrico.adapters.TabsAdapter;
import mauricio.com.tutorgeometrico.data.DatabaseDescription.FigureDesc;
import mauricio.com.tutorgeometrico.fragments.NewFigureFragment;
import mauricio.com.tutorgeometrico.fragments.NewFigureFromFileFragment;


public class NewFigureActivity extends AppCompatActivity {

    private static final int FIGURE_LOADER = 0;

    private static Uri figureEditUri;

    public static final List<String> ACCEPTABLE_MIME_TYPES =
            Arrays.asList("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                          "application/excel", "application/vnd.ms-excel", "application/x-excel",
                          "application/x-msexcel");

    public static final String DEFAULT_MIME_TYPE = "application/excel";

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TabsAdapter tabsAdapter;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private FloatingActionButton saveFigure;

    private  List<Fragment> fragments =
            Arrays.asList(new NewFigureFragment(), new NewFigureFromFileFragment());


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.admin_new_figure_activity);

        viewPager = findViewById(R.id.admin_new_figure_viewpager);
        tabLayout = findViewById(R.id.admin_new_figure_tablayout);
        toolbar = findViewById(R.id.admin_new_figure_actionbar);
        saveFigure = findViewById(R.id.admin_new_figure_fab_save);

        tabsAdapter = new TabsAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(tabsAdapter);

        tabLayout.setupWithViewPager(viewPager, true);
        setupTabs(); // Setup text and icon of the Tabs

        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        saveFigure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NewFigureFromFileFragment newFigureFromFileFragment = (NewFigureFromFileFragment) fragments.get(1);

                // Check if the list from file is empty
                if (newFigureFromFileFragment.isEmpty()){

                    NewFigureFragment newFigureFragment = (NewFigureFragment) fragments.get(0);

                    ContentValues contentValues = newFigureFragment.getFigureFromForm();

                    if (contentValues != null){

                        Uri newFigureUri = saveFigure(contentValues);

                        if(newFigureUri != null){

                            Toast.makeText(getApplicationContext(), R.string.figure_added, Toast.LENGTH_LONG).show();

                        }else{

                            Toast.makeText(getApplicationContext(), R.string.figure_not_added, Toast.LENGTH_LONG).show();
                        }
                    }

                // if the list is empty, then the form will be used
                }else{

                    int cont = 0;

                    ContentValues[] newFigures = newFigureFromFileFragment.getFiguresFromList();

                    for(ContentValues contentValues: newFigures){

                        Uri newFigureUri = saveFigure(contentValues);

                        if(newFigureUri != null)
                            cont++;
                    }

                    if (cont == newFigures.length){

                        Toast.makeText(getApplicationContext(), R.string.figures_added, Toast.LENGTH_LONG).show();

                    }else{

                        String msg = String.format(getString(R.string.some_figures_added), cont);

                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

                    }
                }

                finish();
            }
        });
    }

    private Uri saveFigure(ContentValues figure){

        Uri newFigureUri = getContentResolver().insert(FigureDesc.CONTENT_URI, figure);

        return  newFigureUri;
    }

    public void setupTabs(){

        TabLayout.Tab tabForm = tabLayout.getTabAt(0);
        TabLayout.Tab tabFromFile = tabLayout.getTabAt(1);

        tabForm.setText(R.string.admin_new_figure_tb_item_single);
        tabFromFile.setText(R.string.admin_new_figure_tb_item_multi);

        tabForm.setIcon(R.drawable.ic_mode_edit_24dp);
        tabFromFile.setIcon(R.drawable.ic_file_xls);
    }

}
