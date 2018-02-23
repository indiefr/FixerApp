package com.creatio.fixer.Objects;

import org.json.JSONArray;

/**
 * Created by Layge on 19/07/2017.
 */

public class OSpecialist {
    String id_specialis,name,last_name,title;

    public OSpecialist(String id_specialis, String name, String last_name, String title) {
        this.id_specialis = id_specialis;
        this.name = name;
        this.last_name = last_name;
        this.title = title;
    }

    public String getId_specialis() {
        return id_specialis;
    }

    public void setId_specialis(String id_specialis) {
        this.id_specialis = id_specialis;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}
