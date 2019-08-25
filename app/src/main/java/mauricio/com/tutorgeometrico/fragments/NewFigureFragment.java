package mauricio.com.tutorgeometrico.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;

import mauricio.com.tutorgeometrico.R;
import mauricio.com.tutorgeometrico.data.DatabaseDescription.FigureDesc;


public class NewFigureFragment extends Fragment {

    public static final float IMAGE_NEW_WIDTH = 100;
    public static final float IMAGE_NEW_HEIGHT = 100;

    private static final  int CHOOSE_IMAGE = 1;

    private FloatingActionButton fabChooseImage;
    private TextInputLayout codeInputLayout;
    private TextInputLayout nameInputLayout;
    private TextInputLayout descInputLayout;
    private TextInputLayout othersInputLayout;


    private Bitmap selectedImage;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.admin_new_figure_fragment, container, false);

        fabChooseImage = view.findViewById(R.id.admin_new_figure_fab_choose_image);
        codeInputLayout = view.findViewById(R.id.admin_new_figure_form_code);
        nameInputLayout = view.findViewById(R.id.admin_new_figure_form_name);
        descInputLayout = view.findViewById(R.id.admin_new_figure_form_description);
        othersInputLayout = view.findViewById(R.id.admin_new_figure_form_others);

        fabChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent chooseImageIntent = new Intent();
                chooseImageIntent.setAction(Intent.ACTION_GET_CONTENT);
                chooseImageIntent.setType("image/*");

                startActivityForResult(Intent.createChooser(chooseImageIntent, getString(R.string.select_a_picture)), CHOOSE_IMAGE);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK)
            return;

        switch (requestCode){

            case CHOOSE_IMAGE:

                Snackbar.make(getView(), R.string.not_implemented, Snackbar.LENGTH_LONG)
                        .show();

                break;
        }

    }

    public ContentValues getFigureFromForm(){

        String code = codeInputLayout.getEditText().getText().toString();
        String name = nameInputLayout.getEditText().getText().toString();
        String desc = descInputLayout.getEditText().getText().toString();
        String others = othersInputLayout.getEditText().getText().toString();

        ContentValues contentValues = new ContentValues();

        contentValues.put(FigureDesc.COLUMN_CODE, code);
        contentValues.put(FigureDesc.COLUMN_NAME, name);
        contentValues.put(FigureDesc.COLUMN_DESCRIPTION, desc);
        contentValues.put(FigureDesc.COLUMN_OTHERS, others);
        contentValues.put(FigureDesc.COLUMN_DATE_CREATED, Calendar.getInstance().getTime().toString());

        return contentValues;
    }
}
