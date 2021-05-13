import sqlite3

def createTable(pathToDB, tableName):   
  con = sqlite3.connect('%s' % (pathToDB))
  cur = con.cursor()
  cur.execute("""SELECT name FROM sqlite_master WHERE type='table' AND name='%s';""" % (tableName))
  if cur.fetchone():
    throwError(500, 'Table %s Already Exists' % (tableName))
    return
  cur.execute('CREATE TABLE Students (asuID INT NOT NULL PRIMARY KEY UNIQUE, given_name VARCHAR(50), family_name VARCHAR(50))')

def createAllTables(pathToDB):   
  con = sqlite3.connect('%s' % (pathToDB))
  cur = con.cursor()
  cur.execute("""SELECT name FROM sqlite_master WHERE type='table' AND name='Students';""")
  if cur.fetchone():
    throwError(500, 'Table Students Already Exists')
  else:
    cur.execute('CREATE TABLE Students (id INTEGER(10) NOT NULL PRIMARY KEY UNIQUE, given_name VARCHAR(50), family_name VARCHAR(50), year VARCHAR(10))')
  
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
    cur.execute('CREATE TABLE Students_To_Courses (student_id INTEGER NOT NULL, course_id INTEGER NOT NULL, grade VARCHAR(2), comment VARCHAR(50), CONSTRAINT FK_StudentInCourse FOREIGN KEY (student_id) REFERENCES Students(id), CONSTRAINT FK_CourseForStudent FOREIGN KEY (course_id) REFERENCES Courses(id))')
  
  con.close()

def dbInsert(dbpath):
  con = sqlite3.connect(dbpath)
  cur = con.cursor()
  #INSERT STUDENTS
  cur.execute(""" DELETE FROM Students """)
  cur.execute(""" INSERT INTO Students (id, given_name, family_name, year) 
                    VALUES
                    (1234567890, 'Adriane', 'Inocencio', 'Senior'),
                    (1234567891, 'Ben', 'Afflek', 'Freshman'),
                    (1234567892, 'Elton', 'John', 'Sophomore'),
                    (1234567893, 'Jason', 'Kwon', 'Senior'),
                    (1234567894, 'Jerry', 'Seinfeld', 'Junior'),
                    (1234567895, 'Kyle', 'Gonzalez', 'Senior'),
                    (1234567896, 'Madonna', '', 'Freshman'),
                    (1234567897, 'Mindy', 'Kaling', 'Junior'),
                    (1234567898, 'Greyson', 'Britt', 'Senior'),
                    (3, 'Rajat', 'Hairy man', 'Senior');
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
  cur.execute(""" INSERT INTO Students_To_Courses (student_id, course_id, grade, comment)
                    VALUES
                    (1234567890, 1, 'A+', 'Excellent student'),
                    (1234567890, 2, 'A+', 'Exquisite student'),
                    (1234567891, 1, 'B+', 'Lacking Chapter 3 Knowledge'),
                    (1234567891, 2, 'C-', 'Lacking overall'),
                    (1234567892, 1, 'A0', 'Eagerly participates in class'),
                    (1234567893, 1, 'B+', 'Lacking Continuity Concept understanding'),
                    (1234567893, 1, 'A0', 'Curious Asks many questions'),
                    (1234567894, 1, 'B0', 'Lacking Chapter 3 Knowledge'),
                    (1234567894, 1, 'C+', 'Lacking Chapter 3 Knowledge'),
                    (1234567895, 1, 'A+', 'Lacking Chapter 3 Knowledge'),
                    (1234567896, 1, 'B-', 'Lacking Chapter 3 Knowledge'),
                    (1234567896, 1, 'B0', 'Lacking Chapter 3 Knowledge'),
                    (1234567897, 1, 'A+', 'Lacking Chapter 3 Knowledge'),
                    (1234567897, 1, 'A0', 'Lacking Chapter 3 Knowledge'),
                    (1234567898, 1, 'A0', 'Lacking Chapter 3 Knowledge'),
                    (3, 1, 'A0', 'Lacking Chapter 3 Knowledge')
                    """)


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

def testQuery(dbpath):
  con = sqlite3.connect(dbpath)
  cur = con.cursor()
  cur.execute("""SELECT st.given_name, st.family_name, st.year, grade, comment FROM Students AS st, (SELECT student_id, grade, comment FROM Students_To_Courses WHERE course_id IN 
                      (SELECT course_id FROM Courses WHERE instructorID=1123456780) AND course_id=1) AS stc WHERE id=1234567890 AND stc.student_id=st.id""")
  print(cur.fetchall())
def throwError(errorCode, reason):
  print('Error (%d): %s' % (errorCode, reason))



if __name__ == "__main__":
  dbpath = './db/database.db'
  tableList = ['Students', 'Instructors', 'Courses', 'Students_To_Courses']
  dropAllTables(dbpath, tableList)
  createAllTables(dbpath)
  dbInsert(dbpath)
  printTables(dbpath)
  testQuery(dbpath)