package com.example.startuptourism.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.startuptourism.Database.RoomDb.Entity.Reservation;
import com.example.startuptourism.Helper.DateHelp;
import com.example.startuptourism.databinding.LstReservationTouBinding;

import java.util.List;

public class TouResAdapter extends RecyclerView.Adapter<TouResAdapter.ViewHolder> {
    private final Context context;
    private List<Reservation> reservationList;
    private final ReservationClick reservationClick;

    public TouResAdapter(Context context, List<Reservation> reservationList, ReservationClick reservationClick) {
        this.context = context;
        this.reservationList = reservationList;
        this.reservationClick = reservationClick;
    }

    public void refreshList(List<Reservation> reservationList) {
        this.reservationList = reservationList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TouResAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LstReservationTouBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TouResAdapter.ViewHolder holder, int position) {
        Reservation reservation = reservationList.get(position);
        holder.root.txtReservationDate.setText(DateHelp.convertToString(reservation.getCheckInDate(), DateHelp.dateTimeFormat).concat(" - ".concat(DateHelp.convertToString(reservation.getCheckOutDate(), DateHelp.dateTimeFormat))));
        holder.root.txtAddress.setText(reservation.getReserveAccomPlace());
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
        private final LstReservationTouBinding root;

        public ViewHolder(@NonNull LstReservationTouBinding root) {
            super(root.getRoot());
            this.root = root;
        }
    }
}
