package com.example.website.Permissions;

public class URL {

    String server_url = "http://192.168.21.118:5000";

    public String getServer_url(String url) {
        if(url != null){
            switch(url){
                case "logUser": // Main Activity - LogUser();
                    return server_url + "/logUser";
                case "isLoggedIn": // Main Activity - authURL();
                    return server_url + "/isLoggedIn";
                case "updatePgisha": // ReportMeeting - updatPgisha();
                    return server_url + "/updatePgisha";
                case "uploadImage": //Knisa - uploadImage()
                    return server_url + "/uploadimage";
                case "sendLog": // Knisa - sendLog();
                    return server_url + "/sendLog";
                case "updateKnisa": // Knisa - updateKnisa()
                    return server_url + "/updateKnisa";
                case "userStatus": // Knisa - userStatus()
                    return server_url + "/userStatus";
                case "updateYezia": // Knisa - updateYeziaa
                    return server_url + "/updateYezia";
                case "getTasks": // Tasks - getTasks()
                    return server_url + "/getTasks";
                case "updateTaskVisibility": //Tasks -- updateTaskVisibility
                    return server_url + "/updateTaskVisibility";
                case "updateTaskStatus": //Tasks -- updateTaskStatus
                    return server_url + "/updateTaskStatus";
                case "getEmails": //Emails -- getEmails()
                    return server_url + "/getEmails";
                case "logTable":
                    return server_url + "/logTable"; // ManagerPage -- askForLog()
                case "sendMessage":
                    return server_url + "/sendMessage"; // ManagerPage -- sendMessage()
                case "sendMesima":
                    return server_url + "/sendMesima"; // ManagerPage -- sendMesima()
                case "updateUserDetails":
                    return server_url + "/updateUserDetails"; // ManagerPage - UserInfo - updateUserDetails()
                case "getUsers":
                    return server_url + "/getUsers";
                case "addUser":
                    return server_url + "/addUser";
                case "deleteUser":
                    return server_url + "/deleteUser";
                /*case "":
                    return server_url + "/";
                case "":
                    return server_url + "/";*/
            }
        }
        return server_url;
    }
}
