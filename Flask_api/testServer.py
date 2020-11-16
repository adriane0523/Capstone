import flask
from flask import request

app = flask.Flask(__name__)
app.config['MAX_CONTENT_LENGTH'] = 50 * 1024 * 1024

@app.route('/', methods=['GET', 'POST'])
def handle_request():
    print("Successful Connection")
    return "Successful Connection"

@app.route('/upload', methods=['POST'])
def upload():
    print(request.files.getlist)
    print("Successful Connection to host")
    return "Successful Connection"

app.run(host="0.0.0.0", port=5000, debug=True, threaded=True)