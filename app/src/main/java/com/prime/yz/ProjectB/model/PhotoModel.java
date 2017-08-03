package com.prime.yz.ProjectB.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.prime.yz.ProjectB.common.Pageable;

import java.io.Serializable;

/**
 * Created by KKT on 8/1/2017.
 **/

public class PhotoModel implements Serializable,Pageable {
    @Expose
    @SerializedName("id")
    private int id;

    @Expose
    @SerializedName("albumId")
    private int albumId;

    @Expose
    @SerializedName("title")
    private String title;

    @Expose
    @SerializedName("url")
    private String url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "PhotoModel{" +
                "id=" + id +
                ", albumId=" + albumId +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
