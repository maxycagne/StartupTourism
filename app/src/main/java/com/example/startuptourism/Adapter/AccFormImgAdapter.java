package com.example.startuptourism.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.startuptourism.databinding.LstImagesItemBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AccFormImgAdapter extends RecyclerView.Adapter<AccFormImgAdapter.ViewHolder> {
    private final Context context;
    private List<Uri> imageList;
    private final ImageClick imageClick;

    public AccFormImgAdapter(Context context, List<Uri> imageList, ImageClick imageClick) {
        this.context = context;
        this.imageList = imageList;
        this.imageClick = imageClick;
    }

    public void refreshList(List<Uri> imageList) {
        this.imageList = imageList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AccFormImgAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LstImagesItemBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AccFormImgAdapter.ViewHolder holder, int position) {
        Uri uri = imageList.get(position);
        Picasso.get().load(uri).centerCrop().resize(115, 115).into(holder.root.img);
        holder.root.getRoot().setOnClickListener(view -> imageClick.onClick(uri));
        holder.root.btnRemove.setOnClickListener(view -> imageClick.onRemove(position));




    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public interface ImageClick {
        void onClick(Uri uri);

        void onRemove(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final LstImagesItemBinding root;

        public ViewHolder(@NonNull LstImagesItemBinding root) {
            super(root.getRoot());
            this.root = root;
        }
    }
}
