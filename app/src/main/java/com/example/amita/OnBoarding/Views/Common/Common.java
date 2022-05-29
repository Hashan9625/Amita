package com.example.amita.OnBoarding.Views.Common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class Common {
    public static HashMap currentItem = null;
    public static ArrayList<Integer> colorsMap = new ArrayList<>(Collections.singletonList(0xff494949));
    public static Boolean[] selectedPosition = new Boolean[] { true, false, false, false, false , false, false, false, false , false, false, false, false, false, false};
    public static int model_index = 0;
    public static ArrayList<String> models = new ArrayList<>(Arrays.asList("Created group data ", "WhatsApp Chat data"));
}
