package com.example.website.Emails;

import java.io.Serializable;

public class EmailsAdapter implements Serializable {

    private String message;
    private String date;
    private String from;
    private String topic;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }

    public String getFrom() {
        return from;
    }

}
