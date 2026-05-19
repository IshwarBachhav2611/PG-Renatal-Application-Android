package com.roombuddy.pgrentalapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.roombuddy.pgrentalapp.R;
import com.roombuddy.pgrentalapp.model.BookingModel;
import com.roombuddy.pgrentalapp.utils.BookingRepository;

import java.util.List;

public class OwnerBookingAdapter
        extends RecyclerView.Adapter<OwnerBookingAdapter.ViewHolder> {

    private final Context context;
    private final List<BookingModel> bookingList;

    public OwnerBookingAdapter(Context context, List<BookingModel> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_owner_booking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        BookingModel booking = bookingList.get(position);

        holder.tvStudentName.setText(booking.getStudentName());
        holder.tvStudentEmail.setText(booking.getStudentEmail());
        holder.tvStudentPhone.setText(booking.getStudentPhone());
        holder.tvStatus.setText(booking.getStatus());

        switch (booking.getStatus().toLowerCase()) {

            case "accepted":
                holder.tvStatus.setBackgroundColor(Color.parseColor("#E8F5E9"));
                holder.tvStatus.setTextColor(Color.parseColor("#2E7D32"));
                holder.btnAccept.setEnabled(false);
                holder.btnReject.setEnabled(false);
                break;

            case "rejected":
                holder.tvStatus.setBackgroundColor(Color.parseColor("#FDECEA"));
                holder.tvStatus.setTextColor(Color.parseColor("#C62828"));
                holder.btnAccept.setEnabled(false);
                holder.btnReject.setEnabled(false);
                break;

            default:
                holder.tvStatus.setBackgroundColor(Color.parseColor("#FFF3CD"));
                holder.tvStatus.setTextColor(Color.parseColor("#856404"));
                holder.btnAccept.setEnabled(true);
                holder.btnReject.setEnabled(true);
                break;
        }

        holder.btnAccept.setOnClickListener(v -> {

            BookingRepository.updateStatus(
                    context,
                    booking.getStudentEmail(),
                    booking.getPgName(),
                    "Accepted"
            );

            booking.setStatus("Accepted");
            notifyItemChanged(position);

            Toast.makeText(context,
                    "Booking Accepted",
                    Toast.LENGTH_SHORT).show();
        });

        holder.btnReject.setOnClickListener(v -> {

            BookingRepository.updateStatus(
                    context,
                    booking.getStudentEmail(),
                    booking.getPgName(),
                    "Rejected"
            );

            booking.setStatus("Rejected");
            notifyItemChanged(position);

            Toast.makeText(context,
                    "Booking Rejected",
                    Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvStudentName, tvStudentEmail, tvStudentPhone, tvStatus;
        Button btnAccept, btnReject;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvStudentEmail = itemView.findViewById(R.id.tvStudentEmail);
            tvStudentPhone = itemView.findViewById(R.id.tvStudentPhone);
            tvStatus = itemView.findViewById(R.id.tvStatus);

            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}
