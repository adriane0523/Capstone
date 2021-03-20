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
    cur.execute('CREATE TABLE Students (id INTEGER(10) NOT NULL PRIMARY KEY UNIQUE, given_name VARCHAR(50), family_name VARCHAR(50), grade VARCHAR(2))')
  
  cur.execute("""SELECT name FROM sqlite_master WHERE type='table' AND name='Instructors';""")
  if cur.fetchone():
    throwError(500, 'Table Instructors Already Exists')
  else:   
    cur.execute('CREATE TABLE Instructors (id INTEGER(10) NOT NULL PRIMARY KEY UNIQUE, given_name VARCHAR(50), family_name VARCHAR(50))')

  cur.execute("""SELECT name Courses FROM sqlite_master WHERE type='table' AND name='Courses';""")
  if cur.fetchone():
    throwError(500, 'Table Courses Already Exists')
  else:
    cur.execute('CREATE TABLE Courses (id INTEGER PRIMARY KEY AUTOINCREMENT, instructorID INTEGER NOT NULL, subject VARCHAR(50), number SMALLINT(3), CONSTRAINT FK_Instructor_for_course FOREIGN KEY (instructorID) REFERENCES Instructors(instID))')

  cur.execute("""SELECT name FROM sqlite_master WHERE type='table' AND name='Students_To_Courses';""")
  if cur.fetchone():
    throwError(500, 'Table Student_To_Courses Already Exists')
  else:
    cur.execute('CREATE TABLE Students_To_Courses (student_id INTEGER NOT NULL, course_id INTEGER NOT NULL, grade VARCHAR(2), CONSTRAINT FK_StudentInCourse FOREIGN KEY (student_id) REFERENCES Students(id), CONSTRAINT FK_CourseForStudent FOREIGN KEY (course_id) REFERENCES Courses(id))')
  
  con.close()

def dbInsert(dbpath):
  con = sqlite3.connect(dbpath)
  cur = con.cursor()
  #INSERT STUDENTS
  cur.execute(""" DELETE FROM Students """)
  cur.execute(""" INSERT INTO Students (id, given_name, family_name, grade) 
                    VALUES
                    (1234567890, 'Adriane', 'Inocencio', 'A+'),
                    (1234567891, 'Ben', 'Afflek', 'B0'),
                    (1234567892, 'Elton', 'John', 'C0'),
                    (1234567893, 'Jason', 'Kwon', 'A+'),
                    (1234567894, 'Jerry', 'Seinfeld', 'B-'),
                    (1234567895, 'Kyle', 'Gonzalez', 'A+'),
                    (1234567896, 'Madonna', '', 'F'),
                    (1234567897, 'Mindy', 'Kaling', 'A-');
                  """)
  #INSERT INSTRUCTORS
  cur.execute(""" DELETE FROM Instructors """)
  cur.execute(""" INSERT INTO Instructors (id, given_name, family_name) 
                    VALUES
                    (1123456780, 'Ming', 'Zhao'),
                    (1123456781, 'Mutsumi', 'Nakamura'),
                    (1123456782, 'Janaka', 'Balasooriya')
                    """)
  
  #INSERT COURSES
  cur.execute(""" DELETE FROM Courses """)
  cur.execute(""" INSERT INTO Courses (id, instructorID, subject, number)
                    VALUES
                    (1, 1123456780, 'CSE', 546),
                    (2, 1123456781, 'CSE', 412), 
                    (3, 1123456782, 'CSE', 445)""")

  #INSERT COURSE TO STUDENT RELATION
  cur.execute(""" DELETE FROM Students_To_Courses """)
  cur.execute(""" INSERT INTO Students_To_Courses (student_id, course_id, grade)
                    VALUES
                    (1234567890, 1, 'A+'),
                    (1234567890, 2, 'A+'),
                    (1234567891, 1, 'B+'),
                    (1234567891, 2, 'C-'),
                    (1234567892, 1, 'S+'),
                    (1234567893, 1, 'B+')""")

  con.commit()

def printTables(dbpath):
  con = sqlite3.connect(dbpath)
  cur = con.cursor()
  cur.execute(""" SELECT * FROM Students """)
  for row in cur:
    print(row)
  cur.execute(""" SELECT * FROM Instructors """)
  for row in cur:
    print(row)
  cur.execute(""" SELECT * FROM Courses """)
  for row in cur:
    print(row)
  cur.execute(""" SELECT * FROM Students_To_Courses """)
  for row in cur:
    print(row)
  
def dropAllTables(dbpath, tableList):
  con = sqlite3.connect(dbpath)
  cur = con.cursor()
  for table in tableList:
    dropquery = """DROP TABLE %s""" % (table)
    cur.execute(dropquery)
def throwError(errorCode, reason):
  print('Error (%d): %s' % (errorCode, reason))

if __name__ == "__main__":
  dbpath = './db/database.db'
  tableList = ['Students', 'Instructors', 'Courses', 'Students_To_Courses']
  dropAllTables(dbpath, tableList)
  createAllTables(dbpath)
  dbInsert(dbpath)
  printTables(dbpath)