package com.mdislam.onestep.data;

import android.graphics.Bitmap;

/**
 * Created by mdislam on 2/26/16.
 */
public class Sermon {

    private String imageURL;
    private String title;
    private String speaker;
    private String link;
    private String downlaodURL;
    private Bitmap profileBit;
    private String church;
    private String duration;

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSpeaker() {
        return speaker;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDownlaodURL() {
        return downlaodURL;
    }

    public void setDownlaodURL(String downlaodURL) {
        this.downlaodURL = downlaodURL;
    }

    public Bitmap getProfileBit() {
        return profileBit;
    }

    public void setProfileBit(Bitmap profileBit) {
        this.profileBit = profileBit;
    }

    public String getChurch() {
        return church;
    }

    public void setChurch(String church) {
        this.church = church;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
