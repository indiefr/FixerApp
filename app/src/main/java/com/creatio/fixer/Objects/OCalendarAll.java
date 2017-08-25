package com.creatio.fixer.Objects;

import org.json.JSONArray;

/**
 * Created by Layge on 19/07/2017.
 */

public class OCalendarAll {
    String id_specialis,init_date,finish_date,commits,status;

    public OCalendarAll(String id_specialis, String init_date, String finish_date, String commits, String status) {
        this.id_specialis = id_specialis;
        this.init_date = init_date;
        this.finish_date = finish_date;
        this.commits = commits;
        this.status = status;
    }

    public String getId_specialis() {
        return id_specialis;
    }

    public void setId_specialis(String id_specialis) {
        this.id_specialis = id_specialis;
    }

    public String getInit_date() {
        return init_date;
    }

    public void setInit_date(String init_date) {
        this.init_date = init_date;
    }

    public String getFinish_date() {
        return finish_date;
    }

    public void setFinish_date(String finish_date) {
        this.finish_date = finish_date;
    }

    public String getCommits() {
        return commits;
    }

    public void setCommits(String commits) {
        this.commits = commits;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
