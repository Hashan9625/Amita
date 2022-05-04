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
    print("voice detection ----------------")
    print(sentence)
    return "Happy"