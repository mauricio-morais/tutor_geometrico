package mauricio.com.tutorgeometrico.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;


import mauricio.com.tutorgeometrico.R;
import mauricio.com.tutorgeometrico.data.DatabaseDescription.FigureDesc;
import mauricio.com.tutorgeometrico.models.Figure;

public class UserActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    public static final String LANG_TAG_PT_BR = "pt_BR";

    private static final int CHECK_DATA_TTS = 0;
    private static final int SPEECH_REQUEST_CODE = 1;
    private static final int SPEECH_REQUEST_OTHERS = 2;

    private TextToSpeech tts = null;
    private Figure currentFigure;

    private AppCompatButton btnPlaySpeech;
    private AppCompatButton btnCancel;
    private AppCompatButton btnYes;
    private AppCompatButton btnNo;

    private LinearLayoutCompat panelSpeechLoading;
    private ConstraintLayout panelSpeechCommand;
    private ConstraintLayout panelYesNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_activity);

        btnPlaySpeech = findViewById(R.id.user_btn_speak);
        btnCancel = findViewById(R.id.user_btn_cancel);
        btnYes = findViewById(R.id.user_btn_yes);
        btnNo = findViewById(R.id.user_btn_no);

        panelSpeechLoading = findViewById(R.id.user_panel_loading_speech_resources);
        panelSpeechCommand = findViewById(R.id.user_panel_speech_commands);
        panelYesNo = findViewById(R.id.user_panel_yes_no);

        Intent installDataIntent = new Intent();
        installDataIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(installDataIntent, CHECK_DATA_TTS);

        btnPlaySpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new GetTextBySpeech().execute(getString(R.string.speech_get_figure_code));
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(tts != null){
                    tts.stop();
                }
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toggleVisibilityPanelYesNo();

            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                speak(currentFigure.getOthers());
                toggleVisibilityPanelYesNo();

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){

            case CHECK_DATA_TTS:

                switch (resultCode){

                    case TextToSpeech.Engine.CHECK_VOICE_DATA_PASS:

                        tts = new TextToSpeech(this, this);

                         new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {

                                hideLoadingPanel();
                            }
                        }, 2000); // Wait two seconds to hide the loading panel

                        break;

                    case TextToSpeech.Engine.CHECK_VOICE_DATA_FAIL:

                        new InitializeSpeechEngine().execute();

                        break;
                }

                break;

            case SPEECH_REQUEST_CODE:

                if (resultCode == RESULT_OK){

                    List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    currentFigure = findFigure(results.get(0));

                    if (currentFigure != null){

                       new SpeakFigure().execute(currentFigure);

                    }else{

                        String toSpeak = String.format(getString(R.string.speech_get_figure_code_again), results.get(0));

                        new GetTextBySpeech().execute(toSpeak);
                    }
                }

                break;
        }
    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            // SpeakFigure the initial phrase
            speak(getString(R.string.initial_speaker_phrase));

            int result = tts.setLanguage(Locale.forLanguageTag(LANG_TAG_PT_BR));

        /*    if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            }*/

        } else {
            Toast.makeText(getApplicationContext(), R.string.fail_to_init_speech , Toast.LENGTH_LONG).show();
        }
    }


    private void speak(String text) {

        if (tts.isSpeaking())
            tts.stop();

        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String s) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(getApplicationContext(), "Started" + keyword, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onDone(String s) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(getApplicationContext(), "Done ", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String s) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(getApplicationContext(), "Error ", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        Bundle params = new Bundle();
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "");

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    private class SpeakToWaitResponse extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... strings) {

            speak(strings[0]);

            while (tts.isSpeaking())
                continue;

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            toggleVisibilityPanelYesNo();

        }
    }


    private class SpeakFigure extends AsyncTask<Figure, Void, Figure>{

        @Override
        protected Figure doInBackground(Figure... figures) {

            speak(figures[0].getDescription());

            while (tts.isSpeaking())
                continue;

            return figures[0];
        }

        @Override
        protected void onPostExecute(Figure figure) {

           new SpeakToWaitResponse().execute(getString(R.string.speech_others_info));
        }
    }


    private class GetTextBySpeech extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... strings) {

            speak(strings[0]);

            while (tts.isSpeaking())
                continue;

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            startSpeechRecognizer(SPEECH_REQUEST_CODE);
        }
    }

    private class InitializeSpeechEngine extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            Intent installIntent = new Intent();
            installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
            startActivity(installIntent);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            hideLoadingPanel();
            Toast.makeText(getApplicationContext(), R.string.loaded_resources, Toast.LENGTH_LONG).show();
        }
    }

    private void startSpeechRecognizer(int speechMode){

        Intent intent = new Intent();
        intent.setAction(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        startActivityForResult(intent, speechMode);

    }

    private void hideLoadingPanel(){

        panelSpeechLoading.setVisibility(View.GONE);
        panelSpeechCommand.setVisibility(View.VISIBLE);

    }

    private void toggleVisibilityPanelYesNo(){

        if(panelYesNo.getVisibility() == View.GONE){

            panelYesNo.setVisibility(View.VISIBLE);
            panelSpeechCommand.setVisibility(View.GONE);

        }else{
            panelYesNo.setVisibility(View.GONE);
            panelSpeechCommand.setVisibility(View.VISIBLE);
        }

    }


    private Figure findFigure(String code){

        Cursor cursor = getContentResolver().query(FigureDesc.CONTENT_URI,
                null,
                FigureDesc.COLUMN_CODE + " = ?",
                new String[]{code},
                null);

        if (cursor != null && cursor.getCount() > 0){

            cursor.moveToFirst();

            Figure figure = new Figure();

            int indexCode = cursor.getColumnIndex(FigureDesc.COLUMN_CODE);
            int indexName = cursor.getColumnIndex(FigureDesc.COLUMN_NAME);
            int indexDesc = cursor.getColumnIndex(FigureDesc.COLUMN_DESCRIPTION);
            int indexOthers = cursor.getColumnIndex(FigureDesc.COLUMN_OTHERS);

            figure.setCode(cursor.getString(indexCode));
            figure.setName(cursor.getString(indexName));
            figure.setDescription(cursor.getString(indexDesc));
            figure.setOthers(cursor.getString(indexOthers));

            return figure;
        }

        return null;
    }

}
