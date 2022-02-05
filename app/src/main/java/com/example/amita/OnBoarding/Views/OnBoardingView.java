package com.example.amita.OnBoarding.Views;

import androidx.appcompat.app.AppCompatActivity;
import com.chaquo.python.Python;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.amita.OnBoarding.Interactor.OnBoardingInteractor;
import com.example.amita.R;

import java.util.ArrayList;
import java.util.Locale;

public class OnBoardingView extends AppCompatActivity {
    OnBoardingInteractor interactor = new OnBoardingInteractor(this);
    pl.droidsonroids.gif.GifImageView animation;
    TextView spokeText;
    TextView response;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    public String que ;
    Python py;
    PyObject ai;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        animation = (pl.droidsonroids.gif.GifImageView)findViewById(R.id.ani);
        spokeText = (TextView)findViewById(R.id.speakedText);
        response = (TextView)findViewById(R.id.response);

        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
        //start python
        py = Python.getInstance();
        ai = py.getModule("ai");
//        interactor.getResponses(main);


    }
    public void onClick(View view){
        promptSpeechInput();
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
                    display();

                    PyObject main = ai.callAttr("main",que,1);
                    response.setText(main.toString());
                }
                break;
            }

        }
    }

    private void display(){

        if(que.contains("hi"))
        {
            animation.setImageResource(R.drawable.giphya);
        }

        if(que.contains("hello"))
        {
            animation.setImageResource(R.drawable.giphyhapy);
        }
        if(que.contains("love"))
        {
            animation.setImageResource(R.drawable.giphyc);
        }
        if(que.contains("like"))
        {
            animation.setImageResource(R.drawable.giphyd);
        }
        if(que.contains("bad"))
        {
            animation.setImageResource(R.drawable.giphysad);
        }
        if(que.contains("may") || que.contains("ask"))
        {
            animation.setImageResource(R.drawable.may);
        }
        if(que.contains("idiot") || que.contains("donkey"))
        {
            animation.setImageResource(R.drawable.idiot);
        }
        if(que.contains("big") || que.contains("long"))
        {
            animation.setImageResource(R.drawable.big);
        }

        if(que.contains("do you") || que.contains("know"))
        {
            animation.setImageResource(R.drawable.doyou);
        }

        if(que.contains("right") || que.contains("correct"))
        {
            animation.setImageResource(R.drawable.right);
        }

        if(que.contains("crazy") || que.contains("funny"))
        {
            animation.setImageResource(R.drawable.crazy);
        }

        if(que.contains("wanna") || que.contains(" me "))
        {
            animation.setImageResource(R.drawable.iwanna);
        }

        if(que.contains("may") || que.contains("ask"))
        {
            animation.setImageResource(R.drawable.may);
        }

        if(que.contains("are you"))
        {
            animation.setImageResource(R.drawable.areyou);
        }

        if(que.contains("beautiful") || que.contains("pretty"))
        {
            animation.setImageResource(R.drawable.beautiful);
        }

        if(que.contains("how"))
        {
            animation.setImageResource(R.drawable.how);
        }

        if(que.contains("ok"))
        {
            animation.setImageResource(R.drawable.ok);
        }

        if(que.contains("what"))
        {
            animation.setImageResource(R.drawable.what);
        }

    }

}