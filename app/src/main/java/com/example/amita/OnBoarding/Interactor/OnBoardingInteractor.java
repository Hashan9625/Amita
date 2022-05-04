package com.example.amita.OnBoarding.Interactor;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.amita.OnBoarding.Views.OnBoardingView;

import java.lang.ref.WeakReference;

public class OnBoardingInteractor {
    OnBoardingView view;
 

    public OnBoardingInteractor(OnBoardingView context) {
        WeakReference<OnBoardingView> weakReference = new WeakReference<>(context);
        this.view = weakReference.get();
    }

    public void getResponses(PyObject main) {
    }

    public void emotionDetection() {
    }
}
