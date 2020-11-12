# develop a classifier for the 5 Celebrity Faces Dataset
from random import choice
from numpy import load
from numpy import expand_dims
from sklearn.preprocessing import LabelEncoder
from sklearn.preprocessing import Normalizer
from sklearn.svm import SVC
from matplotlib import pyplot

from train import *


def classify(file_name):

    # load training date and their face embeddings
    data = load('5-celebrity-faces-embeddings.npz')
    trainX, trainy = data['arr_0'], data['arr_1']

    #load image you want to classify - it should be in a folder
    x = extract_face(file_name)

    #model to extract face
    model = load_model('facenet_keras.h5')
    X = list()
    X.append(x)
    X = asarray(X)
    xEmbed = list()

    for face_pixels in X:
        embedding = get_embedding(model, face_pixels)
        xEmbed.append(embedding)
    testX = asarray(xEmbed)

    # normalize input vectors
    in_encoder = Normalizer(norm='l2')
    trainX = in_encoder.transform(trainX)
    testX = in_encoder.transform(testX)

    # label encode targets
    out_encoder = LabelEncoder()
    out_encoder.fit(trainy)
    trainy = out_encoder.transform(trainy)


    # fit model
    model = SVC(kernel='linear', probability=True)
    model.fit(trainX, trainy)

    # test model on a random example from the test dataset
    random_face_emb = testX[0]

    # prediction for the face
    samples = expand_dims(random_face_emb, axis=0)
    yhat_class = model.predict(samples)
    yhat_prob = model.predict_proba(samples)

    # get name
    class_index = yhat_class[0]
    class_probability = yhat_prob[0,class_index] * 100
    predict_names = out_encoder.inverse_transform(yhat_class)
    print('Predicted: %s (%.3f)' % (predict_names[0], class_probability))

    # plotting it - if you want to view the image
    pyplot.imshow(x)
    title = '%s (%.3f)' % (predict_names[0], class_probability)
    pyplot.title(title)
    pyplot.show()

if __name__ == "__main__":
    classify('classify/5.jpg')