package com.mdislam.onestep.CORE;

import android.graphics.Bitmap;

/**
 * Created by mdislam on 12/26/15.
 */
public class Video {

    private String title;
    private String videoURL;
    private String thumbnail;
    private String meta;
    private String description;
    private String downloadURL;
    private Bitmap thumbnailBit;
    private boolean isDownloading = false;


    public String getTitle() {
        return title;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getMeta() {
        return meta;
    }

    public String getDescription() {
        return description;
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }


    public Bitmap getThumbnailBit() {
        return thumbnailBit;
    }

    public void setThumbnailBit(Bitmap thumbnailBit) {
        this.thumbnailBit = thumbnailBit;
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public void setIsDownloading(boolean isDownloading) {
        this.isDownloading = isDownloading;
    }
}
