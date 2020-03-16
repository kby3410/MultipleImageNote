package com.example.kby;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class MemoControl{

    Context mContext = null;
    public  MemoControl(Context context){
        mContext = context;
    }

    public  void save(Data data,String Title){              //메모저장
        File saveFile = new File(Environment.getDataDirectory()+ "/data/com.example.kby/files/"+Title);
        if(saveFile.exists()==false) {
            FileWriter fw = null;
            BufferedWriter bufwr = null;
            try {                                    //파일에 한줄씩 입력
                fw = new FileWriter(saveFile);
                bufwr = new BufferedWriter(fw);
                bufwr.write(data.getTitle());
                bufwr.newLine();
                bufwr.write(data.getContent());
                bufwr.newLine();
                bufwr.write(data.getTime());
                bufwr.newLine();
                for (int i = 0; i < data.getResId().size(); i++) {
                    bufwr.write(data.getResId().get(i).toString());
                    bufwr.newLine();
                }
                bufwr.flush();
                System.out.println("저장완료");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("저장실패");
            }
            try {
                if (bufwr != null) {
                    bufwr.close();
                }
                if (fw != null) {
                    fw.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            System.out.println("해당 파일이 이미 존재합니다.");
        }
    }

    public  Data load (String Title){                   //메모 불러오기
        File saveFile = new File(Environment.getDataDirectory()+ "/data/com.example.kby/files/"+Title);
        ArrayList list = new ArrayList();
        String str;
        Data data = new Data();
        ArrayList<Uri> imagememo = new ArrayList<>();
        int count = 0;
        try{
            BufferedReader br = new BufferedReader(new FileReader(saveFile));
            while((str = br.readLine()) != null){                      //파일에서 한줄씩 읽어온다.
                list.add(str);
                count ++;
            }
            for(int i = 0; i < count; i++){
                if(i == 0){
                    data.setTitle((String)list.get(i));
                }
                else if(i == 1){
                    data.setContent((String)list.get(i));
                }
                else if(i == 2){
                    data.setTime((String)list.get(i));
                }
                else{
                    imagememo.add(Uri.parse(list.get(i).toString()));
                }
            }
            data.setResId(imagememo);
            System.out.println("출력완료");
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("출력실패");
        }
        return data;
    }

    public void delete(String Title){                    //메모 삭제
        try{
            File f = new File(Environment.getDataDirectory()+ "/data/com.example.kby/files/"+Title);
            if(f.exists()){
                f.delete();
            }
            else{
                System.out.println("파일이 존재하지 않습니다.");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
