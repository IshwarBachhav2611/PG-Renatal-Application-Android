package com.roombuddy.pgrentalapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.roombuddy.pgrentalapp.R;
import com.roombuddy.pgrentalapp.model.BookingModel;
import com.roombuddy.pgrentalapp.utils.BookingRepository;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {

    private final Context context;
    private final List<BookingModel> bookingList;

    public BookingAdapter(Context context, List<BookingModel> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_booking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        BookingModel booking = bookingList.get(position);

        holder.tvPgName.setText(booking.getPgName());
        holder.tvRent.setText("₹ " + booking.getRent() + " / Month");

        String address = booking.getPgAddress();
        String city = booking.getPgCity();

        if (address == null) address = "";
        if (city == null) city = "";

        if (!address.isEmpty() || !city.isEmpty()) {
            holder.tvAddress.setText(address + ", " + city);
        } else {
            holder.tvAddress.setText("Address not available");
        }


        String status = booking.getStatus().toUpperCase();
        holder.tvStatus.setText(status);

        switch (status) {

            case "ACCEPTED":
                holder.tvStatus.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.status_accepted_bg)
                );
                holder.tvStatus.setTextColor(
                        ContextCompat.getColor(context, R.color.status_accepted_text)
                );
                holder.btnCancel.setVisibility(View.GONE);
                break;

            case "REJECTED":
                holder.tvStatus.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.status_rejected_bg)
                );
                holder.tvStatus.setTextColor(
                        ContextCompat.getColor(context, R.color.status_rejected_text)
                );
                holder.btnCancel.setVisibility(View.GONE);
                break;

            default:
                holder.tvStatus.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.status_pending_bg)
                );
                holder.tvStatus.setTextColor(
                        ContextCompat.getColor(context, R.color.status_pending_text)
                );
                holder.btnCancel.setVisibility(View.VISIBLE);
                break;
        }

        holder.btnCancel.setOnClickListener(v -> {

            new AlertDialog.Builder(context)
                    .setTitle("Cancel Booking")
                    .setMessage("Are you sure you want to cancel this booking request?")
                    .setPositiveButton("Yes", (dialog, which) -> {

                        boolean deleted = BookingRepository.deleteBooking(
                                context,
                                booking.getStudentEmail(),
                                booking.getPgName()
                        );

                        if (deleted) {
                            bookingList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, bookingList.size());

                            Toast.makeText(context,
                                    "Booking cancelled successfully",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context,
                                    "Failed to cancel booking",
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvPgName, tvRent, tvAddress, tvStatus;
        Button btnCancel;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvPgName = itemView.findViewById(R.id.tvPgName);
            tvRent = itemView.findViewById(R.id.tvRent);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnCancel = itemView.findViewById(R.id.btnCancelBooking);
        }
    }
}
