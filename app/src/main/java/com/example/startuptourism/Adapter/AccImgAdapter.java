package com.example.startuptourism.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.startuptourism.databinding.LstImagesItemDispBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AccImgAdapter extends RecyclerView.Adapter<AccImgAdapter.ViewHolder> {
    private final Context context;
    private List<Uri> imageList;
    private final ImageClick imageClick;

    public AccImgAdapter(Context context, List<Uri> imageList, ImageClick imageClick) {
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
    public AccImgAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LstImagesItemDispBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AccImgAdapter.ViewHolder holder, int position) {
        Uri uri = imageList.get(position);
        Picasso.get().load(uri).centerCrop().resize(115, 115).into(holder.root.img);
        holder.root.getRoot().setOnClickListener(view -> imageClick.onClick(uri));

    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public interface ImageClick {
        void onClick(Uri uri);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final LstImagesItemDispBinding root;

        public ViewHolder(@NonNull LstImagesItemDispBinding root) {
            super(root.getRoot());
            this.root = root;
        }
    }
}
