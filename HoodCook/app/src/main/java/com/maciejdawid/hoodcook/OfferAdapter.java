package com.maciejdawid.hoodcook;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        offers.clear();
        offers.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OfferAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_offer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferAdapter.ViewHolder holder, int position) {
        Offer offer = offers.get(position);
        holder.productName.setText(offer.getProductName());
        holder.category.setText(offer.getCategory());
        holder.price.setText(String.format("%.2f PLN", offer.getPrice()));
        holder.sellerEmail.setText(offer.getSellerEmail());
        new Thread(() -> {
            Favorite fav = database.favoriteDao().getFavoriteByUserAndOffer(currentUserEmail, offer.id);
            boolean isFavorite = fav != null;
            holder.favoriteIcon.post(() -> {
                holder.favoriteIcon.setImageResource(isFavorite ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border);
            });
        }).start();
        holder.favoriteIcon.setOnClickListener(v -> {
            new Thread(() -> {
                Favorite fav = database.favoriteDao().getFavoriteByUserAndOffer(currentUserEmail, offer.id);
                if (fav == null) {
                    database.favoriteDao().insertFavorite(new Favorite(currentUserEmail, offer.id));
                    holder.favoriteIcon.post(() -> holder.favoriteIcon.setImageResource(R.drawable.ic_favorite_filled));
                } else {
                    database.favoriteDao().deleteFavorite(fav);
                    holder.favoriteIcon.post(() -> holder.favoriteIcon.setImageResource(R.drawable.ic_favorite_border));
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
