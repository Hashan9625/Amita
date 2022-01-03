# from nltk.stem.api import StemmerI
# import nltk
# import numpy as np
import pickle
from os.path import dirname, join
import json
# from ntlk.chat.util import Chat
def main():
    filename = join(dirname(__file__),'model/amita.json')

    intents = json.loads(open(filename).read())
    words = pickle.load(open(join(dirname(__file__),'model/words.pkl'), 'rb'))
    print("-------------------------------"+str(words))
    return "  hhh " + str(words)




# print(main())
# with open(filename, 'r', encoding='utf8', errors="ignore") as fin:
# data = fin.read().lower()