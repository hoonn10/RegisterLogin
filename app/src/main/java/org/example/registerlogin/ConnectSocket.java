package org.example.registerlogin;


import android.os.Handler;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

// 소켓 클라이언트  :  [참조] https://m.blog.naver.com/rhrkdfus/221406915879
public class ConnectSocket {
    private Socket socket;

    private DataOutputStream dos;
    private DataInputStream dis;

    // 서버의 IP 와 연결Port
    private String ip = "172.30.1.57";
    private int port = 22;


    void socket_connect() {
        try {
            socket = new Socket(ip, port);
            Log.w("Socket서버", "접속됨");
        } catch (IOException e1) {
            Log.w("Socket서버", "접속 못함");
            e1.printStackTrace();
        }

        Log.w("edit 넘어가야 할 값: ", "안드로이드에서 서버로 연결요청");

        try {
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream((socket.getInputStream()));
            dos.writeUTF("안드로이드에서 서버로 연결요청");
        } catch (IOException e) {
            e.printStackTrace();
            Log.w("버퍼", "버퍼 생성 실패");
        }

        Log.w("버퍼", "버퍼 생성 성공");
        while (true){
            try {
                String line = "";
                int line2;

                line = (String) dis.readUTF();
                line2 = (int) dis.read();

                Log.w("서버에서 받아온 값(1) ", "" + line);
                Log.w("서버에서 받아온 값(2) ", "" + line2);

                break;
            }
            catch (Exception e) {
                Log.d("Socket","수신 실패");
            }
        }

    }
}
