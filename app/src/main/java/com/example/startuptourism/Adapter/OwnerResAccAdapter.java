package com.example.startuptourism.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.startuptourism.Database.RoomDb.Entity.Reservation;
import com.example.startuptourism.Helper.DateHelp;
import com.example.startuptourism.databinding.LstReservationPowBinding;

import java.util.List;

public class OwnerResAccAdapter extends RecyclerView.Adapter<OwnerResAccAdapter.ViewHolder> {
    private final Context context;
    private List<Reservation> reservationList;
    private final ReservationClick reservationClick;
    private final int mode;

    public OwnerResAccAdapter(Context context, List<Reservation> reservationList, ReservationClick reservationClick, int mode) {
        this.context = context;
        this.reservationList = reservationList;
        this.reservationClick = reservationClick;
        this.mode = mode;
    }

    public void refreshList(List<Reservation> reservationList) {
        this.reservationList = reservationList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OwnerResAccAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LstReservationPowBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OwnerResAccAdapter.ViewHolder holder, int position) {
        Reservation reservation = reservationList.get(position);
        if (mode == 0)
            holder.root.layoutAccom.setVisibility(View.GONE);
        else {
            holder.root.layoutAccom.setVisibility(View.VISIBLE);
            holder.root.txtAddress.setText(reservation.getReserveAccomPlace());
        }
        holder.root.txtClientName.setText(reservation.getReserveClientName());
        holder.root.txtReservationDate.setText(DateHelp.convertToString(reservation.getCheckInDate(), DateHelp.dateTimeFormat).concat(" - ".concat(DateHelp.convertToString(reservation.getCheckOutDate(), DateHelp.dateTimeFormat))));
        holder.root.txtStatus.setText(reservation.getReserveStatus());
        holder.root.getRoot().setOnClickListener(view -> reservationClick.onClick(reservation));
    }

    @Override
    public int getItemCount() {
        return reservationList.size();
    }

    public interface ReservationClick {
        void onClick(Reservation reservation);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final LstReservationPowBinding root;

        public ViewHolder(@NonNull LstReservationPowBinding root) {
            super(root.getRoot());
            this.root = root;
        }
    }
}
