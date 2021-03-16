import pyttsx3
import speech_recognition as sr
import numpy as np


r=sr.Recognizer()  
with sr.Microphone() as source:
    print("say something");
    audio=r.listen(source)
    print("time over")
try:
    print("text : "+r.recognize_google(audio));
    c=r.recognize_google(audio)
except:
    pass;

# initialisation 
engine = pyttsx3.init() 
  
# testing 
engine.say(c) 
engine.say("Thank you") 
engine.runAndWait() 
