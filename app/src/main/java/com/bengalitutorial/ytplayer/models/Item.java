package com.bengalitutorial.ytplayer.models;

import com.google.gson.annotations.SerializedName;

public class Item {
    @SerializedName("kind")

    private String kind;
    @SerializedName("etag")

    private String etag;
    @SerializedName("id")

    private Id id;
    @SerializedName("snippet")

    private Snippet snippet;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public Snippet getSnippet() {
        return snippet;
    }

    public void setSnippet(Snippet snippet) {
        this.snippet = snippet;
    }
}
