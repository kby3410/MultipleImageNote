package com.example.kby;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class PictuerAdapter extends RecyclerView.Adapter<PictuerAdapter.ItemViewHolder> {

    private ArrayList<Uri> listImage;

    public PictuerAdapter(ArrayList<Uri> listImage) {  //생성자 생성
        this.listImage = listImage;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.picture, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        holder.onBind(listImage.get(position));
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return listImage.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        ItemViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.PickImage);
        }
        void onBind(Uri uri) {
            imageView.setImageURI(uri);
        }
    }
}
