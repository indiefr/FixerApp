package com.creatio.fixer.Objects;

/**
 * Created by Layge on 19/07/2017.
 */

public class OMySpecialist {
    String id_specialis,name,last_name,desc,age,email,password;

    public OMySpecialist(String id_specialis, String name, String last_name, String desc, String age, String email, String password) {
        this.id_specialis = id_specialis;
        this.name = name;
        this.last_name = last_name;
        this.desc = desc;
        this.age = age;
        this.email = email;
        this.password = password;
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
