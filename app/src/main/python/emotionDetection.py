import cv2
import pickle
from tensorflow.keras.preprocessing import image
import warnings
warnings.filterwarnings("ignore")
from tensorflow.keras.preprocessing.image import load_img, img_to_array
from tensorflow.keras.models import  load_model
import numpy as np

import base64
from os.path import dirname, join
# from tensorflow import keras
import tensorflow as tf
from tensorflow import keras

def main(data):
    # get image
    # decoded_data = base64.b64decode(data)
    # np_data = np.fromstring(decoded_data, np.uint8)
    # test_img = cv2.imdecode(np_data, cv2.IMREAD_UNCHANGED)

    print(tf.__version__)

    # model = pickle.load(open(join(dirname(__file__),'EmotionDetectionModel/model.pkl'), 'rb'))
    #
    # face_haar_cascade = cv2.CascadeClassifier(join(dirname(__file__),'EmotionDetectionModel/haarcascade_frontalface_default.xml'))
    #
    # gray_img = cv2.cvtColor(test_img, cv2.COLOR_BGR2RGB)
    #
    # faces_detected = face_haar_cascade.detectMultiScale(gray_img, 1.32, 5) #detect the face
    # print(faces_detected)
    # # way that how to detect the face from objects
    # for (x, y, w, h) in faces_detected:
    #     cv2.rectangle(test_img, (x, y), (x + w, y + h), (255, 0, 0), thickness=7)
    #     roi_gray = gray_img[y:y + w, x:x + h]  # cropping region of interest i.e. face area from  image
    #     roi_gray = cv2.resize(roi_gray, (224, 224))
    #     img_pixels = image.img_to_array(roi_gray)
    #     img_pixels = np.expand_dims(img_pixels, axis=0)
    #     img_pixels /= 255
    #
    #     #model prediction
    #     predictions = model.predict(img_pixels)
    #
    #     # find max indexed array
    #     max_index = np.argmax(predictions[0])
    #
    #     emotions = ('angry', 'disgust', 'fear', 'happy', 'sad', 'surprise', 'neutral')
    #     predicted_emotion = emotions[max_index]
    #
    #     print(predicted_emotion)
    return "" #predicted_emotion
