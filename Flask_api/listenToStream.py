import os
import cv2
import requests
import numpy as np
import time
import threading
from classify import *
import json
import datetime
from werkzeug.utils import secure_filename
import shutil

#r = requests.get('http://98.171.4.142:8000/stream.mjpg', stream=True)


UPLOAD_FOLDER = 'classify'
FILE_PATH = './photos/classify'

def start_classification(killtime, filename='./photos/img.png'):
    # if user does not select file, browser also
    # submit a empty part without filename
    while(time.time() < killtime + 3):   
      if(detect_face(filename)):
          shutil.copyfile((filename), (FILE_PATH+'/img.png'))
          classify_picture(filename)
      else:
          print("No Face detected")
      time.sleep(2)


def classify_picture(filename):
    name = classify(FILE_PATH + '/img.png')
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


if __name__ == "__main__":
  r = requests.get('http://127.0.0.1:5000/video_feed', stream=True)
  T_END = time.time() + 10 
  th = threading.Thread(target = start_classification, args = (T_END,))
 
  if(r.status_code == 200):
      bytes = bytes()
      dropcount = 0
      threaded = False
    
      for chunk in r.iter_content(chunk_size=1024):
          dropcount += 1
          bytes += chunk
          a = bytes.find(b'\xff\xd8')
          b = bytes.find(b'\xff\xd9')
          if a != -1 and b != -1:
              jpg = bytes[a:b+2]
              bytes = bytes[b+2:]
              if dropcount > 1200:
                dropcount = 0
                i = cv2.imdecode(np.fromstring(jpg, dtype=np.uint8), cv2.IMREAD_COLOR)
                print("writiting")
                os.remove('./photos/img.png')
                cv2.imwrite('./photos/img.png',i)
                #cv2.imshow('i', i)
                if not threaded:
                  print("threading")
                  th.start()
                  threaded = True
          if time.time() > T_END:
            th.join()
            exit(0)
            
      
  else:
      print("Received unexpected status code {}".format(r.status_code))
