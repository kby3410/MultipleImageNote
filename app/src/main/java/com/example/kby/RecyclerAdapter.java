package com.example.kby;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> {

    private ArrayList<Data> listData;

    public RecyclerAdapter(ArrayList<Data> listData)
    {
        this.listData = listData;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        holder.onBind(listData.get(position));
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return listData.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private  TextView textView1;
        private TextView textView2;
        private TextView timeView;
        private ImageView imageView;

        ItemViewHolder(View itemView) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.textView1);
            textView2 = itemView.findViewById(R.id.textView2);
            timeView = itemView.findViewById(R.id.timeView);
            imageView = itemView.findViewById(R.id.imageView);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {       //해당 아이템 클릭 리스너
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION)
                    {
                        Context context = v.getContext();
                        //인텐트를 사용해 MemoDetailActivity로 값을 넘겨준다.
                        Intent memo_detail_form = new Intent(context.getApplicationContext(), MemoDetailActivity.class);
                        memo_detail_form.putExtra("Title", listData.get(pos).getTitle());
                        memo_detail_form.putExtra("Content", listData.get(pos).getContent());
                        memo_detail_form.putExtra("Time", listData.get(pos).getTime());
                        memo_detail_form.putExtra("Image", listData.get(pos).getResId());
                        context.startActivity(memo_detail_form.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                }
            });
        }
        void onBind(Data data) {
            textView1.setText(data.getTitle());
            textView2.setText(data.getContent());
            timeView.setText(data.getTime());
            if(data.getResId().isEmpty()){              //사진 값이 빈 값일 경우
                imageView.setVisibility(View.GONE);     //이미지뷰 공간을 보이지 않게해준다.
            }
            else{
                imageView.setImageURI(data.getResId().get(0));
            }
        }
    }
}

