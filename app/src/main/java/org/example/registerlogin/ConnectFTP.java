package org.example.registerlogin;

import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.FileInputStream;

public class ConnectFTP {
    private final String TAG = "Connect FTP";
    public FTPClient mFTPClient = null;

    public ConnectFTP() {
        mFTPClient = new FTPClient();
    }

    public boolean ftpConnect(String host, String username, String passwd, int port) {
        boolean result = false;
        try {
            mFTPClient.connect(host,port);

            if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())){
                result = mFTPClient.login(username, passwd);
                mFTPClient.enterLocalPassiveMode();
            }
        }
        catch (Exception e) {
            Log.d(TAG, "호스트와 연결할 수 없음");
        }
        return result;
    }

    public boolean ftpDisconnect() {
        boolean result = false;
        try {
            mFTPClient.logout();
            mFTPClient.disconnect();
            result = true;
        }
        catch (Exception e) {
            Log.d(TAG,"연결 종료를 실패함");
        }
        return false;
    }

    public String ftpGetDirectory() {
        String directory = null;
        try {
            directory = mFTPClient.printWorkingDirectory();
        }
        catch (Exception e) {
            Log.d(TAG, "최신 디렉터리를 찾을 수 없음");
        }
        return directory;
    }

    public boolean ftpChangeDirectory(String directory) {
        try {
            mFTPClient.changeWorkingDirectory(directory);
            return true;
        }
        catch (Exception e) {
            Log.d(TAG,"디렉터리를 바꿀 수 없음");
        }
        return false;
    }

    public String[] ftpGetFileList(String directory) {
        String[]fileList = null;
        int i = 0;
        try {
            FTPFile[] ftpFiles = mFTPClient.listFiles(directory);
            fileList = new String[ftpFiles.length];
            for(FTPFile file : ftpFiles) {
                String fileName = file.getName();

                if (file.isFile()) {
                    fileList[i] = "(File)" + fileName;
                }
                else {
                    fileList[i] = "(Directory)" + fileName;
                }
                i++;
            }
        } catch (Exception e) {
            Log.d(TAG,"Get File List Failed");
            e.printStackTrace();
        }
        return fileList;
    }

    public boolean ftpCreateDirectory(String directory) {
        boolean result = false;
        try {
            result = mFTPClient.makeDirectory(directory);
        }
        catch (Exception e) {
            Log.d(TAG,"디렉터리를 만들 수 없음");
        }
        return result;
    }

    public boolean ftpDeleteDirectory(String directory) {
        boolean result = false;
        try {
            result = mFTPClient.removeDirectory(directory);
        }
        catch (Exception e) {
            Log.d(TAG,"디렉터리를 지울 수 없음");
        }
        return result;
    }

    public boolean ftpDeleteFile(String file) {
        boolean result = false;
        try {
            result = mFTPClient.deleteFile(file);
        }
        catch (Exception e) {
            Log.d(TAG,"파일을 지울 수 없음");
        }
        return result;
    }

    public boolean ftpRenameFile(String from, String to) {
        boolean result = false;
        try {
            result = mFTPClient.rename(from, to);
        }
        catch (Exception e) {
            Log.d(TAG,"이름을 변경할 수 없음");
        }
        return result;
    }

    public boolean ftpUploadFile(String srcFilePath, String desFileName, String desDirectory){
        boolean result = false;
        try {
            FileInputStream fis = new FileInputStream(srcFilePath);
            if(ftpChangeDirectory(desDirectory)) {
                result = mFTPClient.storeFile(desFileName,fis);
            }
            fis.close();
        }
        catch (Exception e) {
            Log.d(TAG,"파일을 업로드할 수 없음");
        }
        return result;
    }

}
