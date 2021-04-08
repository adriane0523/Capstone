import os
import cv2
import requests
import numpy as np
import time
import threading
from classify import *
import json
from firebase import Firebase
import datetime
from werkzeug.utils import secure_filename
import shutil
from firebase import firebase
import datetime
import sqlite3
import numpy as np
from PIL import Image as im
import random


#pip install pycryptodome
#https://pypi.org/project/firebase/

fbconfig = {
  "databaseURL": "https://capstonephoneapp-default-rtdb.firebaseio.com/",
  "apiKey": "AAAAB9tD1QY:APA91bFoyAq5bkZ1W0jp5c-rb0CEuOUGXS2jmNxp0TNi5REyb2T9WJh_lnY-nfKNLQAew7kwKL5wnD9lr_LB9g5h5kJ6fkUmsASmZsLx3mhAENcxasL1L-hRG0l5la6p5kh2fYIicGk-",
  "authDomain": "",
  "storageBucket": ""
}

firebase = Firebase(fbconfig)
#r = requests.get('http://98.171.4.142:8000/stream.mjpg', stream=True)

UPLOAD_FOLDER = 'classify'
FILE_PATH = './photos/classify'
DB_PATH = './db/database.db'
index = 0

def start_classification(killtime, instructor_id, course_id, filename='./photos/img.png'):
    global index

    firebase.database().child('log').remove()
    for file in os.listdir('static'):
      if file.endswith('.png'):
            os.remove('static/'+file)
    # if user does not select file, browser also
    # submit a empty part without filename
    while(time.time() < killtime + 3):   
      if(detect_face(filename)):
          shutil.copyfile((filename), (FILE_PATH+'/img.png'))
          classify_picture(filename, instructor_id, course_id)
      else:
          print("No Face detected")
      time.sleep(1)

def classify_picture(filename, instructor_id, course_id):
  global index

  index = (int)(random.randint(1,1000))
  if(detect_face(filename)):
      con = sqlite3.connect(DB_PATH)
      cur = con.cursor()
      result = classify(FILE_PATH + '/img.png')
      data = {}
      relation = ""
      description = ""
      with open('data.json') as f:
          data = json.load(f)

      for i in data["data"]:

          if i["name"] == result[0]:
              relation = i["relation"]
              description = i["description"]
      print(result[0])
      cur.execute("""SELECT st.given_name, st.family_name, st.year, grade, comment FROM Students AS st, (SELECT student_id, grade, comment FROM Students_To_Courses WHERE course_id IN 
                        (SELECT course_id FROM Courses WHERE instructorID=%d) AND course_id=%d) AS stc WHERE id=%d AND stc.student_id=st.id""" % (int(instructor_id), int(course_id), int(result[0])))
                        
      row = cur.fetchone()
      data = im.fromarray(extract_face(filename))
      data.save('static/file'+format(index)+'.png')

      if row:
        data = {
            "name": row[0] + ' ' + row[1],
            "year": row[2],
            "grade": row[3],
            "comment": row[4],
            "probability": result[1],
            "relation": relation,
            "description": description,
            "picture": 'static/file'+format(index)+'.png',
            "timestamp": stringify_date(datetime.datetime.now())
        }
        if (firebase.database().child('log').get().val() != None and len(firebase.database().child('log').get().val()) > 3):
          firebase.database().child('log').remove()
          firebase.database().child('log').push(data)
          number = 0
        else:
            firebase.database().child('log').push(data)
        print(data)
      else: 
        print("No student with id exists")
  else:
    print("No face detected")

def stringify_date(obj):
    if isinstance(obj, datetime.datetime):
        return obj.strftime('%Y-%m-%d %H:%M:%S')
  

# 
# Remove hardcoded instructor id and course id, and pass real user input as arguments
# 
def listen(instructor_id=1123456780, course_id=1):
  r = requests.get('http://127.0.0.1:5000/video_feed', stream=True)
  T_END = time.time() + 10 
  th = threading.Thread(target = start_classification, args = (T_END, instructor_id, course_id))
 
  if(r.status_code == 200):
      byte = bytes()
      dropcount = 0
      threaded = False
    
      for chunk in r.iter_content(chunk_size=1024):
          dropcount += 1
          byte += chunk
          a = byte.find(b'\xff\xd8')
          b = byte.find(b'\xff\xd9')
          if a != -1 and b != -1:
              jpg = byte[a:b+2]
              byte = byte[b+2:]
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

if __name__ == "__main__":
  listen()