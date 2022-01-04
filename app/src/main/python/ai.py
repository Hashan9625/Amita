import nltk
from nltk.stem.api import StemmerI
from nltk.stem.lancaster import LancasterStemmer
import numpy as np
import pickle
from os.path import dirname, join
import json
from tensorflow.keras.models import load_model
stemmer = nltk.stem.PorterStemmer()
lemmatizer = nltk.stem.WordNetLemmatizer()
import random

def main(sentence, emotion):
    nltk.download('stopwords')
    nltk.download('wordnet')
    nltk.download('punkt')
    nltk.download('averaged_perceptron_tagger')

    # with open(join(dirname(__file__),'model/model_amita'), 'rb') as f:
    #     model = pickle.load(f)

    # from tensorflow.keras.models import load_model
    # with open(join(dirname(__file__),'model/model_amita'), 'rb') as fModel:

    # model = pickle.load(open(join(dirname(__file__),'model/model_amita.pkl', 'rb')
    # model = pickle.load(open(join(dirname(__file__),'model/model_amita.pkl'), 'rb'))

    filename = join(dirname(__file__),'model/amita.json')
    intents = json.loads(open(filename).read())
    words = pickle.load(open(join(dirname(__file__),'model/words.pkl'), 'rb'))
    classes = pickle.load(open(join(dirname(__file__),'model/classes.pkl'), 'rb'))

    return " Response > "

def clean_up_sentence(sentence):
    # tokenize
    sentence_words = nltk.word_tokenize(sentence)
    # lemmatize
    sentence_words = [stemmer.stem(word.lower()) for word in sentence_words]
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
                if show_details:
                    print("found in bag: %s" % w)
    return(np.array(bag))

# return set of responce
def predict_class(sentence, model):
    p = bow(sentence, words, show_details=False)
    res = model.predict(np.array([p]))[0]
    ERROR_THRESHOD = 0.25
    results = [[i,r] for i,r in enumerate(res) if r>ERROR_THRESHOD]
    # sort by probability
    results.sort(key=lambda x: x[1], reverse=True)
    return_list = []
    for r in results:
        return_list.append({"groups": tags[r[0]], "probability": str(r[1])})
    return return_list

#pic emotion response
def getResponse(ints, groups_json, emotion):
    tag = ints[0]['groups']
    list_of_group = groups_json['groups']
    for i in list_of_group:
        if(i['tag']==tag):
            # print(i['responses'])
            result = i['responses'][emotion]
            break
    return result

def amita_response(msg, emotion):
    ints = predict_class(msg, model)
    res = getResponse(ints, intents, emotion)
    # print(ints)
    return res

# print(main())
# with open(filename, 'r', encoding='utf8', errors="ignore") as fin:
# data = fin.read().lower()
# data = np.zeros(5)


    # print("-------------------------------"+str(words))