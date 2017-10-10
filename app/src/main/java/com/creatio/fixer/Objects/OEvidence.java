package com.creatio.fixer.Objects;

/**
 * Created by Layge on 06/10/2017.
 */

public class OEvidence {
    String id_evidence, name, create_on;

    public OEvidence(String id_evidence, String name, String create_on) {

        this.id_evidence = id_evidence;
        this.name = name;
        this.create_on = create_on;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreate_on() {
        return create_on;
    }

    public void setCreate_on(String create_on) {
        this.create_on = create_on;
    }

    public String getId_evidence() {

        return id_evidence;
    }

    public void setId_evidence(String id_evidence) {
        this.id_evidence = id_evidence;
    }


}
