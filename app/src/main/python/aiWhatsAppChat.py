import nltk
from nltk.stem.api import StemmerI
from nltk.stem.lancaster import LancasterStemmer
import numpy as np
import pickle
from os.path import dirname, join
import re, json
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

def main(sentence, emotion):
    print(sentence)
    return amita_response(sentence,emotion)

def clean_up_sentence(sentence):
    # tokenize
    sentence_words = nltk.word_tokenize(sentence)
    # lemmatize
    sentence_words = [stemmer.stem(re.sub(r'[^a-zA-Z]', '', word).lower()) for word in sentence_words]
    return sentence_words

def bow(sentence, words, show_details=True):
    # tokenize
    sentence_words = clean_up_sentence(sentence)
    # matrix of N words
    bag = [0]*len(words)
    for s in sentence_words:
        for i,w in enumerate(words):
            if w == s:
                bag[i] = 1
    return(np.array(bag))

# return set of responce
def predict_class(sentence, model):
    words = pickle.load(open(join(dirname(__file__),'VerbalResponseModel2WhatsAppChat/words.pkl'), 'rb'))
    classes = pickle.load(open(join(dirname(__file__),'VerbalResponseModel2WhatsAppChat/classes.pkl'), 'rb'))

    p = bow(sentence, words)

    res = model.predict(np.array([p]))[0]
    ERROR_THRESHOD = 0.01
    results = [[i,r] for i,r in enumerate(res) if r>ERROR_THRESHOD]
    # sort by probability
    results.sort(key=lambda x: x[1], reverse=True)
    return_list = []
    for r in results:
        return_list.append({"groups": classes[r[0]], "probability": str(r[1])})

    print(return_list)
    return return_list

#pic emotion response
def getResponse(ints, intents_json, emotion):
    if len(ints) != 0:

        predictMsgId = -1
        for groupPedicted in ints:
            if(intents_json[int(groupPedicted['groups'])]['emotion'] == emotion):
                predictMsgId = int(groupPedicted['groups'])
                break

        if(predictMsgId == -1):
            predictMsgId = int(ints[0]['groups'])

        predictMsg = intents_json[predictMsgId]
        print('predict message> '+ str(predictMsg))

        sender = intents_json[predictMsgId]['sender']

        i = 1
        while True:
            if sender == intents_json[predictMsgId+i]['sender']:
                i = i+1
            else:
                result = intents_json[predictMsgId+i]['message']
                print(intents_json[predictMsgId+i])
                i = i+1
                break

        while True:
            if sender != intents_json[predictMsgId+i]['sender']:
                result += ' \n ' + intents_json[predictMsgId+i]['message']
                print(intents_json[predictMsgId+i])
                i = i+1
            else:
                break
    else:
        result = "Can you say again please"
    return result

def amita_response(msg, emotion):
    filenameModel = join(dirname(__file__),'VerbalResponseModel2WhatsAppChat/model_amita.h5')
    filename = join(dirname(__file__),'VerbalResponseModel2WhatsAppChat/WhatsAppDataFinal.json')
    intents = json.loads(open(filename).read())

    model = load_model(filenameModel, compile = False)
    ints = predict_class(msg, model)
    res = getResponse(ints, intents, emotion)
    return res