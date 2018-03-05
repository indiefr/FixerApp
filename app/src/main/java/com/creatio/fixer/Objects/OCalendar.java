package com.creatio.fixer.Objects;

import org.json.JSONArray;

/**
 * Created by Layge on 19/07/2017.
 */

public class OCalendar {
    String id_specialis, name, last_name, age, description, phone, status, extra;
    JSONArray objCalendar;

    public OCalendar(String id_specialis, String name, String last_name, String age, String description, String phone, String status, String extra, JSONArray objCalendar) {
        this.id_specialis = id_specialis;
        this.name = name;
        this.last_name = last_name;
        this.age = age;
        this.description = description;
        this.phone = phone;
        this.status = status;
        this.extra = extra;
        this.objCalendar = objCalendar;
    }

    public JSONArray getObjCalendar() {
        return objCalendar;
    }

    public void setObjCalendar(JSONArray objCalendar) {
        this.objCalendar = objCalendar;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
