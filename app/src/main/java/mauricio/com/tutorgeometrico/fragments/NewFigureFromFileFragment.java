package mauricio.com.tutorgeometrico.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import mauricio.com.tutorgeometrico.R;
import mauricio.com.tutorgeometrico.activities.NewFigureActivity;
import mauricio.com.tutorgeometrico.adapters.FigureRVAdapter;
import mauricio.com.tutorgeometrico.data.DatabaseDescription;
import mauricio.com.tutorgeometrico.models.Figure;


public class NewFigureFromFileFragment extends Fragment {

    private static final int CHOOSE_FILE = 1;

    // Columns index for each variable on excel file
    public static final  int FIGURE_NAME = 0;
    public static final  int FIGURE_DESC = 1;
    public static final  int FIGURE_CODE = 2;
    public static final  int FIGURE_OTHERS = 3;

    private AppCompatButton firstSelectXLSFile;
    private AppCompatButton secondSelectXLSFile;
    private AppCompatButton clearData;

    private RecyclerView rvFiguresFromxls;
    private FigureRVAdapter rvAdapter;
    private ConstraintLayout panelLoadedFile;
    private View.OnClickListener fileSelectListener;

    private List<Figure> figureList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.admin_new_figure_from_file_fragment, container, false);

        firstSelectXLSFile = view.findViewById(R.id.admin_new_figure_btn_select_file);
        secondSelectXLSFile = view.findViewById(R.id.admin_new_figure_btn_select_other_file);
        rvFiguresFromxls = view.findViewById(R.id.admin_new_figures_rv_figures_loaded);
        panelLoadedFile = view.findViewById(R.id.admin_new_figure_panel_loaded_file);
        clearData = view.findViewById(R.id.admin_new_figure_btn_reset_rv);

        rvFiguresFromxls.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFiguresFromxls.setHasFixedSize(true);

        rvAdapter = new FigureRVAdapter(figureList);
        rvFiguresFromxls.setAdapter(rvAdapter);

        fileSelectListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent chooseFile  = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
                chooseFile.setType(NewFigureActivity.DEFAULT_MIME_TYPE);

                Intent chooser = Intent.createChooser(chooseFile, getString(R.string.admin_new_figure_title_chooser));

                startActivityForResult(chooser, CHOOSE_FILE);

            }
        };

        firstSelectXLSFile.setOnClickListener(fileSelectListener);
        secondSelectXLSFile.setOnClickListener(fileSelectListener);

        clearData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                resetView();

                Snackbar.make(getView(), R.string.cleared_data, Snackbar.LENGTH_LONG)
                        .setAction("", null)
                        .show();
            }
        });

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK)
            return;

        switch (requestCode) {

            case CHOOSE_FILE:

                Uri uri = data.getData();

                String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());

                if (extension != null){

                    MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

                    String mimeType = mimeTypeMap.getMimeTypeFromExtension(extension);

                    if(NewFigureActivity.ACCEPTABLE_MIME_TYPES.contains(mimeType)){

                        try {

                            InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);

                            POIFSFileSystem  poifsFileSystem = new POIFSFileSystem(inputStream);

                            HSSFWorkbook workbook = new HSSFWorkbook(poifsFileSystem);
                            workbookToFigureList(workbook);
                            rvAdapter.notifyDataSetChanged();

                            toggleVisibilityPanel(true);

                            String loadedMsg = String.format(getString(R.string.loaded_count_figures), figureList.size());

                            Snackbar.make(getView(), loadedMsg, Snackbar.LENGTH_LONG)
                                    .setAction("", null)
                                    .show();

                        }catch(Exception exception){

                            Snackbar.make(getView(), R.string.runtime_error, Snackbar.LENGTH_LONG)
                                    .setAction("", null)
                                    .show();

                            Log.e(getString(R.string.TAG_TUTOR), exception.toString());
                        }

                    }else{

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Dialog);

                        builder.setMessage(R.string.dialog_msg_file_type_error)
                                .setTitle(R.string.dialog_title_error)
                                .setNeutralButton(R.string.button_neutral_name, null)
                                .show();
                    }
                }

                break;
        }
    }

    private void workbookToFigureList(@NonNull HSSFWorkbook workbook){

        this.figureList.clear();

        HSSFSheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIter = sheet.iterator();

        while (rowIter.hasNext()){

            Row row = rowIter.next();

            Iterator<Cell> cellsIter = row.cellIterator();

            Figure figure = new Figure();

            while (cellsIter.hasNext()){

                Cell cell = cellsIter.next();

                int columnIndex = cell.getColumnIndex();

                switch (columnIndex){

                    case FIGURE_CODE:

                        figure.setCode(cell.getStringCellValue());

                        break;

                    case FIGURE_NAME:

                        figure.setName(cell.getStringCellValue());

                        break;

                    case FIGURE_DESC:

                        figure.setDescription(cell.getStringCellValue());

                        break;

                    case FIGURE_OTHERS:

                        figure.setOthers(cell.getStringCellValue());

                        break;
                }
            }

            figureList.add(figure);
        }
    }

    private void toggleVisibilityPanel(final boolean visibility){

        if (visibility){

            panelLoadedFile.setVisibility(View.VISIBLE);
            firstSelectXLSFile.setVisibility(View.GONE);

        }else{

            panelLoadedFile.setVisibility(View.GONE);
            firstSelectXLSFile.setVisibility(View.VISIBLE);
        }
    }

    private void resetView(){

        toggleVisibilityPanel(false);
        rvAdapter.clearData();
    }

    public boolean isEmpty(){

        return (rvAdapter.getItemCount() == 0);
    }


    public ContentValues[] getFiguresFromList(){

        List<Figure> figures = rvAdapter.getFigures();

        ContentValues[] allContentValues = new ContentValues[figures.size()];

        for (int i = 0; i < figures.size(); i++){

            String code = figures.get(i).getCode().toLowerCase();
            String name = figures.get(i).getName();
            String desc = figures.get(i).getDescription();
            String others = figures.get(i).getOthers();

            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseDescription.FigureDesc.COLUMN_CODE, code);
            contentValues.put(DatabaseDescription.FigureDesc.COLUMN_NAME, name);
            contentValues.put(DatabaseDescription.FigureDesc.COLUMN_DESCRIPTION, desc);
            contentValues.put(DatabaseDescription.FigureDesc.COLUMN_OTHERS, others);
            contentValues.put(DatabaseDescription.FigureDesc.COLUMN_DATE_CREATED, Calendar.getInstance().getTime().toString());

            allContentValues[i] = contentValues;
        }

        return allContentValues;
    }

}
