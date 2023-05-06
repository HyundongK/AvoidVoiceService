package com.example.avoidvoice.service;

import android.media.AudioFormat;
import android.media.AudioRecord;

import com.microsoft.cognitiveservices.speech.audio.AudioStreamFormat;
import com.microsoft.cognitiveservices.speech.audio.PullAudioInputStreamCallback;

public class STTAudioStream extends PullAudioInputStreamCallback {
    private final static int SAMPLE_RATE = 16000;
    private final AudioStreamFormat format;
    private AudioRecord recorder;

    public STTAudioStream(int source) {
        this.format = AudioStreamFormat.getWaveFormatPCM(SAMPLE_RATE, (short) 16, (short) 1);
        this.initMic(source);
    }

    public AudioStreamFormat getFormat() {
        return this.format;
    }

    @Override
    public int read(byte[] bytes) {
        if (this.recorder != null) {
            long ret = this.recorder.read(bytes, 0, bytes.length);
            return (int) ret;
        }
        return 0;
    }

    @Override
    public void close() {
        this.recorder.release();
        this.recorder = null;
    }

    private void initMic(int source) {
        // Note: currently, the Speech SDK support 16 kHz sample rate, 16 bit samples, mono (single-channel) only.
        AudioFormat af = new AudioFormat.Builder()
                .setSampleRate(SAMPLE_RATE)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                .build();

        this.recorder = new AudioRecord.Builder()
                .setAudioSource(source) //MediaRecorder.AudioSource.VOICE_RECOGNITION
                .setAudioFormat(af)
                .build();

        this.recorder.startRecording();
    }
}