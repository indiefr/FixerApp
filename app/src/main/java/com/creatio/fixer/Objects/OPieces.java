package com.creatio.fixer.Objects;

/**
 * Created by Layge on 02/08/2017.
 */

public class OPieces {
    String id_piece,name,description,id_store, status, price, name_store;

    public OPieces(String id_piece, String name, String description, String id_store, String status, String price, String name_store) {
        this.id_piece = id_piece;
        this.name = name;
        this.description = description;
        this.id_store = id_store;
        this.status = status;
        this.price = price;
        this.name_store = name_store;
    }

    public String getName_store() {
        return name_store;
    }

    public void setName_store(String name_store) {
        this.name_store = name_store;
    }

    public String getId_piece() {
        return id_piece;
    }

    public void setId_piece(String id_piece) {
        this.id_piece = id_piece;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId_store() {
        return id_store;
    }

    public void setId_store(String id_store) {
        this.id_store = id_store;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
