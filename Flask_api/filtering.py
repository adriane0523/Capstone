from train import *
import time

from os import listdir
from os.path import isfile, join

mypath = "./photos"
onlyfiles = [f for f in listdir(mypath) if isfile(join(mypath, f))]


if (len(onlyfiles) > 0):
    for i in onlyfiles:
        image_path = mypath + '/'+ i
        print(detect_face(image_path, required_size=(160, 160)))


print("Running filtering script...")

'''
while(True):
    time.sleep(1)
'''