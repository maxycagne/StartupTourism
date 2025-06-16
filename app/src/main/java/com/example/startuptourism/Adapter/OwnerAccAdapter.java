package com.example.startuptourism.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.startuptourism.Database.RoomDb.Entity.Accommodation;
import com.example.startuptourism.R;
import com.example.startuptourism.databinding.LstPowAccommodationItemBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OwnerAccAdapter extends RecyclerView.Adapter<OwnerAccAdapter.ViewHolder> {
    private final Context context;
    private List<Accommodation> accomList;
    private final AccomClick accomClick;

    public OwnerAccAdapter(Context context, List<Accommodation> accomList, AccomClick accomClick) {
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
    public OwnerAccAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LstPowAccommodationItemBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OwnerAccAdapter.ViewHolder holder, int position) {
        Accommodation accomUserImages = accomList.get(position);


        holder.root.txtName.setText(accomUserImages.getAccomName());
        holder.root.txtAddress.setText(accomUserImages.getAccomAddress());
        holder.root.txtType.setText(accomUserImages.getAccomType());
        Picasso.get().load(accomUserImages.getImgPath1()).resize(150, 150).centerCrop().placeholder(R.drawable.ic_image)
                .into(holder.root.img);
        holder.root.getRoot().setOnClickListener(view -> accomClick.onClick(accomUserImages));

    }

    @Override
    public int getItemCount() {
        return accomList.size();
    }

    public interface AccomClick {
        void onClick(Accommodation accomUserImages);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final LstPowAccommodationItemBinding root;

        public ViewHolder(@NonNull LstPowAccommodationItemBinding root) {
            super(root.getRoot());
            this.root = root;
        }
    }
}
