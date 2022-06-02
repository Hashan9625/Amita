package com.example.amita.OnBoarding.Views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chaquo.python.Python;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.chaquo.python.PyObject;
import com.chaquo.python.android.AndroidPlatform;
import com.example.amita.OnBoarding.Interactor.OnBoardingInteractor;
import com.example.amita.OnBoarding.Views.Adapter.AdapterCategoryList;
import com.example.amita.OnBoarding.Views.Common.Common;
import com.example.amita.R;
import com.example.amita.ml.ModelEmotion;
import com.google.common.util.concurrent.ListenableFuture;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

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

    // Images
    int imageSize = 224;
    String imageString = "";
    private int emotion = 0;
    String[] emotionArray = {"", "happy", "sad", "neutral", "fear"};

    // RecyclerView
    private final Handler handler = new Handler(Looper.getMainLooper());
    private AdapterCategoryList adapterCategoryList;
    private ArrayList<String> arrayList = new ArrayList<>();

    //camera x
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private PreviewView previewView;
    private ImageCapture imgeCapture;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        animation = findViewById(R.id.ani);
        spokeText = findViewById(R.id.speakedText);
        response = findViewById(R.id.response);
        emotionText = findViewById(R.id.emotionText);
        previewView = findViewById(R.id.previewView);

        //RecyclerView for select model
        adapterCategoryList = new AdapterCategoryList(this, arrayList);
        RecyclerView recyclerViewDiscoverGoalCategory = findViewById(R.id.model_category);
        LinearLayoutManager layoutManagerHorizontal = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewDiscoverGoalCategory.setLayoutManager(layoutManagerHorizontal);
        recyclerViewDiscoverGoalCategory.setItemViewCacheSize(11);
        recyclerViewDiscoverGoalCategory.setAdapter(adapterCategoryList);
        showRecyclerView();


        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
        //start python
        py = Python.getInstance();
        ai = py.getModule("ai");
        aiWhatsAppChat = py.getModule("aiWhatsAppChat");
        emotionDetectionInVoice = py.getModule("emotionDetectionInVoice");

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {

                    int lang = textToSpeech.setLanguage(Locale.ENGLISH);

                    textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onDone(String utteranceId) {
                            animation.setImageResource(R.drawable.animation_normal);
                        }

                        @Override
                        public void onError(String utteranceId) {
                        }

                        @Override
                        public void onStart(String utteranceId) {
                            animation.setImageResource(R.drawable.animation_specking);
                        }
                    });
                }
            }

        });

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() ->{
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                startCameraX(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, getExecutor());

        findViewById(R.id.ask).setOnClickListener(this);
        findViewById(R.id.emotionDetect).setOnClickListener(this);
    }

    private Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }

    private void startCameraX(ProcessCameraProvider cameraProvider) {
        cameraProvider.unbindAll();

        // camera selector use case
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();

        // preview use case
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        // Image capture  use case
        imgeCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();

        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imgeCapture);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ask:
                    promptSpeechInput();
                break;
            case R.id.emotionDetect:
//                    emotionDetection();
                    emotionDetectionChaquopy();
                break;
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void showRecyclerView() {
        handler.post(() -> {
            arrayList.addAll(Common.models);
            adapterCategoryList.notifyDataSetChanged();
            arrayList = new ArrayList<>();
        });
    }

    public void showError(String message) {
        handler.post(() -> {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage(message)
                    .setPositiveButton("OK", (dialogInterface, i) -> {

                    })
                    .show();
        });
    }

    private void emotionDetectionChaquopy() {
        // call python
        Bitmap image = previewView.getBitmap();
        assert image != null;

        imageString = getStringImage(image);
        emotionDetectionPy =  py.getModule("emotionDetection");
        PyObject main = emotionDetectionPy.callAttr("main",imageString);
        String responseText = main.toString();
        Log.d(ContentValues.TAG,"emotionDetection-------------------------------------------- "+responseText);
    }

    private void emotionDetection() {
        Bitmap image = previewView.getBitmap();
        assert image != null;

        int dimension = image.getWidth();
        image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);

        image = Bitmap.createScaledBitmap(image, imageSize, imageSize, true);

        try {
            ModelEmotion model = ModelEmotion.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);

            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize *3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

            int pixel = 0;
            for (int i = 0; i < imageSize; i++) {
                for (int j = 0; j<imageSize; j++) {
                    int val = intValues[pixel++];
                    byteBuffer.putFloat(((val >> 16 ) & 0xFF) * (1.f / 255));
                    byteBuffer.putFloat(((val >> 8 ) & 0xFF) * (1.f / 255));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            ModelEmotion.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();

            // find biggest confidence
            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidences.length; i++) {
                if(confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i];
                    maxPos=i;
                }
            }

            String[] emotions = {"angry", "disgust", "fear", "happy", "sad", "surprise", "neutral"};
            emotionText.setText(emotions[maxPos]);

            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            showError(e.getMessage());
        }
    }

    private String getStringImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);

        byte[] imageByte = baos.toByteArray();

        return Base64.encodeToString(imageByte, Base64.DEFAULT);
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

        try {

            switch (requestCode) {
                case REQ_CODE_SPEECH_INPUT: {
                    if (resultCode == RESULT_OK && null != data) {

                        ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        spokeText.setText(result.get(0));
                        que = result.get(0);

                        PyObject emotionOutput = emotionDetectionInVoice.callAttr("main",que);
                        String emotionOutputString = emotionOutput.toString();
                        Log.d(ContentValues.TAG,"OnBoardingView.java-------------------------------------------- voice emotion output--- > "+emotionOutputString);

                        PyObject verbalResponse;
                        if(Common.model_index == 0) {
                            verbalResponse = ai.callAttr("main",que,emotionOutputString);
                        } else if(Common.model_index == 1) {
                            verbalResponse = aiWhatsAppChat.callAttr("main",que,emotionOutputString+"//");
                        } else {
                            verbalResponse = ai.callAttr("main",que,emotionOutputString);
                        }

                        String responseText = verbalResponse.toString();
                        String toDisplay = responseText.split("/")[0];
                        String toSpeech = responseText.split("/")[1];

                        // display reply
                        response.setText(toDisplay);
                        // display emotion
                        if(emotionOutputString.equals("happy")) {
                            emotionText.setText("\uD83D\uDE42");
                        } else if(emotionOutputString.equals("sad")) {
                            emotionText.setText("\uD83D\uDE1E");
                        } else {
                            emotionText.setText(emotionOutputString);
                        }

                        //Speech
                        textToSpeech(toSpeech);
                    }
                    break;
                }
            }

        } catch (Exception e) {
            showError(e.getMessage());
            Log.d(ContentValues.TAG,"OnBoardingView.java-------------------------------------------- error "+e.getMessage());
        }
    }

    public void textToSpeech(String responseText) {
        class Class_textToSpeech implements Runnable {
            @Override
            public void run() {

                HashMap<String, String> myHashAlarm = new HashMap<String, String>();
                myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_ALARM));
                myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "SOME MESSAGE");
                textToSpeech.speak(responseText,TextToSpeech.QUEUE_FLUSH,myHashAlarm);
            }
        }
        Class_textToSpeech class_textToSpeech = new Class_textToSpeech();
        new Thread(class_textToSpeech).start();
    }

}