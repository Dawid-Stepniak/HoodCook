package com.maciejdawid.hoodcook;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;

import java.util.List;

@Dao
public interface FavoriteDao {

    @Insert
    void insertFavorite(Favorite favorite);

    @Delete
    void deleteFavorite(Favorite favorite);

    @Query("SELECT offers.* FROM offers INNER JOIN favorites ON offers.id = favorites.offerId WHERE favorites.userEmail = :userEmail")
    List<Offer> getFavoritesForUser(String userEmail);

    @Query("SELECT * FROM favorites WHERE userEmail = :userEmail AND offerId = :offerId LIMIT 1")
    Favorite getFavoriteByUserAndOffer(String userEmail, int offerId);
}
