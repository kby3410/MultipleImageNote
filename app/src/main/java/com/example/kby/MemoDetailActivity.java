package com.example.kby;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MemoDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private  String PicturePath = Environment.getExternalStorageDirectory() + "/Gallery/KBY";
    MemoControl detail_control = new MemoControl(this);
    private static final int REQUEST_TAKE_PHOTO = 2222;
    private static final int REQUEST_TAKE_ALBUM = 3333;
    private ArrayList<Uri> detailList = new ArrayList<>();        //어댑터에 넘겨줄 사진 목록
    private ArrayList<Uri> recevie = new ArrayList<>();          //받아온 사진 목록
    private detailRecyclerAdapter detailRecycler;
    Data save_data = new Data();
    EditText detail_title, detail_memo;
    TextView detail_time;
    Button btn;
    Uri imageUri, urlURI, photoURI, albumURI;
    Bitmap detailbitmap;
    String detailmCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_detail);

        RecyclerView recyclerView = findViewById(R.id.dataRecycle);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        detailRecycler = new detailRecyclerAdapter(detailList);
        recyclerView.setAdapter(detailRecycler);

        btn = (Button) findViewById(R.id.dataSave);
        btn.setOnClickListener(this);
        btn = (Button) findViewById(R.id.dataCamera);
        btn.setOnClickListener(this);
        btn = (Button) findViewById(R.id.dataDelete);
        btn.setOnClickListener(this);
        detail_title = (EditText)findViewById(R.id.dataTitle);
        detail_memo = (EditText) findViewById(R.id.dataMemo);
        detail_time = (TextView) findViewById(R.id.dataTime);

        Intent intent = getIntent();            //메인 어댑터에서 넘어온 데이터들
        String Title = intent.getStringExtra("Title");
        String Content = intent.getStringExtra("Content");
        String Time = intent.getStringExtra("Time");
        recevie = (ArrayList<Uri>) intent.getSerializableExtra("Image");
        detail_title.setText(Title);
        detail_memo.setText(Content);
        detail_time.setText(Time);
        for(int i = 0; i < recevie.size(); i++){
            detailList.add(recevie.get(i));
        }
        save_data.setTitle(Title);
        detailRecycler.notifyDataSetChanged();
    }
    public  boolean exists(String URLName) {             //외부 URL  에러처리
        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con =
                    (HttpURLConnection) new URL(URLName).openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            Handler cHandler = new Handler(Looper.getMainLooper()) {
                @Override public void handleMessage(Message msg) {
                    Toast.makeText(MemoDetailActivity.this, "인터넷 연결확인", Toast.LENGTH_LONG).show();
                }
            };
            cHandler.sendEmptyMessage(0);
            e.printStackTrace();
            return false;
        }
    }
    public void Thread(final String Eurl){
        Thread mThread = new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(Eurl);      //입력받은 URL
                    exists(Eurl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    detailbitmap = BitmapFactory.decodeStream(is);        //비트맵 타입으로 decode
                    saveBitmap(detailbitmap);                             //비트맵 타입으로 사진 저장
                } catch (MalformedURLException e) {
                    Handler cHandler = new Handler(Looper.getMainLooper()) {
                        @Override public void handleMessage(Message msg) {
                            Toast.makeText(MemoDetailActivity.this, "http를 확인하세요", Toast.LENGTH_LONG).show();
                        }
                    };
                    cHandler.sendEmptyMessage(0);
                    e.printStackTrace();
                } catch (IOException e) {
                    Handler cHandler = new Handler(Looper.getMainLooper()) {
                        @Override public void handleMessage(Message msg) {
                            Toast.makeText(MemoDetailActivity.this, "IOE 에러입니다", Toast.LENGTH_LONG).show();
                        }
                    };
                    cHandler.sendEmptyMessage(0);
                    e.printStackTrace();
                }
            }
        };
        mThread.start();
        try{
            mThread.join();
        }catch (InterruptedException e){
            Toast.makeText(MemoDetailActivity.this, "인터럽트 에러입니다", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void saveBitmap(Bitmap bitmap){
        try{
            File URLfile = null;
            URLfile = createImageFile();             //파일 생성
            urlURI = Uri.fromFile(URLfile);
            FileOutputStream out = new FileOutputStream(URLfile.getAbsolutePath());
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);        //비트맵 타입으로 압축
            out.close();
            galleryAddPic();                                //사진저장
            detailList.add(urlURI);
        }catch (FileNotFoundException e){
            Log.e("FileNotFoundException",e.getMessage());
        }catch (IOException e) {
            Log.e("IOException", e.getMessage());
        }
    }

    public void CreateListDialog() {                             //카메라 버튼 다이얼로그
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("메뉴");
        builder.setItems(R.array.LAN, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int pos) {
                if (pos == 0) {
                    takePhoto();
                } else if (pos == 1) {
                    goToAlbum();
                } else if (pos == 2) {
                    urldialog();
                    detailRecycler.notifyDataSetChanged();     //스레드 끝난후 반영
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void  urldialog(){                 //외부 URL 다이얼로그
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("URL 입력");
        alert.setMessage("URL을 입력해주세요.");
        final EditText name = new EditText(this);
        alert.setView(name);
        alert.setPositiveButton("입력", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String urlname = name.getText().toString();     //입력받은 URL값을 Thread에 넘겨줌
                Thread(urlname);
                dialog.dismiss();
            }
        });
        alert.setNegativeButton("취소",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Toast.makeText(MemoDetailActivity.this, "취소하였습니다.", Toast.LENGTH_SHORT).show();
            }
        });
        alert.show();
    }

    private void goToAlbum() {                //사진 앨범 접근
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, REQUEST_TAKE_ALBUM);
    }

    private void galleryAddPic(){               //해당 이미지파일 저장
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(detailmCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:             //카메라 사진
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        galleryAddPic();    //사진 저장
                        detailList.add(imageUri);
                        detailRecycler.notifyDataSetChanged();

                    } catch (Exception e) {
                        Log.e("REQUEST_TAKE_PHOTO", e.toString());
                    }
                } else {
                    Toast.makeText(this, "사진찍기를 취소하였습니다.", Toast.LENGTH_SHORT).show();
                }
                break;

            case REQUEST_TAKE_ALBUM:        //앨범 사진
                if (resultCode == Activity.RESULT_OK) {
                    if(data.getData() != null){
                        try {
                            File albumFile = null;
                            albumFile = createImageFile();      //사진 파일 생성
                            photoURI = data.getData();          //선택된 사진
                            albumURI = Uri.fromFile(albumFile);
                            InputStream in = getContentResolver().openInputStream(photoURI);
                            Bitmap img = BitmapFactory.decodeStream(in);      //선택된 사진을 비트맵으로 저장
                            FileOutputStream out = new FileOutputStream(albumFile.getAbsolutePath());
                            img.compress(Bitmap.CompressFormat.JPEG,100,out);    //생성된 파일에 선택된 사진을 비트맵형식으로 압축
                            out.close();
                            galleryAddPic();                //사진 저장
                            detailList.add(albumURI);
                            detailRecycler.notifyDataSetChanged();
                        }catch (Exception e){
                            Log.e("TAKE_ALBUM_SINGLE ERROR", e.toString());
                        }
                    }
                }
                break;
        }
    }

    private void takePhoto() {                //카메라 접근
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(intent.resolveActivity(getPackageManager())!= null){
                File photoFile = null;
                try{
                    photoFile = createImageFile();        //새로운 파일 생성
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                }
                if (photoFile != null) {
                    //파일 공유시 권한문제로인한 파일프로바이더 사용 (content://)
                    Uri providerURI = FileProvider.getUriForFile(getApplicationContext(), getPackageName(), photoFile);
                    imageUri = providerURI;
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, providerURI);
                    startActivityForResult(intent, REQUEST_TAKE_PHOTO);
                }
            }
        }
        else {
            Toast.makeText(this, "저장공간이 접근 불가능한 기기입니다", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public File createImageFile() throws IOException {             //새로운 파일 생성
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + ".png";
        File imageFile = null;
        File storageDir = new File(PicturePath);
        if (!storageDir.exists()) {       //파일이 존재하지않을 경우 파일 생성
            storageDir.mkdirs();
        }
        imageFile = new File(storageDir, imageFileName);   //이미지파일생성  경로와 파일이름을 넣어준다
        detailmCurrentPhotoPath = imageFile.getAbsolutePath();
        return imageFile;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dataSave:                      //수정버튼
                AlertDialog.Builder salert = new AlertDialog.Builder(this);   //다이얼로그 생성
                salert.setTitle("메모 수정");
                salert.setMessage("메모를 수정하시겠습니까?.");
                salert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Data detail_data = new Data();
                        detail_control.delete(save_data.getTitle());        //처음넘어온 파일 삭제
                        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
                        String title = detail_title.getText().toString();
                        String content = detail_memo.getText().toString();
                        detail_data.setTitle(title);
                        detail_data.setContent(content);
                        detail_data.setTime(sf.format(new Date()));
                        detail_data.setResId(detailList);
                        detail_control.save(detail_data,title);        //수정된 파일 저장
                        finish();
                    }
                });
                salert.setNegativeButton("취소",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(MemoDetailActivity.this, "취소하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
                salert.show();
                break;

            case R.id.dataCamera:
                CreateListDialog();
                break;

            case R.id.dataDelete:
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("메모 삭제");
                alert.setMessage("메모를 삭제하시겠습니까?.");
                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        detail_control.delete(detail_title.getText().toString());  //파일 삭제
                            finish();
                        }
                });
                alert.setNegativeButton("취소",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(MemoDetailActivity.this, "취소하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();
                break;
        }
    }
}

