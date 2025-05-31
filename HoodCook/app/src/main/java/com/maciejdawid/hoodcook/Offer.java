package com.maciejdawid.hoodcook;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "offers")
public class Offer {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String productName;
    public String category;
    public double pricePerUnit;
    public String sellerEmail;

    public Offer(String productName, String category, double pricePerUnit, String sellerEmail) {
        this.productName = productName;
        this.category = category;
        this.pricePerUnit = pricePerUnit;
        this.sellerEmail = sellerEmail;
    }

    public String getProductName() {
        return productName;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return pricePerUnit;
    }

    public String getSellerEmail() {
        return sellerEmail;
    }
}
