package com.example.liuqiang.medialearn.media;

import android.app.Application;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class AudioCapture {
    private static final String TAG = "AudioCapture";
    private static final int DEFAULT_SOURCE = MediaRecorder.AudioSource.MIC;
    private static final int DEFAULT_RECORDER_SAMPLERATE = 44100;
    private static final int DEFAULT_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int DEFAULT_AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    private AudioRecord audioRecord;
    private volatile boolean isRecording = false;
    private int minBufferSize;

    public boolean startRecord(String audioPath){
        return startRecord(DEFAULT_SOURCE, DEFAULT_RECORDER_SAMPLERATE, DEFAULT_CHANNELS,
                DEFAULT_AUDIO_FORMAT, audioPath);
    }

    public boolean startRecord(int audioSource, int sampleRateInHz, int channelConfig,
                         int audioFormat, String path){
        if (isRecording){
            Log.e(TAG, "AudioCapture is recording now");
            return false;
        }
        minBufferSize = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig,
                audioFormat);
        if (minBufferSize == AudioRecord.ERROR_BAD_VALUE){
            Log.e(TAG, "Invalid buffer size");
            return false;
        }
        Log.d(TAG, "minBufferSize = " + minBufferSize);
        audioRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig,
                audioFormat, minBufferSize);
        if (audioRecord.getState() == AudioRecord.STATE_UNINITIALIZED){
            Log.e(TAG, "AudioRecord initialize fail!");
            return false;
        }
        Log.d(TAG, "start record");
        audioRecord.startRecording();
        isRecording = true;
        new Thread(new AudioRecordTask(path)).start();
        return true;
    }

    public void stopRecord(){
        if (audioRecord != null){
            Log.d(TAG, "stop record");
            isRecording = false;
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
    }

    private class AudioRecordTask implements Runnable {
        private String path;

        AudioRecordTask(String path){
            this.path = path;
        }

        @Override
        public void run() {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(path);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e(TAG, "error: " + e.getMessage());
                return;
            }
            ByteBuffer buffer = ByteBuffer.allocateDirect(minBufferSize);
            while (isRecording){
                int ret = audioRecord.read(buffer, minBufferSize);
                Log.d(TAG, "ret: " + ret);
                if (ret == AudioRecord.ERROR_INVALID_OPERATION){
                    Log.e(TAG, "Error ERROR_INVALID_OPERATION");
                } else if (ret == AudioRecord.ERROR_BAD_VALUE){
                    Log.e(TAG, "Error ERROR_BAD_VALUE");
                } else {
                    try {
                        fos.write(buffer.array(), 0, minBufferSize);
                        buffer.clear();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "error: " + e.getMessage());
                        return;
                    }
                }
            }

            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "error: " + e.getMessage());
            }
        }
    }

}
