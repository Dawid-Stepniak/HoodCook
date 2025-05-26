package com.maciejdawid.hoodcook;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.Executors;

@Database(entities = {User.class, Offer.class}, version = 2)
public abstract class UserDatabase extends RoomDatabase {

    private static UserDatabase instance;

    public abstract UserDao userDao();

    public static synchronized UserDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            UserDatabase.class, "user_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            Executors.newSingleThreadExecutor().execute(() -> {
                                UserDatabase database = getInstance(context.getApplicationContext());
                                UserDao userDao = database.userDao();

                                userDao.insertUser(new User("Jan", "Kowalski", "jan.kowalski@example.com", "haslo123"));
                                userDao.insertUser(new User("Anna", "Nowak", "anna.nowak@example.com", "qwerty"));
                                userDao.insertUser(new User("Piotr", "Wiśniewski", "piotr.wisniewski@example.com", "123456"));
                            });
                        }
                    })
                    .build();
        }
        return instance;
    }
    public abstract OfferDao offerDao();
}
