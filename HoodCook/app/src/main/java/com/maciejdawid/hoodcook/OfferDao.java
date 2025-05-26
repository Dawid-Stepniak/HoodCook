package com.maciejdawid.hoodcook;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface OfferDao {
    @Insert
    void insertOffer(Offer offer);

    @Query("SELECT * FROM offers")
    List<Offer> getAllOffers();
}