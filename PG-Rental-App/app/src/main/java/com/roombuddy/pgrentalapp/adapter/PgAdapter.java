package com.roombuddy.pgrentalapp.adapter;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.roombuddy.pgrentalapp.R;
import com.roombuddy.pgrentalapp.model.PgModel;
import com.roombuddy.pgrentalapp.utils.RatingRepository;

import java.util.List;

public class PgAdapter extends RecyclerView.Adapter<PgAdapter.PgViewHolder> {

    public interface OnPgClickListener {
        void onPgClick(PgModel pg);
    }

    private final Context context;
    private final List<PgModel> pgList;
    private final OnPgClickListener listener;

    private double userLat = 0.0;
    private double userLng = 0.0;

    public PgAdapter(Context context, List<PgModel> pgList, OnPgClickListener listener) {
        this.context = context;
        this.pgList = pgList;
        this.listener = listener;
    }

    public void setUserLocation(double lat, double lng) {
        this.userLat = lat;
        this.userLng = lng;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PgViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_pg, parent, false);
        return new PgViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PgViewHolder holder, int position) {

        PgModel pg = pgList.get(position);

        holder.txtName.setText(pg.getName());
        holder.txtLocation.setText(pg.getLocation());
        holder.txtRent.setText("₹ " + pg.getRent() + " / month");

        float avgRating = RatingRepository.getAverageRating(context, pg.getName());
        holder.ratingBar.setRating(avgRating);

        if (pg.getImageUri() != null && !pg.getImageUri().isEmpty()) {
            holder.imgPg.setImageURI(Uri.parse(pg.getImageUri()));
        } else {
            holder.imgPg.setImageResource(R.drawable.ic_launcher_background);
        }

        if (pg.getLatitude() == 0.0 || pg.getLongitude() == 0.0
                || userLat == 0.0 || userLng == 0.0) {

            holder.txtDistance.setText("Near");

        } else {
            float[] result = new float[1];
            Location.distanceBetween(
                    userLat,
                    userLng,
                    pg.getLatitude(),
                    pg.getLongitude(),
                    result
            );

            float km = result[0] / 1000f;
            holder.txtDistance.setText(String.format("%.1f km away", km));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPgClick(pg);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pgList != null ? pgList.size() : 0;
    }

    static class PgViewHolder extends RecyclerView.ViewHolder {

        ImageView imgPg;
        TextView txtName, txtLocation, txtRent, txtDistance;
        RatingBar ratingBar;

        public PgViewHolder(@NonNull View itemView) {
            super(itemView);

            imgPg = itemView.findViewById(R.id.imgPg);
            txtName = itemView.findViewById(R.id.txtPgName);
            txtLocation = itemView.findViewById(R.id.txtLocation);
            txtRent = itemView.findViewById(R.id.txtRent);
            txtDistance = itemView.findViewById(R.id.txtDistance);
            ratingBar = itemView.findViewById(R.id.ratingBarPg);
        }
    }
}
