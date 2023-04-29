package com.example.avoidvoice.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.avoidvoice.BuildConfig;
import com.microsoft.cognitiveservices.speech.KeywordRecognitionModel;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class VoiceAvoidService extends Service {
    private static final String SpeechSubscriptionKey = BuildConfig.STT_KEY;
    private static final String SpeechRegion = "koreacentral";

    private static final String logTag = "stt";
    private boolean sttStarted = false;
    private SpeechRecognizer reco = null;
    private AudioConfig audioInput = null;
    private ArrayList<String> content = new ArrayList<>();

    private ArrayList<String> totalcontent = new ArrayList<>();

    private MicrophoneStream microphoneStream;
    private MicrophoneStream createMicrophoneStream() {
        this.releaseMicrophoneStream();

        microphoneStream = new MicrophoneStream();
        return microphoneStream;
    }
    private void releaseMicrophoneStream() {
        if (microphoneStream != null) {
            microphoneStream.close();
            microphoneStream = null;
        }
    }

    public VoiceAvoidService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        onTaskRemoved(intent);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
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
    public void onCreate(){
        super.onCreate();
        startConvert();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopConvert();
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

        //try recognize continuously
        if(!sttStarted) {
            sttStarted = true;
            try {
                content.clear();
                audioInput = AudioConfig.fromStreamInput(createMicrophoneStream());
                reco = new SpeechRecognizer(speechConfig, audioInput);

                Toast.makeText(getApplicationContext(),"start1",Toast.LENGTH_SHORT).show();
                reco.recognizing.addEventListener((o, speechRecognitionResultEventArgs) -> {
                    final String s = speechRecognitionResultEventArgs.getResult().getText();
                    Log.i(logTag, "Intermediate result received: " + s);
                    content.add(s);
                    content.remove(content.size() - 1);

                    Toast.makeText(getApplicationContext(),"start",Toast.LENGTH_SHORT).show();
                });

                //event that a speech ended
                reco.recognized.addEventListener((o, speechRecognitionResultEventArgs) -> {
                    final String s = speechRecognitionResultEventArgs.getResult().getText();
                    Log.i(logTag, "Final result received: " + s);
                    content.add(s);

                    content.add(0, "\nMe : ");
                    totalcontent.addAll(content);
                    content.clear();
                    //TODO : make to check conversation
                    Toast.makeText(getApplicationContext(),TextUtils.join("",totalcontent),
                            Toast.LENGTH_SHORT).show();

                    //setRecognizedText(TextUtils.join(" ", content));
                });

                final Future<Void> task = reco.startContinuousRecognitionAsync();
                setOnTaskCompletedListener(task, result -> {
                    sttStarted = true;
                });
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private void stopConvert() {
        sttStarted = false;
        if (reco != null) {
            final Future<Void> task = reco.stopContinuousRecognitionAsync();
            setOnTaskCompletedListener(task, result -> {
                Log.i(logTag, "Continuous recognition stopped.");
            });
            //TODO : save converted content or result



        } else {
        }
        Toast.makeText(getApplicationContext(),"end",
                Toast.LENGTH_SHORT).show();

        content.clear();
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

}