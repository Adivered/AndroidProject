# -*- coding: utf-8 -*-

from flask import Flask, render_template, redirect, url_for, request, jsonify, flash
import json
from flask_bootstrap import Bootstrap
from flask_wtf import FlaskForm
from wtforms import StringField, PasswordField, BooleanField
from wtforms.validators import InputRequired, Email, Length
from flask_sqlalchemy import SQLAlchemy
from werkzeug.security import generate_password_hash, check_password_hash
import sqlite3
from flask_login import LoginManager, UserMixin, login_user, login_required, logout_user, current_user
from datetime import datetime, date
from werkzeug.utils import secure_filename
from SQL import *
from PIL import Image
from io import BytesIO
import base64
import urllib2

app = Flask(__name__)
IMGFOLDER = 'PATH'
ALLOWED_EXTENSIONS = set(['png', 'jpg', 'jpeg', 'gif'])
app.config['SECRET_KEY'] = 'NOBODY-CAN-GUESS-THIS'
app.config['JSON_AS_ASCII'] = False
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///UserDataBase.db'
Bootstrap(app)
db = SQLAlchemy(app)
login_manager = LoginManager()
login_manager.init_app(app)
login_manager.login_view = 'login'


class User(UserMixin, db.Model):
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String, unique=True)
    email = db.Column(db.String)
    password = db.Column(db.String)


@login_manager.user_loader
def load_user(user_id):
    return User.query.get(int(user_id))


class LoginForm(FlaskForm):
    username = StringField(u'שם משתמש', validators=[
                           InputRequired(), Length(min=3, max=20)])
    password = PasswordField('password', validators=[
                             InputRequired(), Length(min=3, max=80)])
    remember = BooleanField('remember me')


class RegisterForm(FlaskForm):
    email = StringField('email', validators=[InputRequired(), Email(
        message="Invalid Email"), Length(min=6, max=30)])
    username = StringField('name', validators=[
                           InputRequired(), Length(min=4, max=20)])
    password = PasswordField('password', validators=[
                             InputRequired(), Length(min=5, max=80)])


def dict_factory(cursor, row):
    d = {}
    for idx, col in enumerate(cursor.description):
        d[col[0]] = row[idx]
    return d


@app.route('/')
def index():
    return render_template('index.html')


@app.route('/login', methods=['POST'])
def login():
    form = LoginForm()

    if form.validate_on_submit():
        user = User.query.filter_by(username=form.username.data).first()
        if user:
            # compares the password hash in the db and the hash of the password typed in the form
            if check_password_hash(user.password, form.password.data):
                login_user(user, remember=form.remember.data)
                return redirect(url_for('dashboard'))
        return 'invalid username or password'

    return render_template('login.html', form=form)


@app.errorhandler(404)
def page_not_found(e):
    return "<h1>404</h1><p>The resource could not be found.</p>", 404


@app.route('/all', methods=['GET'])
def api_all():
    conn = sqlite3.connect('UserDataBase.db')
    conn.row_factory = dict_factory
    cur = conn.cursor()
    users = cur.execute('SELECT * FROM user;').fetchall()
    return jsonify(users)


@app.route('/signup', methods=['GET', 'POST'])
def signup():
    form = RegisterForm()

    if form.validate_on_submit():
        hashed_password = generate_password_hash(
            form.password.data, method='sha256')
        # add the user form input which is form.'field'.data into the column which is 'field'
        new_user = User(username=form.username.data,
                        email=form.email.data, password=hashed_password)
        db.session.add(new_user)
        db.session.commit()
        return 'new user has been created bro!'

    return render_template('signup.html', form=form)


@app.route('/updateKnisa', methods=['GET', 'POST'])
def updateKnisa():
    if request.method == 'POST':
        today = date.today()
        data = request.json
        username = request.json[0]["username"]
        lat = request.json[0]["LAT"]
        lon = request.json[0]["LONG"]
        qrcode = request.json[0]["QRCODE"]
        name = request.json[0]["name"]
        now = returnCurrentHour()
        if(authRehev(name, qrcode)):
            doKnisa(username, today, now)
            updateLoginLATCordinates(username, lat)
            updateLoginLONGCordinates(username, lon)
            print username + " has just logged in @@ " + now
            return jsonify("OK", u"  שעת התחברות " + now)
        else:
            return jsonify("FAIL", u" רכב לא תואם, נא לברר באופן אישי" + now)


@app.route('/updateYezia', methods=['GET', 'POST'])
def updateYezia():
    if request.method == 'POST':
        data = request.json
        username = request.json[0]["Username"]
        lat = request.json[0]["Lat"]
        lon = request.json[0]["Lon"]
        today = date.today()
        now = returnCurrentHour()
        if(isLoggedIn(username, today)):
            doYeziaa(username, today, now)
            updateLogoutLATCordinates(username, lat)
            updateLogoutLONGCordinates(username, lon)
            print username + " has just logged off @@ " + now
            return jsonify("OK", u" שעת יציאה עודכנה במערכת " + now)
        else:
            return jsonify("FAIL", u"נא להתחבר קודם")


@app.route('/updatePgisha', methods=['GET', 'POST'])
def updatePgisha():
    if request.method == 'POST':
        today = date.today()
        data = request.json
        print data
        username = request.json[0]['Username']
        name = request.json[0]['Name']
        title = request.json[0]['Esek']
        time = request.json[0]['Time']
        location = request.json[0]['Location']
        eishKesher = request.json[0]['EishKesher']
        longcor = request.json[0]['LongCor']
        latcor = request.json[0]['LatCor']
        sogEsek = request.json[0]['spinEsek']
        sogAnaf = request.json[0]['spinAnaf']
        sogTafkid = request.json[0]['spinTafkid']
        sogTozaa = request.json[0]['sogTozaa']
        now = returnCurrentHour()
        sendPgisha(username, title, time, location, eishKesher, latcor,
                   longcor, sogEsek, sogAnaf, sogTafkid, sogTozaa, name)
        print username + " has just sent Pgisha @@ " + now
        return jsonify("OK", u"נשלח בהצלחה!")


@app.route('/sendLog', methods=['GET', 'POST'])
def getCallLog():
    if request.method == 'POST':
        data = request.json
        username = request.json[0]['Username']
        fullname = request.json[0]['Userfullname']
        newlist = []
        counter = 0  # FOR ARRANGING CALL NUMBER IN CSV
        recieved = 0
        outgoing = 0
        time = datetime.now()
        now = time.strftime('%a %b %d')
        year = time.strftime('%Y')
        for n in range(len(data)):
            if(now in data[n]['Date'] and year in data[n]['Date']):
                if(u'נכנסת' in request.json[n]['Type']) or ('INCOMING' in request.json[n]['Type']):
                    recieved += 1
                elif(u'יוצאת' in request.json[n]['Type']) or ('OUTGOING' in request.json[n]['Type']):
                    outgoing += 1
        if(isCSVExists(username)):
            deleteCSV(username)
        for i in range(len(data)):
            if(now in data[i]['Date'] and year in data[i]['Date']):
                counter += 1
                try:
                    contact = request.json[i]['Contact']
                except:
                    contact = "איש קשר לא שמור "
                number = request.json[i]['Number']
                calltype = request.json[i]['Type']
                duration = request.json[i]['Duration']
                calldate = request.json[i]['Date']
                newlist.append(counter)
                try:
                    newlist.append(contact.encode('utf-8'))
                except:
                    newlist.append(contact)
                newlist.append(number.encode('utf-8'))
                newlist.append(calltype.encode('utf-8'))
                newlist.append(duration.encode('utf-8'))
                newlist.append(calldate.encode('utf-8'))
                createCSV(username, newlist)
                newlist = []
        updateTodayRecievedCalls(recieved, username)
        updateTodayOutGoingCalls(outgoing, username)
        updateTotalPgishot(fullname, username)
        response = jsonify("SUCCESS", u"נשלח בהצלחה")
        print username + " has just sent Phone Log @@ " + now
        return response


@app.route('/uploadimage', methods=['GET', 'POST'])
def upload_file():
    if request.method == 'POST':
        data = request.json
        username = request.json["username"]
        img = request.json["image"]
        today = date.today()
        now = returnCurrentHour()
        imgname = request.json["name"]
        photo = IMGFOLDER+"\\{}\\{}\\{}.jpg".format(username, today, imgname)
        createFolder(username, IMGFOLDER)
        createFolder(today, IMGFOLDER+"\\{}".format(username))
        with open(photo, "wb") as fh:
            fh.write(base64.b64decode(img))
    return u"הועלה בהצלחה"


@app.route('/isLoggedIn', methods=['GET', 'POST'])
def returnUserStatus():
    if request.method == 'POST':
        data = request.json
        username = request.json[0]["Username"]
        date = request.json[0]["Date"]
        now = returnCurrentHour()
        print "User: " + username
        print "Date: " + date
        if(selectIsLoggedIn(username, date)):
            print username + " Status: Connected @@ " + now
            return jsonify("OK", u"מחובר")

        else:
            print username + " Status: Disconnected @@ " + now
            return jsonify("FAIL", "Not connected")


@app.route('/getEmails', methods=['GET', 'POST'])
def returnEmails():
    if request.method == 'POST':
        data = request.json
        print data
        username = request.json[0]["Username"]
        name = request.json[0]["Name"]
        msgs = selectMessageAsJson(name)
        return msgs


@app.route('/logTable', methods=['GET', 'POST'])
def returnLog():
    if request.method == 'POST':
        data = request.data
        date = data.split("=")
        date = date[1].replace('&', '')
        log = getUserLog(date)
        return log


@app.route('/getTasks', methods=['GET', 'POST'])
def returnTasks():
    if request.method == 'POST':
        data = request.json
        username = request.json[0]["username"]
        tasks = getUserTasks(username)
        return tasks


@app.route('/getUsers', methods=['GET', 'POST'])
def getAllUsers():
    if request.method == 'POST':
        allUsers = getUsersInformation()
        return allUsers


@app.route('/userStatus', methods=['GET', 'POST'])
def returnStatus():
    if request.method == 'POST':
        data = request.json
        username = request.json[0]["username"]
        # 2019-07-29
        time = datetime.now()
        today = time.strftime('%Y-%m-%d')
        status = getUserStatus(username, today)
        return status


@app.route('/sendMessage', methods=['GET', 'POST'])
def sendMessage():
    if request.method == 'POST':
        data = request.json
        print data
        username = request.json[0]["username"]
        topic = request.json[0]["Topic"]
        message = request.json[0]["Message"]
        toWho = request.json[0]["ToWho"]
        name = request.json[0]["name"]
        time = datetime.now()
        today = time.strftime('%d/%m/%Y')
        insertMessage(username, name, topic, message, toWho, today)
        return jsonify("OK", u" נשלח בהצלחה")
        # else:
        # return jsonify("FAIL", u" נכשל" + now)


@app.route('/sendMesima', methods=['GET', 'POST'])
def sendMesima():
    if request.method == 'POST':
        data = request.json
        print data
        username = request.json[0]["username"]
        topic = request.json[0]["Topic"]
        task_date = request.json[0]["Date"]
        info = request.json[0]["Info"]
        toWho = request.json[0]["ToWho"]
        name = request.json[0]["name"]
        insertMesima(username, name, topic, info, toWho, task_date)
        return jsonify("OK", u" נשלח בהצלחה")


@app.route('/updateTaskStatus', methods=['GET', 'POST'])
def updateMesimaStatus():
    if request.method == 'POST':
        data = request.json
        username = request.json[0]["username"]
        task = request.json[0]["task"]
        info = request.json[0]["info"]
        date = request.json[0]["date"]
        updateMesimaStatusSQL(username, date, task, info)
        now = returnCurrentHour()
        print username + " has just updated Task Status @@ " + now
        return jsonify("OK", u" נשלח בהצלחה")


@app.route('/updateTaskVisibility', methods=['GET', 'POST'])
def updateMesimaVisibility():
    if request.method == 'POST':
        data = request.json
        username = request.json[0]["username"]
        task = request.json[0]["task"]
        info = request.json[0]["info"]
        date = request.json[0]["date"]
        updateMesimaVisibilitySQL(username, date, task, info)
        now = returnCurrentHour()
        print username + " has just removed task from his list @@ " + now
        return jsonify("OK", u" נשלח בהצלחה")


@app.route('/updateUserDetails', methods=['GET', 'POST'])
def updateUserDetails():
    if request.method == 'POST':
        data = request.json
        username = request.json[0]["Username"]
        newusername = request.json[0]["NewUsername"]
        password = request.json[0]["Password"]
        email = request.json[0]["Email"]
        name = request.json[0]["Name"]
        rank = request.json[0]["Rank"]
        updateUser(username, newusername, password, email, name, rank)
        now = returnCurrentHour()
        print username + " profile has been updated. @@ " + now
        return jsonify("OK", u" נשלח בהצלחה")


@app.route('/addUser', methods=['GET', 'POST'])
def addUser():
    if request.method == 'POST':
        data = request.json
        print data
        username = request.json[0]["Username"]
        password = request.json[0]["Password"]
        email = request.json[0]["Email"]
        name = request.json[0]["Name"]
        rank = request.json[0]["Rank"]
        insertUser(username, password, email, name, rank)
        now = returnCurrentHour()
        print username + " user has been created. @@ " + now
        return jsonify("OK", u" נרשם בהצלחה ")


@app.route('/deleteUser', methods=['GET', 'POST'])
def deleteUser():
    if request.method == 'POST':
        data = request.json
        print data
        username = request.json[0]["username"]
        removeUser(username)
        now = returnCurrentHour()
        print username + " user has been deleted. @@ " + now
        return jsonify("OK", u" נמחק בהצלחה ")


@app.route('/logUser', methods=['GET', 'POST'])
def logUser():
    if request.method == 'POST':
        data = request.json
        print data
        username = request.json[0]["Username"]
        password = request.json[0]["Password"]
        print "Username: " + username
        print "Password: " + password
        if (isExist(username)):
            response = authUser(username, password)
            return response
        else:
            return jsonify("NONE", u"שם משתמש לא קיים במערכת")


@app.route('/dashboard')
@login_required
def dashboard():
    return render_template('dashboard.html', name=current_user.username)


@app.route('/logout')
@login_required
def logout():
    logout_user()
    return redirect(url_for('index'))


if __name__ == '__main__':
    app.run(host='0.0.0.0', debug=True)
