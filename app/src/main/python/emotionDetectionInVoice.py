import nltk
from nltk.stem.api import StemmerI
from nltk.stem.lancaster import LancasterStemmer
import numpy as np
import pickle
from os.path import dirname, join
import json
from tensorflow import keras
from tensorflow.keras.models import load_model
stemmer = nltk.stem.PorterStemmer()
lemmatizer = nltk.stem.WordNetLemmatizer()
import random
import tensorflow
from tensorflow.keras.models import load_model

import sys
import sklearn
from sklearn.metrics import accuracy_score
from sklearn.linear_model import LogisticRegression
import pandas as pd

nltk.download('stopwords')
nltk.download('wordnet')
nltk.download('punkt')
nltk.download('averaged_perceptron_tagger')
while not nltk.download('averaged_perceptron_tagger'):
    print("Retrying download - averaged_perceptron_tagger")

while not nltk.download('punkt'):
    print("Retrying download - punkt")

while not nltk.download('wordnet'):
    print("Retrying download - wordnet")

while not nltk.download('stopwords'):
    print("Retrying download - stopwords")

def main(sentence):
    return predict(sentence)

def predict(sentence):
    data = pickle.load(open(join(dirname(__file__),'EmotionDetectionInVoiceModel/data.pkl'), 'rb'))
    logreg = pickle.load(open(join(dirname(__file__),'EmotionDetectionInVoiceModel/logreg.pkl'), 'rb'))

    from sklearn.feature_extraction.text import CountVectorizer
    count_vect = CountVectorizer(analyzer='word')
    count_vect.fit(data['content'])

    tweets = pd.DataFrame([sentence])

    tweets[0] = tweets[0].str.replace('[^\w\s]',' ')
    from nltk.corpus import stopwords
    stop = stopwords.words('english')
    tweets[0] = tweets[0].apply(lambda x: " ".join(x for x in x.split() if x not in stop))
    from textblob import Word
    tweets[0] = tweets[0].apply(lambda x: " ".join([stemmer.stem(Word(word)) for word in x.split()]))

    tweet_count = count_vect.transform(tweets[0])

    tweet_pred = logreg.predict(tweet_count)

    if(tweet_pred[0]==0):
        emotion = "happy"
    else:
        emotion = "sad"

    return emotion