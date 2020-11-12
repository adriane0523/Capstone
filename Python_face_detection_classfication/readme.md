dependencies:
pip install tensorflow keras matplotlib numpy pillow
__________________________________________________________________

Inspired by: https://machinelearningmastery.com/how-to-develop-a-face-recognition-system-using-facenet-in-keras-and-an-svm-classifier/

This program will classify any image stored into the classify folder. In the classify.py make sure to reference which image you are referencing. 

Classify Folder – Hold photos you want to classify
Classify.py – classify image in the classify folder
Train.py – train model to recognize faces from the train folder
Train Folder – name of each folder is the name of the user which will hold photos of that user
.npz file – stores the face arrays and embeddings of the training images

Instructions when trying to classify new faces:
1.	Create new folder in train with your name and photos of that person
2.	Run train.py – if the program fails to run due to out of index error from 	x1, y1, width, height = results[0]['box'], that means it can’t recognize a face in one of the folders. If that is the case then crop the photos to be closer to the face or replace with a clearer photo
3.	After successful run of the program - 5-celebrity-faces-dataset.npz and 5-celebrity-faces-embeddings.npz is written into.
4.	Input image you want to classify in the classify folder
5.	Go to classify.py and make sure the directory is pointing to the image you want to classify.
6.	Run classify.py

