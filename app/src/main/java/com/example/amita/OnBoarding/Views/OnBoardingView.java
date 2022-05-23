package com.example.amita.OnBoarding.Views;

import androidx.appcompat.app.AppCompatActivity;
import com.chaquo.python.Python;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.amita.OnBoarding.Interactor.OnBoardingInteractor;
import com.example.amita.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Locale;

public class OnBoardingView extends AppCompatActivity implements View.OnClickListener {
    OnBoardingInteractor interactor = new OnBoardingInteractor(this);
    pl.droidsonroids.gif.GifImageView animation;
    TextView spokeText;
    private TextView response, emotionText;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    public String que ;
    Python py;
    PyObject ai, aiWhatsAppChat;
    PyObject emotionDetectionInVoice;
    PyObject emotionDetectionPy;
    private TextToSpeech textToSpeech;
    ImageView detectImage;

    BitmapDrawable drawable;
    Bitmap bitmap;
    String imageString = "";
    private int emotion = 0;
    String[] emotionArray = {"", "happy", "sad", "neutral", "fear"};

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        animation = findViewById(R.id.ani);
        spokeText = findViewById(R.id.speakedText);
        response = findViewById(R.id.response);
        detectImage = findViewById(R.id.detectImage);
        emotionText = findViewById(R.id.emotionText);

        emotionText.setText(emotionArray[emotion]);

        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
        //start python
        py = Python.getInstance();
        ai = py.getModule("ai");
        aiWhatsAppChat = py.getModule("aiWhatsAppChat");
        emotionDetectionInVoice = py.getModule("emotionDetectionInVoice");

        textToSpeech = new TextToSpeech(getApplicationContext()
        , new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    int lang = textToSpeech.setLanguage(Locale.ENGLISH);
                }
            }
        });

        findViewById(R.id.ask).setOnClickListener(this);
        findViewById(R.id.emotion).setOnClickListener(this);
//        cameraProviderFu
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ask:
                    promptSpeechInput();
//                    emotionDetection();
                break;
            case R.id.emotion:
                if(emotion == 4)
                    emotion = 0;
                else
                    emotion++;

                emotionText.setText(emotionArray[emotion]);
                break;
        }
    }

    private void emotionDetection() {
        drawable = (BitmapDrawable)detectImage.getDrawable();
        bitmap = drawable.getBitmap();
        imageString = getStringImage(bitmap);

        emotionDetectionPy =  py.getModule("emotionDetection");
        PyObject main = emotionDetectionPy.callAttr("main",imageString);
        String responseText = main.toString();
        Log.d(ContentValues.TAG,"emotionDetection--------------------------------------------"+responseText);
        interactor.emotionDetection();
    }

    private String getStringImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);

        byte[] imageByte = baos.toByteArray();

        String encodedImage = android.util.Base64.encodeToString(imageByte, Base64.DEFAULT);
        return encodedImage;
    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    spokeText.setText(result.get(0));
                    que = result.get(0);

                    PyObject emotionOutput = emotionDetectionInVoice.callAttr("main",que);
                    String emotionOutputString = emotionOutput.toString();
                    Log.d(ContentValues.TAG,"OnBoardingView.java-------------------------------------------- ashan output--- > "+emotionOutputString);

//                    PyObject verbalResponse = ai.callAttr("main",que,emotion);
                    PyObject verbalResponse = aiWhatsAppChat.callAttr("main",que,"");
                    String responseText = verbalResponse.toString();
                    response.setText(responseText);
                    display(responseText);

                    textToSpeech.speak(responseText,TextToSpeech.QUEUE_FLUSH,null);
                }
                break;
            }
        }
    }

    private void display(String responseText){

        if(responseText.contains("tag> 01")) {
            animation.setImageResource(R.drawable.giphya);
        }else if(responseText.contains("tag> 02")) {
            animation.setImageResource(R.drawable.giphyhapy);
        }else if(responseText.contains("tag> 03")) {
            animation.setImageResource(R.drawable.giphyc);
        }else if(responseText.contains("tag> 04")) {
            animation.setImageResource(R.drawable.giphyd);
        }else if(responseText.contains("tag> 05")) {
            animation.setImageResource(R.drawable.giphysad);
        }else if(responseText.contains("tag> 06")) {
            animation.setImageResource(R.drawable.may);
        }else if(responseText.contains("tag> 07")) {
            animation.setImageResource(R.drawable.idiot);
        }else if(responseText.contains("tag> 08")) {
            animation.setImageResource(R.drawable.big);
        }else if(responseText.contains("tag> 09")) {
            animation.setImageResource(R.drawable.doyou);
        }else if(responseText.contains("tag> 10")) {
            animation.setImageResource(R.drawable.right);
        }else if(responseText.contains("tag> 11")) {
            animation.setImageResource(R.drawable.crazy);
        }else if(responseText.contains("tag> 12")) {
            animation.setImageResource(R.drawable.iwanna);
        }else if(responseText.contains("tag> 13")) {
            animation.setImageResource(R.drawable.may);
        }else if(responseText.contains("tag> 14")) {
            animation.setImageResource(R.drawable.areyou);
        }else if(responseText.contains("tag> 15")) {
            animation.setImageResource(R.drawable.beautiful);
        }else if(responseText.contains("tag> 16")) {
            animation.setImageResource(R.drawable.how);
        }else if(responseText.contains("tag> 17")) {
            animation.setImageResource(R.drawable.ok);
        }else if(responseText.contains("tag> 18")) {
            animation.setImageResource(R.drawable.what);
        } else {
            animation.setImageResource(R.drawable.ic_baseline_image_search_24);
        }

    }


}