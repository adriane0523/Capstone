
from classify import *
import json
from firebase import firebase
import datetime
import cv2
from werkzeug.utils import secure_filename
import shutil
import time

UPLOAD_FOLDER = 'classify'
file_path = './photos/classify'

def start_classfication(filename):
    # if user does not select file, browser also
    # submit a empty part without filename
   
    if(detect_face(filename)):
        shutil.copyfile((filename), (file_path+'/img.png'))
        classify_picture(filename)
    else:
        print("No Face detected")



def classify_picture(filename):
    name = classify(file_path + '/img.png')
    data = {}
    relation = ""
    description = ""
    with open('data.json') as f:
        data = json.load(f)

    for i in data["data"]:

        if i["name"] == name[0]:
            relation = i["relation"]
            description = i["description"]

    data = {
        "name": name[0],
        "probability":name[1],
        "relation": relation,
        "description": description,
        "picture": filename,
        "timestamp": datetime.datetime.now() 
    }
    print(name[0])

while(True):
    time.sleep(2)
    start_classfication('./photos/img.png')
