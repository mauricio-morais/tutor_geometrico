package mauricio.com.tutorgeometrico.speech;

import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Locale;

public class Speaker {

    public static  final String LANG_TAG_PT_BR = "pt_BR";

    private static TextToSpeech tts;
    public static String lang;

    private Context mContext;

    public Speaker(@NonNull Context context, @Nullable String language){

        mContext = context;
        lang = (language != null) ? language : LANG_TAG_PT_BR;

        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

                tts.setLanguage(Locale.forLanguageTag(lang));
            }
        });
    }


    public void speakPhrase(@NonNull final String phrase){


        Bundle arguments = new Bundle();

        arguments.putString("engine", "com.svox.pico_foo");

        tts.speak(phrase, TextToSpeech.QUEUE_FLUSH, arguments, null);

    }

    public void stopSpeaker(){

        tts.stop();
    }
}
