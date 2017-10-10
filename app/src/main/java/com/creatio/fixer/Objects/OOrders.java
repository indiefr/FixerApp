package com.creatio.fixer.Objects;

/**
 * Created by Layge on 20/07/2017.
 */

public class OOrders {
    String id_order;
    String create_on;
    String total;
    String subtotal;
    String lat_lng;
    String init_date;
    String id_specialist;
    String name;
    String id_calendary;
    String name_user;
    String last_name_user;
    String status_sc;
    String status_so;
    String id_user;
    String hour_date;
    String hour_date_service;
    String service_date;
    String rate;
    String reference;


    public OOrders(String id_order, String create_on, String total, String subtotal, String lat_lng, String init_date, String id_specialist, String name, String id_calendary, String name_user, String last_name_user, String status_sc, String status_so, String id_user, String hour_date, String hour_date_service, String service_date, String rate, String reference) {
        this.id_order = id_order;
        this.create_on = create_on;
        this.total = total;
        this.subtotal = subtotal;
        this.lat_lng = lat_lng;
        this.init_date = init_date;
        this.id_specialist = id_specialist;
        this.name = name;
        this.id_calendary = id_calendary;
        this.name_user = name_user;
        this.last_name_user = last_name_user;
        this.status_sc = status_sc;
        this.status_so = status_so;
        this.id_user = id_user;
        this.hour_date = hour_date;
        this.hour_date_service = hour_date_service;
        this.service_date = service_date;
        this.rate = rate;
        this.reference = reference;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getHour_date_service() {
        return hour_date_service;
    }

    public void setHour_date_service(String hour_date_service) {
        this.hour_date_service = hour_date_service;
    }

    public String getService_date() {
        return service_date;
    }

    public void setService_date(String service_date) {
        this.service_date = service_date;
    }

    public String getHour_date() {
        return hour_date;
    }

    public void setHour_date(String hour_date) {
        this.hour_date = hour_date;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getStatus_sc() {
        return status_sc;
    }

    public void setStatus_sc(String status_sc) {
        this.status_sc = status_sc;
    }

    public String getStatus_so() {
        return status_so;
    }

    public void setStatus_so(String status_so) {
        this.status_so = status_so;
    }

    public String getName_user() {
        return name_user;
    }

    public void setName_user(String name_user) {
        this.name_user = name_user;
    }

    public String getLast_name_user() {
        return last_name_user;
    }

    public void setLast_name_user(String last_name_user) {
        this.last_name_user = last_name_user;
    }

    public String getId_calendary() {
        return id_calendary;
    }

    public void setId_calendary(String id_calendary) {
        this.id_calendary = id_calendary;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInit_date() {
        return init_date;
    }

    public void setInit_date(String init_date) {
        this.init_date = init_date;
    }

    public String getId_specialist() {
        return id_specialist;
    }

    public void setId_specialist(String id_specialist) {
        this.id_specialist = id_specialist;
    }

    public String getId_order() {
        return id_order;
    }

    public void setId_order(String id_order) {
        this.id_order = id_order;
    }

    public String getCreate_on() {
        return create_on;
    }

    public void setCreate_on(String create_on) {
        this.create_on = create_on;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    public String getLat_lng() {
        return lat_lng;
    }

    public void setLat_lng(String lat_lng) {
        this.lat_lng = lat_lng;
    }
}
