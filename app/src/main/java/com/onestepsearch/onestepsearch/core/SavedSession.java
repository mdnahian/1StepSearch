package com.onestepsearch.onestepsearch.core;

import java.io.Serializable;

/**
 * Created by mdislam on 4/14/16.
 */
public class SavedSession implements Serializable {

    private String fname;
    private String lname;
    private String username;
    private String email;
    private String phone;
    private String plan;
    private int numOfSearches;
    private int currentNumOfSearches;
    private String emailVerification;
    private String planExpiration;

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public int getNumOfSearches() {
        return numOfSearches;
    }

    public void setNumOfSearches(int numOfSearches) {
        this.numOfSearches = numOfSearches;
    }

    public int getCurrentNumOfSearches() {
        return currentNumOfSearches;
    }

    public void setCurrentNumOfSearches(int currentNumOfSearches) {
        this.currentNumOfSearches = currentNumOfSearches;
    }

    public String getEmailVerification() {
        return emailVerification;
    }

    public void setEmailVerification(String emailVerification) {
        this.emailVerification = emailVerification;
    }

    public String getPlanExpiration() {
        return planExpiration;
    }

    public void setPlanExpiration(String planExpiration) {
        this.planExpiration = planExpiration;
    }
}
