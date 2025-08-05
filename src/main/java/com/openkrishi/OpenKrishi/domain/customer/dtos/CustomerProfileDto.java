package com.openkrishi.OpenKrishi.domain.customer.dtos;


import java.time.LocalDateTime;
import java.util.UUID;

public class CustomerProfileDto {

   public String getFullName() {
       return fullName;
   }

   public String getEmail() {
       return email;
   }

   public String getPhone() {
       return phone;
   }


   public Double getLatitude() {
       return latitude;
   }


   public Double getLongitude() {
       return longitude;
   }

   public void setFullName(String fullName) {
       this.fullName = fullName;
   }

   public void setEmail(String email) {
       this.email = email;
   }

   public void setPhone(String phone) {
       this.phone = phone;
   }

   public void setLatitude(Double latitude) {
       this.latitude = latitude;
   }

   public void setLongitude(Double longitude) {
       this.longitude = longitude;
   }



   public LocalDateTime getCreatedAt() {
    return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

   private String fullName;

   private String email;

   private String phone;

    public UUID getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(UUID subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    private UUID subscriptionId;

   private Double latitude;
   private Double longitude;

    private LocalDateTime createdAt;
}
