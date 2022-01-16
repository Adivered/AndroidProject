# -*- coding: utf-8 -*-
from flask import Flask, render_template, redirect, url_for, request, jsonify
from datetime import datetime, date
import sqlite3,os, urllib2,json, csv
from flask_sqlalchemy import SQLAlchemy
import json

def setConnSQLConnection():
            conn = sqlite3.connect('UserDataBase.db')
            return conn

def setCurSQLConnection(conn):
            cur = conn.cursor()
            return cur
            
def closeSQLConnection(conn):
            conn = conn
            conn.commit()
            conn.close()      
      
def createFolder(nameforfolder,path):
    if os.path.exists(path+'\\{}'.format(nameforfolder)):
         pass
    else:
         os.mkdir(path+'\\{}'.format(nameforfolder))

def returnCurrentHour():
        time = datetime.now()
        curr_hour = time.strftime('%H:%M:%S')
        return curr_hour


def doLogin(username,date,hour):
      conn = setConnSQLConnection()
      cur = setCurSQLConnection(conn)
      sql = "INSERT INTO `users_log` (`username`,`loggedin`, `date`,`loginhour`) VALUES (?, 1, ?,?)"
      cur.execute(sql, (username, date , hour))
      closeSQLConnection(conn)

def doLogout(username,date,hour):
      conn = setConnSQLConnection()
      cur = setCurSQLConnection(conn)
      sql = "UPDATE  `users_log` SET `logouthour`=? WHERE `username` =? AND `date`=? AND `logouthour` is NULL"
      cur.execute(sql, (hour, username,date))
      sql2 = "UPDATE  `users_log` SET `loggedin`=0 WHERE `username` =? AND `date`=? AND `loggedin` is 1"
      cur.execute(sql2, (username,date))
      closeSQLConnection(conn)

def updeteCallsLog(number,username):
        today = date.today()
        conn = setConnSQLConnection()
        cur = setCurSQLConnection(conn)
        sql = "UPDATE `users_log` SET `totalRecievedCalls`=? WHERE `username` =? AND `date`=? AND `totalRecievedCalls` is 0"
        cur.execute(sql, (number, username,today))
        closeSQLConnection(conn)

def updateTodayOutGoingCalls(number,username):
        today = date.today()
        conn = setConnSQLConnection()
        cur = setCurSQLConnection(conn)
        sql = "UPDATE `users_log` SET `totalDialedCalls`=? WHERE `username` =? AND `date`=? AND `totalDialedCalls` is 0"
        cur.execute(sql, (number, username,today))
        closeSQLConnection(conn)

def updateTotalMeetings(name,username):
        time = datetime.now()
        today = time.strftime('%d/%m/%y')
        conn = setConnSQLConnection()
        cur = setCurSQLConnection(conn)
        sql = "SELECT * FROM `Meetings` WHERE `Name` LIKE '"+name+"' AND `Date` LIKE '"+today+"'"
        userinfo = cur.execute(sql).fetchall()
        num = len(userinfo)
        today = date.today()
        sql2 = "UPDATE `users_log` SET `Meetings`=? WHERE `username` =? AND `date`=? AND `totalMeetings` = 0 AND `loggedin` = 1"
        cur.execute(sql2, (num, username,today))
        closeSQLConnection(conn)

def updateLoginLATCordinates(username, lat):
  if(isLoggedIn):
    today=date.today()
    conn = setConnSQLConnection()
    cur = setCurSQLConnection(conn)
    sql = "UPDATE `users_log` SET `LOGINLAT`=? WHERE `username` =? AND `date`=? AND `LOGINLAT` is NULL"
    cur.execute(sql, (lat, username,today))
    closeSQLConnection(conn)

def updateLogoutLONGCordinates(username, lon):
  if(isLoggedIn):
    today=date.today()
    conn = setConnSQLConnection()
    cur = setCurSQLConnection(conn)
    sql = "UPDATE `users_log` SET `LOGOUTLONG`=? WHERE `username` =? AND `date`=? AND `LOGOUTLONG` is NULL"
    cur.execute(sql, (lon, username,today))
    closeSQLConnection(conn)

    
def updateLoginLONGCordinates(username, lon):
  if(isLoggedIn):
    today=date.today()
    conn = setConnSQLConnection()
    cur = setCurSQLConnection(conn)
    sql = "UPDATE `users_log` SET `LOGINLONG`=? WHERE `username` =? AND `date`=? AND `LOGINLONG` is NULL"
    cur.execute(sql, (lon, username,today))
    closeSQLConnection(conn)

def updateLogoutLATCordinates(username, lat):
  if(isLoggedIn):
    today=date.today()
    conn = setConnSQLConnection()
    cur = setCurSQLConnection(conn)
    sql = "UPDATE `users_log` SET `LOGOUTLAT`=? WHERE `username` =? AND `date`=? AND `LOGOUTLAT` is NULL"
    cur.execute(sql, (lat, username,today))
    closeSQLConnection(conn)
      


def getUserLog(date):
      conn = setConnSQLConnection()
      cur = setCurSQLConnection(conn)
      sql = "SELECT `username`,`loggedin`,`loginhour`,`logouthour` FROM `users_log` WHERE `date` LIKE '"+date+"'"
      log = cur.execute(sql).fetchall()
      columns = [desc[0] for desc in cur.description]
      result = []
      closeSQLConnection(conn)
      if(len(log) == 0):
        return ""
      for row in log:
          row = dict(zip(columns, row))
          result.append(row)
      return jsonify(result)


def getUserStatus(username,date):
      conn = setConnSQLConnection()
      cur = setCurSQLConnection(conn)
      sql = "SELECT * FROM `users_log` WHERE `username` LIKE '"+username+"' AND `date` LIKE '"+date+"'"
      log = cur.execute(sql).fetchall()
      columns = [desc[0] for desc in cur.description]
      result = []
      closeSQLConnection(conn)
      if(len(log) == 0):
        return jsonify("NONE","NONE")
      for row in log:
          row = dict(zip(columns, row))
          result.append(row)
      return jsonify(result)


def getUserTasks(username):
      conn = setConnSQLConnection()
      cur = setCurSQLConnection(conn)
      sql = "SELECT * FROM `Tasks` WHERE `Username` LIKE '"+username+"' AND `ISDONE` is 0 AND `Visible` is 0"
      log = cur.execute(sql).fetchall()
      columns = [desc[0] for desc in cur.description]
      result = []
      closeSQLConnection(conn)
      if(len(log) == 0):
        return jsonify("NONE", u"אין משימות נוספות")
      for row in log:
          row = dict(zip(columns, row))
          result.append(row)
      return jsonify(result)

def getUsersInformation():
    conn = setConnSQLConnection()
    cur = setCurSQLConnection(conn)
    sql = "SELECT * FROM `user`"
    log = cur.execute(sql).fetchall()
    columns = [desc[0] for desc in cur.description]
    result = []
    closeSQLConnection(conn)
    if(len(log) == 0):
        return jsonify("NONE", u"אין משימות נוספות")
    for row in log:
        row = dict(zip(columns, row))
        result.append(row)
    return jsonify(result)


def selectMessageAsJson(name):
      conn = setConnSQLConnection()
      cur = setCurSQLConnection(conn)
      All = u"כולם"
      msgs = cur.execute("SELECT * FROM Messages WHERE `Visible` LIKE '"+name+"' OR `Visible` LIKE '"+All+"'").fetchall()
      columns = [desc[0] for desc in cur.description]
      result = []
      if(len(msgs) == 0):
        return jsonify("NONE", u"לא נמצאו הודעות")
      for row in msgs:
          row = dict(zip(columns, row))
          result.append(row)
      closeSQLConnection(conn)
      return jsonify(result)
    


def insertMessage(username,name,topic,message,toWho,date):
      conn = setConnSQLConnection()
      cur = setCurSQLConnection(conn)
      sql = "INSERT INTO `Messages` (`Topic`,`Message`, `Visible`,`Date`,`From`) VALUES (?, ?, ?,?,?)"
      cur.execute(sql, (topic,message,toWho, date , name))
      closeSQLConnection(conn)


def updateUser(username,newusername,password,email,name,rank):
    conn = setConnSQLConnection()
    cur = setCurSQLConnection(conn)
    sql = "UPDATE `user` SET `password`=? WHERE `username` =?"
    cur.execute(sql, (password,username))
    sql = "UPDATE `user` SET `email`=? WHERE `username` =?"
    cur.execute(sql, (email,username))
    print "New name: " + name
    sql = "UPDATE `user` SET `name`=? WHERE `username` =?"
    cur.execute(sql, (name,username))
    sql = "UPDATE `user` SET `rank`=? WHERE `username` =?"
    cur.execute(sql, (rank,username))
    sql = "UPDATE `user` SET `username`=? WHERE `username` =?"
    cur.execute(sql, (newusername,username))    
    closeSQLConnection(conn)

def insertUser(username,password,email,name,rank):
    conn = setConnSQLConnection()
    cur = setCurSQLConnection(conn)
    sql = "INSERT INTO `user` (`username`, `password`,`email`,`name`,`rank`) VALUES (?,?,?,?,?)"
    cur.execute(sql,(username,password,email,name,rank))
    closeSQLConnection(conn)

def removeUser(username):
    conn = setConnSQLConnection()
    cur = setCurSQLConnection(conn)
    sql = "DELETE FROM `user` WHERE `username` LIKE '"+username+"'"
    cur.execute(sql)
    closeSQLConnection(conn)

def isExist(user):
    conn = setConnSQLConnection()
    cur = setCurSQLConnection(conn)
    sql = "SELECT * FROM `user` WHERE `username` LIKE '"+user+"'"
    log = cur.execute(sql).fetchall()
    columns = [desc[0] for desc in cur.description]
    result = []
    closeSQLConnection(conn)
    if(len(log) == 0):
        return False
    for row in log:
        row = dict(zip(columns, row))
        result.append(row)
    if(result[0]["username"] ==user):
        return True
    else:
        return False

def authUser(user,password):
    conn = setConnSQLConnection()
    cur = setCurSQLConnection(conn)
    sql = "SELECT * FROM `user` WHERE `username` LIKE '"+user+"' AND `password` LIKE '"+password+"'"
    log = cur.execute(sql).fetchall()
    columns = [desc[0] for desc in cur.description]
    result = []
    closeSQLConnection(conn)
    if(len(log) == 0):
        return jsonify("WRONG", u"סיסמא לא נכונה")
    else:
        for row in log:
            row = dict(zip(columns, row))
            result.append(row)
        return jsonify(result)
    
  
def isLoggedIn(username,date):
    isloggedin = False
    userinfo = selectKnisaSQL(username,date)
    try:
      if(userinfo[0][0] == 1):
          isloggedin = True
    except:
      pass
    return isloggedin

