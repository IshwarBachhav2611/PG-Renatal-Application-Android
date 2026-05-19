package com.roombuddy.pgrentalapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.roombuddy.pgrentalapp.R;
import com.roombuddy.pgrentalapp.model.ReviewModel;

import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.Holder> {

    private List<ReviewModel> list;

    public ReviewAdapter(List<ReviewModel> list) {

        this.list = (list != null) ? list : new ArrayList<>();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int position) {
        ReviewModel r = list.get(position);

        h.user.setText(
                r.getUserName() != null ? r.getUserName() : "Anonymous"
        );

        h.rating.setRating(r.getRating());

        h.comment.setText(
                r.getComment() != null ? r.getComment() : ""
        );
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateList(List<ReviewModel> newList) {
        this.list = (newList != null) ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class Holder extends RecyclerView.ViewHolder {

        TextView user, comment;
        RatingBar rating;

        Holder(@NonNull View v) {
            super(v);
            user = v.findViewById(R.id.txtUser);
            rating = v.findViewById(R.id.ratingBar);
            comment = v.findViewById(R.id.txtComment);
        }
    }
}
