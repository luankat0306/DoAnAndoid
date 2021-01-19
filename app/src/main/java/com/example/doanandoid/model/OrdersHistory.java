package com.example.doanandoid.model;


import java.util.Date;

public class OrdersHistory {

    private String id;
    private String datePay;
    private String listFood;
    private int totalPrice;

    public OrdersHistory() {}

    public OrdersHistory(String id, String datePay, String listFood, int totalPrice) {
        this.id = id;
        this.datePay = datePay;
        this.listFood = listFood;
        this.totalPrice = totalPrice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDatePay() {
        return datePay;
    }

    public void setDatePay(String datePay) {
        this.datePay = datePay;
    }

    public String getListFood() {
        return listFood;
    }

    public void setListFood(String listFood) {
        this.listFood = listFood;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }
}
