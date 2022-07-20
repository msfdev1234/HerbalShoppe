package com.madmobiledevs.ecommerce.Model;

public class AdminOrders {

    private String name, phone, address, city, state, date, time, totalAmount, orderId, phoneKey, paymentStatus, grandTotal, userID, cGst, Gst, deliveryCharges, transactionId;

    public AdminOrders() {
    }

    public AdminOrders(String name, String phone, String address, String city, String state, String date, String time, String totalAmount, String orderId, String phoneKey, String paymentStatus, String grandTotal, String userID, String cGst, String gst, String deliveryCharges, String transactionId) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.state = state;
        this.date = date;
        this.time = time;
        this.totalAmount = totalAmount;
        this.orderId = orderId;
        this.phoneKey = phoneKey;
        this.paymentStatus = paymentStatus;
        this.grandTotal = grandTotal;
        this.userID = userID;
        this.cGst = cGst;
        Gst = gst;
        this.deliveryCharges = deliveryCharges;
        this.transactionId = transactionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPhoneKey() {
        return phoneKey;
    }

    public void setPhoneKey(String phoneKey) {
        this.phoneKey = phoneKey;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(String grandTotal) {
        this.grandTotal = grandTotal;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getcGst() {
        return cGst;
    }

    public void setcGst(String cGst) {
        this.cGst = cGst;
    }

    public String getGst() {
        return Gst;
    }

    public void setGst(String gst) {
        Gst = gst;
    }

    public String getDeliveryCharges() {
        return deliveryCharges;
    }

    public void setDeliveryCharges(String deliveryCharges) {
        this.deliveryCharges = deliveryCharges;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}

