import cv2
import requests
import numpy as np
import time
#r = requests.get('http://98.171.4.142:8000/stream.mjpg', stream=True)

r = requests.get('http://127.0.0.1:5000/video_feed', stream=True)

if(r.status_code == 200):
    bytes = bytes()
    for chunk in r.iter_content(chunk_size=1024):
        bytes += chunk
        a = bytes.find(b'\xff\xd8')
        b = bytes.find(b'\xff\xd9')
        if a != -1 and b != -1:
            jpg = bytes[a:b+2]
            bytes = bytes[b+2:]
            i = cv2.imdecode(np.fromstring(jpg, dtype=np.uint8), cv2.IMREAD_COLOR)
            print("writiting")
            cv2.imwrite('./photos/img.png',i)
            time.sleep(5)
            #cv2.imshow('i', i)
            
    else:
        print("Received unexpected status code {}".format(r.status_code))