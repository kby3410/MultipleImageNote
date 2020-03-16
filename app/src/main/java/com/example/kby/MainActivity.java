package com.example.kby;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/*
Permission - 아래의 링크의 라이브러리를 사용하였습니다.
http://github.com/ParkSangGwon/TedPermission
 */

public class MainActivity extends AppCompatActivity {

    MemoControl control = new MemoControl(this);
    private ArrayList<Data> fileList = new ArrayList();               //메모 파일들의 목록
    private RecyclerAdapter adapter;
    String folder_path = Environment.getDataDirectory() + "/data/com.example.kby/files/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        getfileList();
        Button btn = (Button) findViewById(R.id.insert_memo);
        btn.setOnClickListener(new View.OnClickListener() {           //메모추가 버튼클릭 리스너
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Memotext.class);
                startActivity(intent);
            }
        });

        PermissionListener permissionlistener = new PermissionListener()     //카메라  권한 얻어오기
        {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                String data = "파일 생성내용....";
                try {
                    File path = Environment.getExternalStoragePublicDirectory
                            (Environment.DIRECTORY_PICTURES);
                    File f = new File(path, "external.txt"); // 경로, 파일명
                    FileWriter write = new FileWriter(f, false);
                    PrintWriter out = new PrintWriter(write);
                    out.println(data);
                    out.close();
                    Log.d("test", "저장완료");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };
        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION)
                .check();
    }

    private void init() {
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new RecyclerAdapter(fileList);
        recyclerView.setAdapter(adapter);
    }

    public void getfileList() {                        //파일 목록 얻어와 시간순으로 정렬
        try {
            File folder = new File(folder_path);
            File list[] = folder.listFiles();
            fileList.clear();                          //파일 목록 초기화
            if (list.length > 0) {
                for (int i = 0; i < list.length; i++) {
                    fileList.add(control.load(list[i].getName()));
                    adapter.notifyDataSetChanged();
                }
                Comparator<Data> comparator = new Comparator<Data>() {     //파일목록 시간순으로 정렬
                    @Override
                    public int compare(Data a, Data b) {
                        return b.getTime().compareTo(a.getTime());
                    }
                };
                Collections.sort(fileList, comparator);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    protected void onRestart(){               //메모 수정,삭제했을 경우 반영하기위한 리스타트
        init();
        super.onRestart();
        getfileList();
    }

}

