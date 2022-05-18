import numpy as np
import os
import cv2
from PIL import Image
import base64
import io
from os.path import dirname, join
import json
from tensorflow import keras
import tensorflow
from tensorflow.keras.preprocessing import image
import warnings
warnings.filterwarnings("ignore")
from tensorflow.keras.preprocessing.image import load_img, img_to_array
from tensorflow.keras.models import load_model
def main(data):
    decoded_data = base64.b64decode(data)
    np_data = np.fromstring(decoded_data, np.uint8)
    test_img = cv2.imdecode(np_data, cv2.IMREAD_UNCHANGED)

    # # load trained model
    # filename = join(dirname(__file__),'EmotionDetectionModel/facedetection.h5')
    # # model = load_model(filename)

    gray_img = cv2.cvtColor(test_img, cv2.COLOR_BGR2RGB)


    # faces_detected = face_haar_cascade.detectMultiScale(gray_img, 1.32, 5) #detect the face


    roi_gray = cv2.resize(gray_img, (224, 224))
    # img_pixels = image.img_to_array(roi_gray)
    # img_pixels = np.expand_dims(img_pixels, axis=0)
    # img_pixels /= 255

    i = img_to_array(roi_gray)/255
    input_arr = np.array([i])
    input_arr.shape

    print(input_arr)

    pred = np.argmax(model.predict(input_arr))

    #model prediction




    # find max indexed array
    # max_index = np.argmax(predictions[0])

    emotions = ('angry', 'disgust', 'fear', 'happy', 'sad', 'surprise', 'neutral')
    predicted_emotion = emotions[pred]

    return predicted_emotion
