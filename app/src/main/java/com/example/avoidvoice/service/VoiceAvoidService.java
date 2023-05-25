package com.example.avoidvoice.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.avoidvoice.BuildConfig;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class VoiceAvoidService extends Service {
    private static final String SpeechSubscriptionKey = BuildConfig.STT_KEY;
    private static final String SpeechRegion = "koreacentral";
    private static final int MICSOURCE = MediaRecorder.AudioSource.VOICE_UPLINK;
    private static final int AUDIOSOURCE = MediaRecorder.AudioSource.VOICE_DOWNLINK;

    private static final String logTag = "stt";

    private boolean micStarted = false;
    private boolean audioStarted = false;
    private boolean fileStarted = false;
    private SpeechRecognizer recoMic = null;
    private SpeechRecognizer recoAudio = null;
    private SpeechRecognizer recoFile = null;
    private AudioConfig micInput = null;
    private AudioConfig audioInput = null;
    private AudioConfig fileInput = null;
    private ArrayList<String> contentMic = new ArrayList<>();
    private ArrayList<String> contentAudio = new ArrayList<>();
    private ArrayList<String> contentFile = new ArrayList<>();

    private ArrayList<String> totalcontent = new ArrayList<>();

    private STTAudioStream microphoneStream;
    private STTAudioStream audioStream;


    private String sttFile = "pronunciation_assessment.wav";

    public VoiceAvoidService() {

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
        boolean test = intent.getBooleanExtra("test", false);
        if(test)
            startFileSTT();
        else startConvert();

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopConvert();
    }

    private STTAudioStream createMicrophoneStream() {
        this.releaseMicrophoneStream();

        microphoneStream = new STTAudioStream(MICSOURCE);
        return microphoneStream;
    }

    private STTAudioStream createAudioStream() {
        this.releaseAudioStream();

        audioStream = new STTAudioStream(AUDIOSOURCE);
        return audioStream;
    }

    private void releaseMicrophoneStream() {
        if (microphoneStream != null) {
            microphoneStream.close();
            microphoneStream = null;
        }
    }

    private void releaseAudioStream() {
        if (audioStream != null) {
            audioStream.close();
            audioStream = null;
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
        contentMic.clear();
        contentAudio.clear();

        //try recognize continuously
        try {
            micInput = AudioConfig.fromStreamInput(createMicrophoneStream());
            audioInput = AudioConfig.fromStreamInput(createAudioStream());

            recoMic = new SpeechRecognizer(speechConfig, micInput);
            recoAudio = new SpeechRecognizer(speechConfig, audioInput);

            Toast.makeText(getApplicationContext(), "start", Toast.LENGTH_SHORT).show();
            //event that a speech ended
            recoMic.recognized.addEventListener((o, speechRecognitionResultEventArgs) -> {
                final String s = speechRecognitionResultEventArgs.getResult().getText();
                Log.i(logTag, "Final result received: " + s);
                contentMic.add(s);

                contentMic.add(0, "\nMe : ");

                totalcontent.addAll(contentMic);
                contentMic.clear();
            });


            recoAudio.recognized.addEventListener((o, speechRecognitionResultEventArgs) -> {
                final String s = speechRecognitionResultEventArgs.getResult().getText();
                Log.i(logTag, "Final result received: " + s);
                contentAudio.add(s);

                contentAudio.add(0, "\nOther : ");

                totalcontent.addAll(contentAudio);
                contentAudio.clear();
                //TODO : make to check conversation

                Log.d("message", TextUtils.join("", totalcontent));

                //setRecognizedText(TextUtils.join(" ", totalcontent));
            });

            //start and add to ThreadPool
            final Future<Void> micTask = recoMic.startContinuousRecognitionAsync();
            setOnTaskCompletedListener(micTask, result -> {
                micStarted = true;
            });
            final Future<Void> audioTask = recoAudio.startContinuousRecognitionAsync();
            setOnTaskCompletedListener(audioTask, result -> {
                audioStarted = true;
            });

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }


    private void stopConvert() {
        if (recoMic != null) {
            final Future<Void> task = recoMic.stopContinuousRecognitionAsync();
            setOnTaskCompletedListener(task, result -> {
                micStarted = false;
                Log.i(logTag, "Continuous recognition stopped.");
            });
        }
        if (recoAudio != null) {
            final Future<Void> task = recoAudio.stopContinuousRecognitionAsync();
            setOnTaskCompletedListener(task, result -> {
                micStarted = false;
                Log.i(logTag, "Continuous recognition stopped.");
            });
        }
        if (recoFile != null) {
            final Future<Void> task = recoFile.stopContinuousRecognitionAsync();
            setOnTaskCompletedListener(task, result -> {
                fileStarted = false;
                Log.i(logTag, "Continuous recognition stopped.");
            });
        }

        //TODO: save text data


        Toast.makeText(getApplicationContext(), TextUtils.join("", totalcontent),
                Toast.LENGTH_SHORT).show();

        contentMic.clear();
        contentAudio.clear();
        contentFile.clear();
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

        //try recognize continuously
        try {
            fileInput = AudioConfig.fromWavFileInput(copyAssetToCacheAndGetFilePath(sttFile));

            recoFile = new SpeechRecognizer(speechConfig, fileInput);

            Toast.makeText(getApplicationContext(), "start", Toast.LENGTH_SHORT).show();
            //event that a speech ended
            recoFile.recognized.addEventListener((o, speechRecognitionResultEventArgs) -> {
                final String s = speechRecognitionResultEventArgs.getResult().getText();
                Log.i(logTag, "Final result received: " + s);
                contentFile.add(s);
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
}