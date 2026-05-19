package com.roombuddy.pgrentalapp.model;

public class PgModel {

    private int pgId;
    private String name;
    private String location;
    private String rent;
    private String type;
    private String address;
    private String description;
    private String ownerName;
    private String ownerPhone;
    private String ownerEmail;

    private String imageUri;
    private double latitude;
    private double longitude;
    private float avgRating;
    private int ratingCount;

    private float distanceKm = -1f;

    public PgModel() {
        this.latitude = 0.0;
        this.longitude = 0.0;
        this.avgRating = 0.0f;
        this.ratingCount = 0;
    }

    public PgModel(
            String name,
            String location,
            String rent,
            String type,
            String address,
            String description,
            String ownerName,
            String ownerPhone,
            String ownerEmail,
            String imageUri
    ) {
        this.name = name;
        this.location = location;
        this.rent = rent;
        this.type = type;
        this.address = address;
        this.description = description;
        this.ownerName = ownerName;
        this.ownerPhone = ownerPhone;
        this.ownerEmail = ownerEmail;
        this.imageUri = imageUri;
    }

    public PgModel(
            String name,
            String location,
            String rent,
            String type,
            String address,
            String description,
            String ownerName,
            String ownerPhone,
            String ownerEmail,
            String imageUri,
            double latitude,
            double longitude
    ) {
        this(
                name, location, rent, type, address, description,
                ownerName, ownerPhone, ownerEmail, imageUri
        );
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getPgId() { return pgId; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public String getRent() { return rent; }
    public String getType() { return type; }
    public String getAddress() { return address; }
    public String getDescription() { return description; }
    public String getOwnerName() { return ownerName; }
    public String getOwnerPhone() { return ownerPhone; }
    public String getOwnerEmail() { return ownerEmail; }
    public String getImageUri() { return imageUri; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public float getAvgRating() { return avgRating; }
    public int getRatingCount() { return ratingCount; }

    public float getDistanceKm() { return distanceKm; }

    public void setPgId(int pgId) { this.pgId = pgId; }
    public void setName(String name) { this.name = name; }
    public void setLocation(String location) { this.location = location; }
    public void setRent(String rent) { this.rent = rent; }
    public void setType(String type) { this.type = type; }
    public void setAddress(String address) { this.address = address; }
    public void setDescription(String description) { this.description = description; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public void setOwnerPhone(String ownerPhone) { this.ownerPhone = ownerPhone; }
    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }
    public void setImageUri(String imageUri) { this.imageUri = imageUri; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public void setAvgRating(float avgRating) { this.avgRating = avgRating; }
    public void setRatingCount(int ratingCount) { this.ratingCount = ratingCount; }

    public void setDistanceKm(float distanceKm) {
        this.distanceKm = distanceKm;
    }

    public int getRentAsInt() {
        try {
            return Integer.parseInt(rent.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public String toString() {
        return "PgModel{" +
                "pgId=" + pgId +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", rent='" + rent + '\'' +
                ", distanceKm=" + distanceKm +
                '}';
    }
}
