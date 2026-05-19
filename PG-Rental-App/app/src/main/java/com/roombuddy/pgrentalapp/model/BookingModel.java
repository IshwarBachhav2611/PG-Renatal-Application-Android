package com.roombuddy.pgrentalapp.model;

public class BookingModel {

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_ACCEPTED = "ACCEPTED";
    public static final String STATUS_REJECTED = "REJECTED";

    private String pgName;
    private String rent;
    private String pgAddress;
    private String pgCity;

    private String studentName;
    private String studentEmail;
    private String studentPhone;

    private String ownerEmail;
    private String status;

    public BookingModel(
            String pgName,
            String rent,
            String pgAddress,
            String pgCity,
            String studentName,
            String studentEmail,
            String studentPhone,
            String ownerEmail,
            String status
    ) {
        this.pgName = pgName;
        this.rent = rent;
        this.pgAddress = pgAddress;
        this.pgCity = pgCity;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.studentPhone = studentPhone;
        this.ownerEmail = ownerEmail;
        this.status = status;
    }

    public BookingModel(
            String pgName,
            String rent,
            String studentName,
            String studentEmail,
            String studentPhone,
            String ownerEmail,
            String status
    ) {
        this.pgName = pgName;
        this.rent = rent;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.studentPhone = studentPhone;
        this.ownerEmail = ownerEmail;
        this.status = status;

        this.pgAddress = "Address not available";
        this.pgCity = "";
    }

    public String getPgName() {
        return pgName;
    }

    public String getRent() {
        return rent;
    }

    public String getPgAddress() {
        return pgAddress;
    }

    public String getPgCity() {
        return pgCity;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public String getStudentPhone() {
        return studentPhone;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPgAddress(String pgAddress) {
        this.pgAddress = pgAddress;
    }

    public void setPgCity(String pgCity) {
        this.pgCity = pgCity;
    }
}
