package com.example.startuptourism.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.startuptourism.Database.RoomDb.Entity.Accommodation;
import com.example.startuptourism.R;
import com.example.startuptourism.databinding.LstTouAccsItemBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TouAccAdapter extends RecyclerView.Adapter<TouAccAdapter.ViewHolder> {
    private final Context context;
    private List<Accommodation> accomList;
    private final AccomClick accomClick;

    public TouAccAdapter(Context context, List<Accommodation> accomList, AccomClick accomClick) {
        this.context = context;
        this.accomList = accomList;
        this.accomClick = accomClick;
    }

    public void refreshList(List<Accommodation> accomList) {
        this.accomList = accomList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TouAccAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LstTouAccsItemBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TouAccAdapter.ViewHolder holder, int position) {
        Accommodation accom = accomList.get(position);

        Picasso.get().load(accom.getImgPath1()).resize(150, 150).centerCrop().placeholder(R.drawable.ic_image)
                .into(holder.root.img);
        holder.root.txtName.setText(accom.getAccomName());
        holder.root.txtAddress.setText(accom.getAccomAddress());
        holder.root.txtType.setText(accom.getAccomType());
        holder.root.getRoot().setOnClickListener(view -> accomClick.onClick(accom));

    }

    @Override
    public int getItemCount() {
        return accomList.size();
    }

    public interface AccomClick {
        void onClick(Accommodation accomUserImages);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final LstTouAccsItemBinding root;

        public ViewHolder(@NonNull LstTouAccsItemBinding root) {
            super(root.getRoot());
            this.root = root;
        }
    }
}
