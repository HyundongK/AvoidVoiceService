package com.example.avoidvoice.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.avoidvoice.BuildConfig;
import com.example.avoidvoice.R;
import com.example.avoidvoice.chatapi.ML;
import com.example.avoidvoice.chatapi.MLHandler;
import com.example.avoidvoice.main.MainFragment;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class VoiceAvoidService extends Service {
    private static final String SpeechSubscriptionKey = BuildConfig.STT_KEY;
    private static final String SpeechRegion = "koreacentral";
    private static final int ULSOURCE = MediaRecorder.AudioSource.VOICE_UPLINK;
    private static final int DLSOURCE = MediaRecorder.AudioSource.VOICE_DOWNLINK;
    private static final int MICSOURCE = MediaRecorder.AudioSource.MIC;

    private static final String logTag = "stt";

    private boolean ulStarted = false;
    private boolean dlStarted = false;
    private boolean fileStarted = false;
    private boolean micStarted = false;
    private SpeechRecognizer recoUl = null;
    private SpeechRecognizer recoDl = null;
    private SpeechRecognizer recoFile = null;
    private SpeechRecognizer recoMic = null;
    private AudioConfig ulInput = null;
    private AudioConfig dlInput = null;
    private AudioConfig fileInput = null;
    private AudioConfig micInput = null;
    private ArrayList<String> contentUl = new ArrayList<>();
    private ArrayList<String> contentDl = new ArrayList<>();
    private ArrayList<String> contentFile = new ArrayList<>();
    private ArrayList<String> contentMic = new ArrayList<>();
    private ArrayList<String> totalcontent = new ArrayList<>();

    private STTAudioStream uplinkStream;
    private STTAudioStream downlinkStream;
    private STTAudioStream microphoneStream;

    private MediaPlayer mp;

    private String sttFile = "test_sample.wav";
    private MLHandler mlHandler;


    private SharedPreferences appData;
    private boolean saveSwitchData = true;

    public VoiceAvoidService() throws JSONException {

    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        //Intent restartServiceIntent = new Intent(getApplicationContext(),this.getClass());
        //restartServiceIntent.setPackage(getPackageName());
        //startService(restartServiceIntent);
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            mlHandler = new MLHandler(getApplicationContext());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        int test = intent.getIntExtra("test", 0);
        appData = getSharedPreferences("appData", MODE_PRIVATE);
        saveSwitchData = appData.getBoolean("MAIN_SWITCH", false);

        if(test == 0 && saveSwitchData)
            startConvert();
        else if(test == 1)  startFileSTT();
        else if(test == 2) startMicSTT();

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopConvert();
    }



    private STTAudioStream createUplinkStream() {
        this.releaseUplinkStream();

        uplinkStream = new STTAudioStream(ULSOURCE);
        return uplinkStream;
    }

    private STTAudioStream createDownlinkStream() {
        this.releaseDownlinkStream();

        downlinkStream = new STTAudioStream(DLSOURCE);
        return downlinkStream;
    }

    private STTAudioStream createMicStream() {
        this.releaseMicStream();

        microphoneStream = new STTAudioStream(MICSOURCE);
        return microphoneStream;
    }

    private void releaseUplinkStream() {
        if (uplinkStream != null) {
            uplinkStream.close();
            uplinkStream = null;
        }
    }

    private void releaseDownlinkStream() {
        if (downlinkStream != null) {
            downlinkStream.close();
            downlinkStream = null;
        }
    }

    private void releaseMicStream() {
        if (microphoneStream != null) {
            microphoneStream.close();
            microphoneStream = null;
        }
    }


    private void startConvert() {
        //create config
        final SpeechConfig speechConfig;
        try {
            speechConfig = SpeechConfig.fromSubscription(SpeechSubscriptionKey, SpeechRegion);
            speechConfig.setSpeechRecognitionLanguage("ko-KR");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return;
        }

        totalcontent.clear();
        contentUl.clear();
        contentDl.clear();

        //try recognize continuously
        try {
            ulInput = AudioConfig.fromStreamInput(createUplinkStream());
            dlInput = AudioConfig.fromStreamInput(createDownlinkStream());

            recoUl = new SpeechRecognizer(speechConfig, ulInput);
            recoDl = new SpeechRecognizer(speechConfig, dlInput);

            Toast.makeText(getApplicationContext(), "start", Toast.LENGTH_SHORT).show();
            //event that a speech ended
            recoUl.recognized.addEventListener((o, speechRecognitionResultEventArgs) -> {
                final String s = speechRecognitionResultEventArgs.getResult().getText();
                Log.i(logTag, "Final result received: " + s);
                contentUl.add(s);

                contentUl.add(0, "\nMe : ");

                totalcontent.addAll(contentUl);
                contentUl.clear();
            });


            recoDl.recognized.addEventListener((o, speechRecognitionResultEventArgs) -> {
                final String s = speechRecognitionResultEventArgs.getResult().getText();
                Log.i(logTag, "Final result received: " + s);
                contentDl.add(s);

                contentDl.add(0, "\nOther : ");

                totalcontent.addAll(contentDl);
                contentDl.clear();
                //TODO : make to check conversation

                Log.d("message", TextUtils.join(" ", totalcontent));

                //setRecognizedText(TextUtils.join(" ", totalcontent));
            });

            //start and add to ThreadPool
            final Future<Void> uplinkTask = recoUl.startContinuousRecognitionAsync();
            setOnTaskCompletedListener(uplinkTask, result -> {
                ulStarted = true;
            });
            final Future<Void> downlinkTask = recoDl.startContinuousRecognitionAsync();
            setOnTaskCompletedListener(downlinkTask, result -> {
                dlStarted = true;
            });

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }


    private void stopConvert() {
        if (recoUl != null) {
            final Future<Void> task = recoUl.stopContinuousRecognitionAsync();
            setOnTaskCompletedListener(task, result -> {
                ulStarted = false;
                Log.i(logTag, "Continuous recognition stopped.");
            });
        }
        if (recoDl != null) {
            final Future<Void> task = recoDl.stopContinuousRecognitionAsync();
            setOnTaskCompletedListener(task, result -> {
                ulStarted = false;
                Log.i(logTag, "Continuous recognition stopped.");
            });
        }
        if (recoFile != null) {
            mp.stop();
            final Future<Void> task = recoFile.stopContinuousRecognitionAsync();
            setOnTaskCompletedListener(task, result -> {
                fileStarted = false;
                Log.i(logTag, "Continuous recognition stopped.");
            });
        }
        if (recoMic != null) {
            mp.stop();
            final Future<Void> task = recoMic.stopContinuousRecognitionAsync();
            setOnTaskCompletedListener(task, result -> {
                micStarted = false;
                Log.i(logTag, "Continuous recognition stopped.");
            });
        }

        //TODO: save text data


        //Toast.makeText(getApplicationContext(), TextUtils.join(" ", totalcontent),
        //       Toast.LENGTH_SHORT).show();

        contentUl.clear();
        contentDl.clear();
        contentFile.clear();
        contentMic.clear();
        totalcontent.clear();

        return;
    }

    private <T> void setOnTaskCompletedListener(Future<T> task, OnTaskCompletedListener<T> listener) {
        s_executorService.submit(() -> {
            T result = task.get();
            listener.onCompleted(result);
            return null;
        });
    }

    private interface OnTaskCompletedListener<T> {
        void onCompleted(T taskResult);
    }

    private static ExecutorService s_executorService;

    static {
        s_executorService = Executors.newCachedThreadPool();
    }

    private void startFileSTT() {
        final SpeechConfig speechConfig;
        try {
            speechConfig = SpeechConfig.fromSubscription(SpeechSubscriptionKey, SpeechRegion);
            speechConfig.setSpeechRecognitionLanguage("ko-KR");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return;
        }

        contentFile.clear();

        mp = MediaPlayer.create(this, R.raw.test_sample);
        mp.setLooping(false);
        mp.start();

        //try recognize continuously
        try {
            fileInput = AudioConfig.fromWavFileInput(copyAssetToCacheAndGetFilePath(sttFile));
            //fileInput = AudioConfig.fromStreamInput(createMicStream());
            recoFile = new SpeechRecognizer(speechConfig, fileInput);

            Toast.makeText(getApplicationContext(), "start", Toast.LENGTH_SHORT).show();
            //event that a speech ended
            recoFile.recognized.addEventListener((o, speechRecognitionResultEventArgs) -> {
                final String s = speechRecognitionResultEventArgs.getResult().getText();
                Log.i(logTag, "Final result received: " + s);
                contentFile.add(s+"\n");

                //TODO: 파일을 STT하여 contentFile에 저장 TextUtils.join(" ", contentFile)으로 문자열 변환
                try {
                    mlHandler.run2(TextUtils.join(" ", contentFile));
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            });

            //start and add to ThreadPool
            final Future<Void> fileTask = recoFile.startContinuousRecognitionAsync();
            setOnTaskCompletedListener(fileTask, result -> {
                fileStarted = true;
            });

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private String copyAssetToCacheAndGetFilePath(String filename) {
        File cacheFile = new File(getCacheDir() + "/" + filename);
        if (!cacheFile.exists()) {
            try {
                InputStream is = getAssets().open(filename);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                FileOutputStream fos = new FileOutputStream(cacheFile);
                fos.write(buffer);
                fos.close();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return cacheFile.getPath();
    }

    private void startMicSTT() {
        final SpeechConfig speechConfig;
        try {
            speechConfig = SpeechConfig.fromSubscription(SpeechSubscriptionKey, SpeechRegion);
            speechConfig.setSpeechRecognitionLanguage("ko-KR");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return;
        }

        contentMic.clear();
        mp = MediaPlayer.create(this, R.raw.test_sample);
        mp.setLooping(false);
        mp.start();

        //try recognize continuously
        try {
            micInput = AudioConfig.fromStreamInput(createMicStream());
            recoMic = new SpeechRecognizer(speechConfig, micInput);

            Toast.makeText(getApplicationContext(), "start", Toast.LENGTH_SHORT).show();
            //event that a speech ended
            recoMic.recognized.addEventListener((o, speechRecognitionResultEventArgs) -> {
                final String s = speechRecognitionResultEventArgs.getResult().getText();
                Log.i(logTag, "Final result received: " + s);
                contentFile.add(s+"\n");

                //TODO: 파일을 STT하여 contentFile에 저장 TextUtils.join(" ", contentFile)으로 문자열 변환
                //mlHandler.run(TextUtils.join(" ", contentFile));

                //번역 안쓸 때
                try {
                    mlHandler.run2(TextUtils.join(" ", contentFile));
                    contentFile.clear();
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });

            //start and add to ThreadPool
            final Future<Void> micTask = recoMic.startContinuousRecognitionAsync();
            setOnTaskCompletedListener(micTask, result -> {
                micStarted = true;
            });

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}