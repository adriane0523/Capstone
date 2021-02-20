from firebase import firebase

firebase = firebase.FirebaseApplication('https://capstonephoneapp-default-rtdb.firebaseio.com/', None)

#check how many
result = firebase.get('/log/', '')
if (len(result) > 5):
    for i in result:
        firebase.delete('/log/', i)
#put it into the database
data =  { 'Name': 'John Doe',
          'RollNo': 3,
          'Percentage': 70.02
          }
result = firebase.post('/log/',data)
