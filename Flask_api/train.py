
from os import listdir
from PIL import Image
from numpy import asarray
from matplotlib import pyplot
from mtcnn.mtcnn import MTCNN
from numpy import savez_compressed
from os.path import isdir
from keras.models import load_model
from numpy import load
from numpy import expand_dims

from random import choice
from numpy import load
from numpy import expand_dims
from sklearn.preprocessing import LabelEncoder
from sklearn.preprocessing import Normalizer
from sklearn.svm import SVC

 

def detect_face(filename, required_size=(160, 160)):
	# load image from file
	image = Image.open(filename)
	# convert to RGB, if needed
	image = image.convert('RGB')
	# convert to array
	pixels = asarray(image)
	# create the detector, using default weights
	detector = MTCNN()
	# detect faces in the image
	results = detector.detect_faces(pixels)

	if(len(results) > 0):
		return True
	else:
		return False


# extract a single face from a given photograph
def extract_face(filename, required_size=(160, 160)):
	# load image from file
	image = Image.open(filename)
	# convert to RGB, if needed
	image = image.convert('RGB')
	# convert to array
	pixels = asarray(image)
	# create the detector, using default weights
	detector = MTCNN()
	# detect faces in the image
	results = detector.detect_faces(pixels)
	# extract the bounding box from the first face
	#flag = []
	#if len(results) > 0:
	cropped_images = []
	print(results)
	if len(results) > 0:
		for i in range(0,len(results)):
			x1, y1, width, height = results[i]['box']
			# bug fix
			x1, y1 = abs(x1), abs(y1)
			x2, y2 = x1 + width, y1 + height
			# extract the face
			face = pixels[y1:y2, x1:x2]
			# resize pixels to the model size
			image = Image.fromarray(face)
			image = image.resize(required_size)
			flag = asarray(image)
			cropped_images.append(flag)
	return cropped_images


# load images and extract faces for all images in a directory
def load_faces(directory):
	faces = list()

	# enumerate files
	for filename in listdir(directory):
		
		# path
		path = directory + filename
		# get face
		faces = extract_face(path)
		'''
		if 0 < len(face):
			# store
			faces.append(face[0])
		else:
			print("\n\n\nINVALID PHOTO: "  + filename + "\n\n\n")
		'''


	return faces

# load a dataset that contains one subdir for each class that in turn contains images
def load_dataset(directory):
	X, y = list(), list()
	# enumerate folders, on per class

	for subdir in listdir(directory):
		# path
		path = directory + subdir + '/'
		# skip any files that might be in the dir
		if not isdir(path):
			continue
		# load all faces in the subdirectory
		faces = load_faces(path)
		

		# create labels
		labels = [subdir for _ in range(len(faces))]
		# summarize progress
		#print('>loaded %d examples for class: %s' % (len(faces), subdir))
		# store
		X.extend(faces)
		y.extend(labels)

	pyplot.show()
	return asarray(X), asarray(y)
 


# get the face embedding for one face
def get_embedding(model, face_pixels):
	# scale pixel values
	face_pixels = face_pixels.astype('float32')
	# standardize pixel values across channels (global)
	mean, std = face_pixels.mean(), face_pixels.std()
	face_pixels = (face_pixels - mean) / std
	# transform face into one sample
	samples = expand_dims(face_pixels, axis=0)
	# make prediction to get embedding
	yhat = model.predict(samples)
	return yhat[0]


def train(file_name):
	# load train dataset
	trainX, trainy = load_dataset(file_name)

	# save arrays to one file in compressed format
	savez_compressed('5-celebrity-faces-dataset.npz', trainX, trainy)

	# load the face dataset
	data = load('5-celebrity-faces-dataset.npz')
	trainX, trainy = data['arr_0'], data['arr_1']
	#print('Loaded: ', trainX.shape, trainy.shape, testX.shape, testy.shape)
	# load the facenet model
	model = load_model('facenet_keras.h5')
	#print('Loaded Model')
	# convert each face in the train set to an embedding

	newTrainX = list()
	for face_pixels in trainX:
		embedding = get_embedding(model, face_pixels)
		newTrainX.append(embedding)
	newTrainX = asarray(newTrainX)

	# save arrays to one file in compressed format
	savez_compressed('5-celebrity-faces-embeddings.npz', newTrainX, trainy)


if __name__ == "__main__":
	train('train/')

