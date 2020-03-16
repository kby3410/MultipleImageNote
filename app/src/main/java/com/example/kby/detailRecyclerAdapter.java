package com.example.kby;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class detailRecyclerAdapter extends RecyclerView.Adapter<detailRecyclerAdapter.ItemViewHolder> {

    private ArrayList<Uri> mDataset;

    public detailRecyclerAdapter(ArrayList<Uri> mDataset) {  //생성자 생성
        this.mDataset = mDataset;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {       //detailimage.xml에 대한 뷰홀더 생성
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detailimage, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        holder.onBind(mDataset.get(position));
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return mDataset.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        ItemViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.detailimage);
            itemView.setOnClickListener(new View.OnClickListener() {            //리사이클러뷰 클릭 이벤트
                @Override
                public void onClick(View v) {
                    final int pos = getAdapterPosition();                      //클릭된 해당 뷰의 포지션
                    if (pos != RecyclerView.NO_POSITION) {
                        final Context context = v.getContext();                //클릭된 해당 context
                        AlertDialog.Builder alert = new AlertDialog.Builder(context);    //다이얼로그
                        alert.setTitle("사진 삭제");
                        alert.setMessage("사진을 삭제하시겠습니까?");
                        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                remove(pos);                            //클릭된 뷰 삭제 및 데이터 삭제
                                dialog.dismiss();
                            }
                        });
                        alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Toast.makeText(context, "취소하였습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        alert.show();
                    }
                }
            });
        }

        void onBind(Uri uri) {
            if(uri == null){                         //사진 값이 빈 값일 경우
                imageView.setVisibility(View.GONE);  //이미지뷰 공간을 보이지 않게해준다.
            }
            else{
                imageView.setImageURI(uri);
            }
        }

        public void remove(int position) {                   //해당포지션 삭제
            try {
                mDataset.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mDataset.size());
            } catch (IndexOutOfBoundsException ex) {
                ex.printStackTrace();
            }
        }
    }
}

