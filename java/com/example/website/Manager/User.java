package com.example.website.Manager;

public class User {

    private int userID;
    private String Username;
    private String Password;
    private String PrivateName;
    private String Rank;
    private String Email;
    private Boolean isSelected;

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setUsername(String username) {
        this.Username = username;
    }

    public void setPassword(String password) {
        this.Password = password;
    }

    public void setPrivateName(String privateName) {
        this.PrivateName = privateName;
    }

    public void setRank(String rank) {
        this.Rank = rank;
    }

    public void setEmail(String email) {
        this.Email = email;
    }

    public int getUserID() {
        return userID;
    }

    public String getUsername() {
        return Username;
    }

    public String getPassword() {
        return Password;
    }

    public String getPrivateName() {
        return PrivateName;
    }

    public String getRank() {
        return Rank;
    }

    public String getEmail() {
        return Email;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
