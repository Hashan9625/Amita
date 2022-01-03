# from nltk.stem.api import StemmerI
# import nltk
import numpy as np
import pickle
from os.path import dirname, join
import json
# from ntlk.chat.util import Chat
def main():
    filename = join(dirname(__file__),'model/amita.json')

    intents = json.loads(open(filename).read())
    words = pickle.load(open(join(dirname(__file__),'model/words.pkl'), 'rb'))
    classes = pickle.load(open(join(dirname(__file__),'model/classes.pkl'), 'rb'))

    data = np.zeros(5)
    print("-------------------------------"+str(words))
    return "  > " + str(data)




# print(main())
# with open(filename, 'r', encoding='utf8', errors="ignore") as fin:
# data = fin.read().lower()