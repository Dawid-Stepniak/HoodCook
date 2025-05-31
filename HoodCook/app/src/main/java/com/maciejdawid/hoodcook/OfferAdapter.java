package com.maciejdawid.hoodcook;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Intent.getIntent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.ViewHolder> {

    private List<Offer> offers;
    private String currentUserEmail;
    private UserDatabase database;

    public OfferAdapter(List<Offer> offers, String currentUserEmail, UserDatabase database) {
        this.offers = offers;
        this.currentUserEmail = currentUserEmail;
        this.database = database;
    }

    public void updateList(List<Offer> newList) {
        this.offers = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OfferAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_offer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Offer offer = offers.get(position);
        holder.productName.setText(offer.getProductName());
        holder.category.setText(offer.getCategory());
        holder.price.setText(String.format("%.2f PLN", offer.getPrice()));
        holder.sellerEmail.setText(offer.getSellerEmail());

        Context context = holder.itemView.getContext();


        if(currentUserEmail == null) {
            SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            currentUserEmail = prefs.getString("user_email", null);
            Log.w("USER_EMAIL", "Pobrano email z SharedPreferences: " + currentUserEmail);
        }

        new Thread(() -> {
            try {
                Favorite fav = database.favoriteDao().getFavoriteByUserAndOffer(currentUserEmail, offer.id);
                boolean isFavorite = fav != null;

                holder.favoriteIcon.post(() -> {
                    holder.favoriteIcon.setImageResource(
                            isFavorite ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border
                    );
                });
            } catch (Exception e) {
                Log.e("OfferAdapter", "Błąd sprawdzania ulubionych", e);
            }
        }).start();

        holder.favoriteIcon.setOnClickListener(v -> {
            if (currentUserEmail == null || currentUserEmail.isEmpty()) {
                Toast.makeText(context, "Najpierw się zaloguj", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                try {
                    Favorite existing = database.favoriteDao().getFavoriteByUserAndOffer(currentUserEmail, offer.id);
                    if (existing == null) {
                        Favorite newFav = new Favorite(currentUserEmail, offer.id);
                        database.favoriteDao().insertFavorite(newFav);
                        Log.d("DB_DEBUG", "Dodano ulubioną: " + offer.id);

                        holder.favoriteIcon.post(() -> {
                            holder.favoriteIcon.setImageResource(R.drawable.ic_favorite_filled);
                            Toast.makeText(context, "Dodano do ulubionych", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        database.favoriteDao().deleteFavorite(existing);
                        Log.d("DB_DEBUG", "Usunięto ulubioną: " + offer.id);

                        holder.favoriteIcon.post(() -> {
                            holder.favoriteIcon.setImageResource(R.drawable.ic_favorite_border);
                            Toast.makeText(context, "Usunięto z ulubionych", Toast.LENGTH_SHORT).show();
                        });
                    }
                } catch (Exception e) {
                    Log.e("DB_ERROR", "Błąd bazy danych", e);
                    holder.favoriteIcon.post(() -> {
                        Toast.makeText(context, "Błąd operacji", Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();
        });
    }




    @Override
    public int getItemCount() {
        return offers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName, category, price, sellerEmail;
        ImageView favoriteIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.offerProductName);
            category = itemView.findViewById(R.id.offerCategory);
            price = itemView.findViewById(R.id.offerPrice);
            sellerEmail = itemView.findViewById(R.id.offerSellerEmail);
            favoriteIcon = itemView.findViewById(R.id.favoriteIcon);
        }
    }
}
