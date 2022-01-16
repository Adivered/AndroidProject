package com.example.website;

public class LogInfo {

        private String name , loginhour, logouthour, loggedin;


        public String getName(){
            return name;
        }

        public void setName(String name){
            this.name = name;
        }

        public String getIsLoggedIn() {
            if (loggedin.equals("1")) {
                return "מחובר";
            } else {
                return "מנותק";
            }
        }

        public void setLoggedIn(String loggedin){
            this.loggedin = loggedin;
        }

        public String getLoginHour() {
            return loginhour;
        }

        public void setLoginHour(String hour) {
            this.loginhour = hour;
        }

        public String getLogoutHour() {
            if(logouthour.length() < 5){
                return "עדיין לא התנתק";
            }
            else {
                return logouthour;
            }
        }

        public void setLogoutHour(String hour) {
            this.logouthour = hour;
        }

    }
