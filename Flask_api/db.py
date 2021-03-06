import sqlite3

def createTable(pathToDB, tableName):   
  con = sqlite3.connect('%s' % (pathToDB))
  cur = con.cursor()
  cur.execute("""SELECT name FROM sqlite_master WHERE type='table' AND name='%s';""" % (tableName))
  if cur.fetchone():
    throwError(500, 'Table %s Already Exists' % (tableName))
    return
  cur.execute('CREATE TABLE Students (asuID INT NOT NULL PRIMARY KEY UNIQUE, given_name VARCHAR(50), family_name VARCHAR(50), grade VARCHAR(2))')

def createAllTables(pathToDB):   
  con = sqlite3.connect('%s' % (pathToDB))
  cur = con.cursor()
  cur.execute("""SELECT name FROM sqlite_master WHERE type='table' AND name='Students';""")
  if cur.fetchone():
    throwError(500, 'Table Students Already Exists')
  else:
    cur.execute('CREATE TABLE Students (asuID INT NOT NULL PRIMARY KEY UNIQUE, given_name VARCHAR(50), family_name VARCHAR(50), grade VARCHAR(2))')
  
  cur.execute("""SELECT name FROM sqlite_master WHERE type='table' AND name='Instructors';""")
  if cur.fetchone():
    throwError(500, 'Table Instructors Already Exists')
  else:   
    cur.execute('CREATE TABLE Instructors (asuID INT NOT NULL PRIMARY KEY UNIQUE, given_name VARCHAR(50), family_name VARCHAR(50))')

  cur.execute("""SELECT name Courses FROM sqlite_master WHERE type='table' AND name='Courses';""")
  if cur.fetchone():
    throwError(500, 'Table Already Exists')
  else:
    cur.execute('CREATE TABLE Courses (courseID INT NOT NULL PRIMARY KEY UNIQUE, instructorID INT NOT NULL, subject VARCHAR(50), number SMALLINT(3), CONSTRAINT FK_Instructor_for_course FOREIGN KEY (instructorID) REFERENCES Instructors(instID))')

  cur.execute("""SELECT name FROM sqlite_master WHERE type='table' AND name='Students_To_Courses';""")
  if cur.fetchone():
    throwError(500, 'Table Student_To_Courses Already Exists')
  else:
    cur.execute('CREATE TABLE Students_To_Courses (asuID INT NOT NULL, courseID INT NOT NULL, CONSTRAINT FK_StudentInCourse FOREIGN KEY (asuID) REFERENCES Students(asuID), CONSTRAINT FK_CourseForStudent FOREIGN KEY (courseID) REFERENCES Courses(courseID))')
  
  cur.execute("""SELECT name FROM sqlite_master WHERE type='table' AND name='Sources_To_Student';""")
  if cur.fetchone():
    throwError(500, 'Table Sources_To_Student Already Exists')
  else:
    cur.execute('CREATE TABLE Sources_To_Student (asuID INT NOT NULL, sourceID VARCHAR(254) NOT NULL PRIMARY KEY UNIQUE, CONSTRAINT FK_SrcForStudent FOREIGN KEY (asuID) REFERENCES Students(asuID))')
  
  con.close()

def dbtest():
  con = sqlite3.connect('/db/databse.db')
  cur = con.cursor()
  cur.execute(""" INSERT INTO students (name, addr, city, session, grade) 
    VALUES('Jason Kwon', '1367 S Country Club Dr Apt 1171', 'Mesa', 11111, 'A+')
    """)
  cur.execute(""" SELECT * FROM students """)
  for row in cur:
    print(row)
  cur.execute(""" DELETE FROM students """)

def throwError(errorCode, reason):
  print('Error (%d): %s' % (errorCode, reason))

if __name__ == "__main__":
  createAllTables('./db/database.db')
  