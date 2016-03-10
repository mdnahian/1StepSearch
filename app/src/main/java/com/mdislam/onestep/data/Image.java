package com.mdislam.onestep.data;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by mdislam on 12/30/15.
 */
public class Image implements Serializable{

    private String imageURL;
    private Bitmap imageBit;

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public Bitmap getImageBit() {
        return imageBit;
    }

    public void setImageBit(Bitmap imageBit) {
        this.imageBit = imageBit;
    }
}
