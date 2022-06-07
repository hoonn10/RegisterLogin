package org.example.registerlogin;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ftpT {
    // FTP통신을 위한 변수 선언
    private ConnectFTP connectFTP = new ConnectFTP();
    private final String TAGf = "0v0_FTP";
    boolean status, threadStop=false;
    private String FTPip = "172.30.1.57";

    // socket통신을 위한 변수 선언
    private ConnectSocket connectSocket = new ConnectSocket();

    //소켓을 받으면 1, 없으면 0이 저장됨
    int line2;

    //wav파일이 저장된 폴더의 경로
    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SoundSense";

    int currnetFileNum= 0;

    File file;
    File[] files;
    List<String> fileNameList;
    Thread mainThread;


    public List<String> fileNames() {
        file = new File(path);
        files = file.listFiles();
        fileNameList = new ArrayList<>();

        for (int k = 0; k<files.length;k++){
            fileNameList.add(files[k].getName());
//            Log.i("파일 이름",files[k].getName());
        }
        return fileNameList;
    }

    public void start(){
        mainThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!threadStop) {
                    status = false;
                    status = connectFTP.ftpConnect(
                            FTPip,
                            "tester",
                            "1234",21);

                    if (!status == true) {
                        Log.d(TAGf, "Connection failed");
                    }
                    else {
                        Log.d(TAGf, "Connection Success");

                        try {
                            //파일 갯수가 달라지면 파일 업로드 실행
                            if(currnetFileNum != fileNames().size()) {
                                Log.i("파일 리스트","변화 감지");
                                currnetFileNum = fileNames().size();
                                for (int i = 0; i < fileNames().size(); i++) {
                                    connectFTP.ftpUploadFile(path + "/" + fileNames().get(i), fileNames().get(i), "/");
                                    Log.d("Upload File", fileNames().get(i) + " 업로드");
                                }
                                // 소켓 송신 & 수신
                                connectSocket.socket_connect();
                            }
                            else {
                                line2 = 0;
                                Log.d(TAGf,"업로드할 파일 없음");
                            }
                        }
                        catch (Exception e) {
                            Log.d(TAGf, "업로드 실패");
                        }

                        try {
                            Thread.sleep(5000);
                        }
                        catch (InterruptedException e) {
                            Log.d(TAGf, "Thread dead");
                        }
                    }
                }
            }
        });
        mainThread.start();
    }

}