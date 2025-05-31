package com.maciejdawid.hoodcook;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorites")
public class Favorite {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @NonNull
    public String userEmail;
    public int offerId;

    public Favorite(String userEmail, int offerId) {
        this.userEmail = userEmail;
        this.offerId = offerId;
    }

    public int getId() {
        return id;
    }

    public int getOfferId() {
        return offerId;
    }

    public String getUserEmail() {
        return userEmail;
    }
}