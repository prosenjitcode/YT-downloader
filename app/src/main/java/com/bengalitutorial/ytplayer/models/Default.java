package com.bengalitutorial.ytplayer.models;

import com.google.gson.annotations.SerializedName;

public class Default {

    @SerializedName("url")
    private String url;

    @SerializedName("width")
    private Integer width;

    @SerializedName("height")
    private Integer height;


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

}