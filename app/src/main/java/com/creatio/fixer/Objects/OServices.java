package com.creatio.fixer.Objects;

/**
 * Created by Layge on 03/07/2017.
 */

public class OServices {
    String id_service, image, title, desc, time_pre, time_new, type, pieces;


    public OServices(String id_service, String image, String title, String desc, String time_pre, String time_new, String type, String pieces) {
        this.id_service = id_service;
        this.image = image;
        this.title = title;
        this.desc = desc;
        this.time_new = time_new;
        this.time_pre = time_pre;
        this.type = type;
        this.pieces = pieces;
    }

    public String getPieces() {
        return pieces;
    }

    public void setPieces(String pieces) {
        this.pieces = pieces;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime_pre() {
        return time_pre;
    }

    public void setTime_pre(String time_pre) {
        this.time_pre = time_pre;
    }

    public String getTime_new() {
        return time_new;
    }

    public void setTime_new(String time_new) {
        this.time_new = time_new;
    }

    public String getId_service() {
        return id_service;
    }

    public void setId_service(String id_service) {
        this.id_service = id_service;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return title;
    }
}
