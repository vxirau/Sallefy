package com.prpr.androidpprog2.entregable.model;


public class Upload {
    private String mImageUrl;
    private String mKey;

    public Upload() {

    }

    public String getKey(){
        return mKey;
    }

    public void setKey(String mKey){
        this.mKey = mKey;
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
