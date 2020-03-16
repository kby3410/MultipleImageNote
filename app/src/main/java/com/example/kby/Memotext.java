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

public class Memotext extends AppCompatActivity implements View.OnClickListener {

    MemoControl control = new MemoControl(this);
    private  String PicturePath = Environment.getExternalStorageDirectory() + "/Gallery/KBY";    //사진 저장경로
    private ArrayList<Uri> Imagelist = new ArrayList<>();
    private static final int REQUEST_TAKE_PHOTO = 2222;
    private static final int REQUEST_TAKE_ALBUM = 3333;
    String mCurrentPhotoPath;
    Uri imageUri, photoURI, albumURI, urlURI;
    private PictuerAdapter pictuerAdapter;
    EditText TitleText, MemoText;
    Button btn;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memotext);
        RecyclerView recyclerView = findViewById(R.id.ImageRecycle);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        pictuerAdapter = new PictuerAdapter(Imagelist);
        recyclerView.setAdapter(pictuerAdapter);
        btn = (Button) findViewById(R.id.save);
        btn.setOnClickListener(this);
        btn = (Button) findViewById(R.id.camera);
        btn.setOnClickListener(this);
        MemoText = (EditText) findViewById(R.id.MemoView);
        TitleText = (EditText) findViewById(R.id.TitleView);
    }

    public void Thread(final String Eurl){        //외부 URL 스레드
        Thread mThread = new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(Eurl);     //입력받은 외부 URL주소
                    exists(Eurl);                //예외처리
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);      //비트맵 형식으로 decode
                    saveBitmap(bitmap);                           //비트맵 형식으로 저장
                } catch (MalformedURLException e) {
                    Handler cHandler = new Handler(Looper.getMainLooper()) {
                        @Override public void handleMessage(Message msg) {
                            Toast.makeText(Memotext.this, "http를 확인하세요", Toast.LENGTH_LONG).show();
                        }
                    };
                    cHandler.sendEmptyMessage(0);
                    e.printStackTrace();
                } catch (IOException e) {
                    Handler cHandler = new Handler(Looper.getMainLooper()) {
                        @Override public void handleMessage(Message msg) {
                            Toast.makeText(Memotext.this, "IOE 에러입니다", Toast.LENGTH_LONG).show();
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
            Toast.makeText(Memotext.this, "인터럽트 에러입니다", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void saveBitmap(Bitmap bitmap){           //외부 URL 사진 저장
        try{
            File URLfile = null;
            URLfile = createImageFile();          //새로운 파일 생성
            urlURI = Uri.fromFile(URLfile);
            FileOutputStream out = new FileOutputStream(URLfile.getAbsolutePath());
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);      //새로운 파일에 외부 URL 사진을 비트맵형식으로 압축
            out.close();
            galleryAddPic();                   //사진 저장
            Imagelist.add(urlURI);
        }catch (FileNotFoundException e){
            Log.e("FileNotFoundException",e.getMessage());
        }catch (IOException e) {
            Log.e("IOException", e.getMessage());
        }
    }

    public void CreateListDialog() {         //카메라 버튼 다이얼로그
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
                    pictuerAdapter.notifyDataSetChanged();     //스레드 끝난후 반영
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void  urldialog(){         //외부 URL 다이얼로그
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("URL 입력");
        alert.setMessage("URL을 입력해주세요.");
        final EditText name = new EditText(this);
        alert.setView(name);
        alert.setPositiveButton("입력", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String urlname = name.getText().toString();      //입력받은 URL값을 Thread에 넘겨줌
                Thread(urlname);
                dialog.dismiss();
            }
        });
        alert.setNegativeButton("취소",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Toast.makeText(Memotext.this, "취소하였습니다.", Toast.LENGTH_SHORT).show();
            }
        });
        alert.show();
    }

    public void onClick(View v) {
        if (TitleText.getText().toString().equals("")) {    //제목 내용 필수입력
            Toast.makeText(getApplicationContext(), "제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
        } else if (MemoText.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
        } else {
            switch (v.getId()) {
                case R.id.save: {             //메모저장
                    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
                    String TitleData = TitleText.getText().toString();
                    String MemoData = MemoText.getText().toString();
                    Data Memodata = new Data();
                    Memodata.setTitle(TitleData);        //제목
                    Memodata.setContent(MemoData);       //내용
                    Memodata.setTime(sf.format(new Date()));   //시간
                    Memodata.setResId(Imagelist);             //사진들
                    control.save(Memodata, TitleData);       //파일로 저장
                    finish();
                    break;
                }
                case R.id.camera:
                    CreateListDialog();
            }
        }
    }

    private void goToAlbum() {          //사진 앨범 접근
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, REQUEST_TAKE_ALBUM);
    }

    private void galleryAddPic(){           //해당 이미지파일 저장
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:              //카메라 사진
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        galleryAddPic();          //사진 저장
                        Imagelist.add(imageUri);
                        pictuerAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Log.e("REQUEST_TAKE_PHOTO", e.toString());
                    }
                } else {
                    Toast.makeText(this, "사진찍기를 취소하였습니다.", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_TAKE_ALBUM:             //앨범 사진
                if (resultCode == Activity.RESULT_OK) {
                    if(data.getData() != null){
                        try {
                            File albumFile = null;
                            albumFile = createImageFile();             //사진 파일 생성
                            photoURI = data.getData();                //선택된 사진
                            albumURI = Uri.fromFile(albumFile);
                            InputStream in = getContentResolver().openInputStream(photoURI);
                            Bitmap img = BitmapFactory.decodeStream(in);              //선택된 사진을 비트맵으로 저장
                            FileOutputStream out = new FileOutputStream(albumFile.getAbsolutePath());
                            img.compress(Bitmap.CompressFormat.JPEG,100,out);      //생성된 파일에 선택된 사진을 비트맵형식으로 압축
                            out.close();
                            galleryAddPic();           //사진 저장
                            Imagelist.add(albumURI);
                            pictuerAdapter.notifyDataSetChanged();
                        }catch (Exception e){
                            Log.e("TAKE_ALBUM_SINGLE ERROR", e.toString());
                        }
                    }
                }
                break;
        }
    }

    private void takePhoto() {                 //카메라 접근
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(intent.resolveActivity(getPackageManager())!= null){
                File photoFile = null;
                try{
                    photoFile = createImageFile();            //새로운 파일 생성
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

    public File createImageFile() throws IOException {          //새로운 파일 생성
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName =  timeStamp + ".png";
        File imageFile = null;
        File storageDir = new File(PicturePath);
        if (!storageDir.exists()) {            //파일이 존재하지않을 경우 파일 생성
            storageDir.mkdirs();
        }
        imageFile = new File(storageDir, imageFileName);       //이미지파일생성  경로와 파일이름을 넣어준다
        mCurrentPhotoPath = imageFile.getAbsolutePath();
        return imageFile;
    }

    public  boolean exists(String URLName) {        //외부 URL 예외처리
        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con =
                    (HttpURLConnection) new URL(URLName).openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            Handler cHandler = new Handler(Looper.getMainLooper()) {
                @Override public void handleMessage(Message msg) {
                    Toast.makeText(Memotext.this, "인터넷 연결확인", Toast.LENGTH_LONG).show();
                }
            };
            cHandler.sendEmptyMessage(0);
            e.printStackTrace();
            return false;
        }
    }

}