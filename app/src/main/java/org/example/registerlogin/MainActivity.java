package org.example.registerlogin;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class MainActivity extends AppCompatActivity{
    // ?????? ??????
    private Uri BfilePath;

    //???????????? media record
    public   MediaRecorder mRecorder = null;
    public int recordCount = 0;
    TextView test_tv;
    private BluetoothSPP bt;
    private long time = 0;

    NotificationManager notificationManager;
    PendingIntent intent;
    Button test;
    private final int PERMISSIONS_REQUEST_RESULT = 100;
    private static final int REQUEST_ENABLE_BT = 10;

    // FTP????????? ?????? ?????? ??????
    private ConnectFTP connectFTP;
    private final String TAGf = "0v0_FTP";
    boolean status, threadStop = false;
    private String FTPip = "172.30.1.57";

    private static final String TAG = "RecordThread";
    private static final int startAmpl = 33000;
    private final int audioSource = MediaRecorder.AudioSource.VOICE_RECOGNITION;
    private final int outputFormat = MediaRecorder.OutputFormat.THREE_GPP;
    private final int audioEncoder = MediaRecorder.AudioEncoder.AMR_NB;

    // ????????? ?????? media record
    private MediaRecorder mediaRecorder = null;
    private double decibel = 0;
    private boolean isRunning = false;
    private Thread recordThread;
    private static final double AMP_CONST = 1.9; // ????????? ????????? ????????? ?????? ?????????, ????????? : 1.9, ????????? : 2700
    private static final double EMA_FILTER = 0.6; // EMA ?????? ????????? ???????????? ??????, ????????? 0.6
    private static double mEMA = 0.0;
    private NotificationManager notifManager;

    // socket????????? ?????? ?????? ??????
    private ConnectSocket connectSocket;

    //????????? ????????? 1, ????????? 0??? ?????????
    int line2;
    //wav????????? ????????? ????????? ??????
    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SoundSense/";

    int currnetFileNum = 0;
    File file;
    File[] files;
    List<String> fileNameList;
    Thread ftpThread;

    public List<String> fileNames() {
        file = new File(path);
        files = file.listFiles();
        fileNameList = new ArrayList<>();

        for (int k = 0; k < files.length; k++) {
            fileNameList.add(files[k].getName());
//            Log.i("?????? ??????",files[k].getName());
        }
        return fileNameList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //permissionCheck();
        start();


        bt = new BluetoothSPP(this);
        connectFTP = new ConnectFTP();
        connectSocket = new ConnectSocket();

        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // ??????????????? ????????? ????????? ???????????????
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "?????? ????????? ????????? ?????? ??????/?????? ??????", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]
                                {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        2);  //????????? ????????? ??????????????? ?????? ??????

            } else {
                //Toast.makeText(this, "?????? ???????????????", Toast.LENGTH_SHORT).show();
            }
        }*/
        if (!bt.isBluetoothAvailable()) { //???????????? ?????? ??????
            Toast.makeText(getApplicationContext()
                    , "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { //????????? ??????
            public void onDataReceived(byte[] data, String message) {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() { //???????????? ???
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext()
                        , "Connected to " + name + "\n" + address
                        , Toast.LENGTH_SHORT).show();
            }

            public void onDeviceDisconnected() { //????????????
                Toast.makeText(getApplicationContext()
                        , "Connection lost", Toast.LENGTH_SHORT).show();
            }

            public void onDeviceConnectionFailed() { //????????????
                Toast.makeText(getApplicationContext()
                        , "Unable to connect", Toast.LENGTH_SHORT).show();
            }

        });


    }

    public void Onclick(View view) {
        if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
            bt.disconnect();
        } else {
            Intent intent = new Intent(getApplicationContext(), DeviceList.class);
            startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
        }
    }

//        public void onStart() {
//        super.onStart();
//        if (!bt.isBluetoothEnabled()) { //
//            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
//        } else {
//            if (!bt.isServiceAvailable()) {
//                bt.setupService();
//                bt.startService(BluetoothState.DEVICE_OTHER); //DEVICE_ANDROID??? ??????????????? ?????? ??????
//            }
//        }
//
//            ftpThread = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    while (!threadStop){
//                        try {
//                                //?????? ????????? ???????????? ?????? ????????? ??????
//                            if(currnetFileNum != fileNames().size()) {
//                                Log.i("?????? ?????????","?????? ??????");
//
//                                currnetFileNum = fileNames().size();
//                                for (int i = 0; i < fileNames().size(); i++) {
//                                    //?????? ?????????
//                                    uploadFile(fileNames().get(i));
//                                    Log.d("Upload File", fileNames().get(i) + " ?????????");
//                                }
//
//                                // ?????? ?????? & ??????
////                              connectSocket.socket_connect();
//                                }
//                            else {
//                                line2 = 0;
//                                Log.d(TAGf,"???????????? ?????? ??????");
//                            }
//                        }
//
//                            catch (Exception e) {
//                                Log.d(TAGf, "????????? ??????");
//                            }
//
//                            try {
//                                Thread.sleep(2000);
//                            } catch (InterruptedException e) {
//                                Log.d(TAGf, "Thread dead");
//                            }
//                        }
//
//                    }
//            });
//            ftpThread.start();
//
//        Button Button_connect = findViewById(R.id.button_connect);
//        Button_connect.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
//                    bt.disconnect();
//                } else {
//                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
//                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
//                }
//            }
//        });
//
//        //https://lcw126.tistory.com/103
//        // firebaseStorage = FirebaseStorage.getInstance("gs://e1i3-83897.appspot.com/");
//        // mStorageReference = firebaseStorage.getReferenceFromUrl(REFERENCE_URL).child("belly_pain.txt");
//        test_tv = (TextView) findViewById(R.id.test_tv);
//        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
//        DatabaseReference rootRef = firebaseDatabase.getReference();
//        final DatabaseReference textRef = rootRef.child("belly_pain");
//
//
//        textRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                StringBuffer buffer = new StringBuffer();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    String data = snapshot.getValue(String.class);
//                    buffer.append(data);
//                }
//                test_tv.setText(buffer);
//                Intent intent = new Intent(MainActivity.this, alarm_BActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
//                String channelId = "Channel ID";
//                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                NotificationCompat.Builder notificationBuilder =
//                        new NotificationCompat.Builder(MainActivity.this, channelId)
//                                .setSmallIcon(R.drawable.bell)
//                                .setContentTitle("Baby crying")
//                                .setContentText("????????? ??????????????? ")
//                                .setDefaults(Notification.DEFAULT_VIBRATE)//??????
//                                .setAutoCancel(true)//???????????? ???????????? ??? ???????????? ???????????????(true:??????/false:??????)
//                                .setContentIntent(pendingIntent);
//                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    String channelName = "Channel Name";
//                    NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
//                    notificationManager.createNotificationChannel(channel);
//                }
//                notificationManager.notify(0, notificationBuilder.build());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
//        //Set Home Selected
//        bottomNavigationView.setSelectedItemId(R.id.Home);
//        //Preform ItemSelectedListener
//        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//                switch (menuItem.getItemId()) {
//                    case R.id.History:
//                        startActivity(new Intent(getApplicationContext(), History.class));
//                        overridePendingTransition(0, 0);
//                        return true;
//
//                    case R.id.Setting:
//                        startActivity(new Intent(getApplicationContext(), Setting.class));
//                        overridePendingTransition(0, 0);
//                        return true;
//
//                    case R.id.Home:
//                        return true;
//                }
//                return false;
//            }
//        });
//    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            ArrayList<String> results = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            String str = results.get(0);
            Toast.makeText(getBaseContext(), str, Toast.LENGTH_SHORT).show();

            TextView tv = findViewById(R.id.test_tv);
            tv.setText(str);
        }
    }

    /*public void permissionCheck() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 1);
        }
    }*/

    public void start() {
        if (!isRunning) {
            isRunning = true;
            /**
             * Override function
             * Function to execute when the thread is started
             */
            recordThread = new Thread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void run() {
                    while (isRunning) {
                        try {
                            //Sleeps to slow down the reading of the amplitude, such that the GUI can keep up with the readings.
                            Thread.sleep(500);

                            //decibel = getAmplitude() / 2000;
                            decibel = 20 * Math.log10(getAmplitudeEMA() / AMP_CONST);
                            Log.d(TAG, "decibel" + decibel);
                            if (decibel >= 65) {
                                recordAudio();
                                VoiceTask voiceTask = new VoiceTask();
                                voiceTask.execute();

                                try {
                                    Thread.sleep(20000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                //????????? ????????? ????????? ??????
                                try {
                                    Path filePath = Paths.get(Environment.getExternalStorageDirectory()
                                            .getAbsolutePath() + "/myrecording_"+ recordCount +".mp4");
                                    Path filePathToMove = Paths.get(Environment.getExternalStorageDirectory()
                                            .getAbsolutePath() + "/SoundSense/myrecording_"+ recordCount +".mp4");
                                    Files.move(filePath, filePathToMove);
                                }
                                catch (IOException e) {
                                    Log.d("MOVE record","?????? ???????????? ??????");
                                }

                                recordCount++;

                            }


                        } catch (InterruptedException e) {
                            Log.e(TAG, "Thread interrupted.", e.fillInStackTrace());
                        }

                    }
                }
            });
            if (mediaRecorder == null) {
                mediaRecorder = new MediaRecorder();
            }
            try {
                mediaRecorder.setAudioSource(audioSource);
                mediaRecorder.setOutputFormat(outputFormat);
                mediaRecorder.setAudioEncoder(audioEncoder);
                mediaRecorder.setOutputFile("/dev/null");
            }
            catch (IllegalStateException ex) {
                Log.e(TAG, "The order in the media recorder is not correct!", ex.fillInStackTrace());
            }
            recordThread.start();
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (IOException ex) {
                Log.e(TAG, "Could not prepare or start the mediaRecorder", ex.fillInStackTrace());
            }
        }
    }

    @Override
    protected void onDestroy() {
        threadStop = true;
        ftpThread.interrupt();
        finish();
        bt.stopService(); //???????????? ??????
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {   //???????????? ?????? ??????
        if (System.currentTimeMillis() - time >= 2000) {
            time = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), "Press the back button again to exit...", Toast.LENGTH_SHORT).show();
        } else if (System.currentTimeMillis() - time < 2000) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            Toast.makeText(MainActivity.this, "App Start In Background...", Toast.LENGTH_SHORT).show();
        }
    }

    private void recordAudio() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); // ???????????? ?????? ???????????? ?????? ?????????
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); // ?????? ?????? ??????
        mRecorder.setAudioSamplingRate(44100);
        mRecorder.setAudioEncodingBitRate(96000);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        //mediaRecorder.setMaxDuration(10000);
        mRecorder.setOutputFile(Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/myrecording_"+ recordCount+".mp4");

       new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRecorder.stop();
                        mRecorder.reset();
                        mRecorder = null;
                        //Looper.loop();
                    }
                },20000);
            }
        }).start();

        try {
            //mediaRecorder.setMaxDuration(10000);
            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private double getAmplitude() {
        if (mediaRecorder != null)
            return (mediaRecorder.getMaxAmplitude());
        else
            return 0;
    }

    public double getAmplitudeEMA() {
        double amp = getAmplitude();
        mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
        return mEMA;
    }


    //upload the file
    private void uploadFile(String filemp4) {

        //storage
        FirebaseStorage storage = FirebaseStorage.getInstance();

        //?????????????????? ?????? ??????
        String filename = filemp4;
        BfilePath = Uri.fromFile(new File(path+filemp4));

        //storage ????????? ?????? ???????????? ????????? ??????.
        StorageReference storageRef = storage.getReferenceFromUrl("gs://e1i3-83897.appspot.com").child("/" + filename);
        //???????????????...
        storageRef.putFile(BfilePath)
                //?????????
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getApplicationContext(), "????????? ??????!", Toast.LENGTH_SHORT).show();
                    }
                })
                //?????????
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "????????? ??????!", Toast.LENGTH_SHORT).show();
                    }
                })
                //?????????
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        @SuppressWarnings("VisibleForTests") //?????? ?????? ?????? ???????????? ????????? ????????????. ??? ??????????
                                double progress = (100 * taskSnapshot.getBytesTransferred()) /  taskSnapshot.getTotalByteCount();
                    }
                });
    }
    public class VoiceTask extends AsyncTask<String, Integer, String> {
        String str = null;

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {
                getVoice();
            } catch (Exception e) {
                // TODO: handle exception
            }
            return str;
        }

        @Override
        protected void onPostExecute(String result) {
            try {

            } catch (Exception e) {
                Log.d("onActivityResult", "getImageURL exception");
            }
        }
    }

    private void getVoice() {

        Intent intent = new Intent();
        intent.setAction(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        String language = "ko-KR";

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
        startActivityForResult(intent, 2);
    }

}


