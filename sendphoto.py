#CURL COMMAND
#Curl Command eqvialent
#curl -F "image=@Picture3.jpg" "http://localhost:5000/upload"

#PYHTHON SCRIPT
#within the raspberry pi you will need to pip install requests
#python sendphoto.py <directory of file>

import requests
import os
import sys

path = sys.argv[1]

url = 'http://localhost:5000/upload'
files = {'image': open(path, 'rb')}
requests.post(url, files=files)
