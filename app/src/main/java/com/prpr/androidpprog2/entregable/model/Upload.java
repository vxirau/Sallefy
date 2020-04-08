package com.prpr.androidpprog2.entregable.model;


public class Upload {
    private String mImageUrl;

    public Upload() {

    }

    public Upload( String imageUrl) {
        mImageUrl = imageUrl;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

}
