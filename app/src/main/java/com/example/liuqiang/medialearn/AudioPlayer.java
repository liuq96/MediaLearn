package com.example.liuqiang.medialearn;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class AudioPlayer {

    private static final String TAG = "AudioPlayer";

    private final int DEFAULT_STREAM_TYPE = AudioManager.STREAM_MUSIC;
    private final int DEFAULT_SAMPLE_RATE = 44100;
    private final int DEFAULT_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_STEREO;
    private final int DEFAULT_AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private final int DEFAULT_PLAY_MODE = AudioTrack.MODE_STREAM;

    private volatile boolean hasStartedPlayer = false;
    private volatile boolean isPlaying = false;
    private int mMinBufferSize = 0;
    private AudioTrack mAudioTrack;

    public boolean startPlayer(){
        return startPlayer(DEFAULT_STREAM_TYPE, DEFAULT_SAMPLE_RATE, DEFAULT_CHANNEL_CONFIG,
                DEFAULT_AUDIO_FORMAT);
    }

    public boolean startPlayer(int streamType, int sampleRateInHz, int channelConfig,
                                int audioFormat){
        if (hasStartedPlayer){
            Log.e(TAG, "AudioPlayer is playing now");
            return false;
        }

        mMinBufferSize = AudioTrack.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
        if (mMinBufferSize == AudioTrack.ERROR_BAD_VALUE){
            Log.e(TAG, "Invalid buffer size!");
            return false;
        }
        Log.d(TAG, "min buffer size: " + mMinBufferSize);

        mAudioTrack = new AudioTrack(streamType, sampleRateInHz, channelConfig, audioFormat,
                mMinBufferSize, DEFAULT_PLAY_MODE);
        if (mAudioTrack.getState() == AudioTrack.STATE_UNINITIALIZED){
            Log.e(TAG, "AudioTrack init fail!");
            return false;
        }
        hasStartedPlayer = true;
        Log.d(TAG, "init audio player success!");
        return true;
    }

    public boolean play(String path){
        if (!hasStartedPlayer){
            Log.e(TAG, "player not started!");
            return false;
        }
        isPlaying = true;
        new Thread(new AudioPlayerTask(path)).start();
        return true;
    }


    public void stop(){
        if (mAudioTrack != null){
            mAudioTrack.stop();
            mAudioTrack.release();
            mAudioTrack = null;
            isPlaying = false;
            hasStartedPlayer = false;
        }
    }

    private class AudioPlayerTask implements Runnable {
        private String path;

        public AudioPlayerTask(String path) {
            this.path = path;
        }

        @Override
        public void run() {
            FileInputStream fis = null;
            File file = null;
            try {
                file = new File(path);
                fis = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e(TAG, "error: " + e.getMessage());
                return;
            }
            long bytesRead = 0;
            long size = file.length();
            Log.d(TAG, "file length: " + size);
            byte[] data = new byte[mMinBufferSize];
            while (isPlaying && bytesRead < size){
                int ret = 0;
                try {
                    ret = fis.read(data, 0, mMinBufferSize);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "error: " + e.getMessage());
                    return;
                }
                if (ret != -1){
                    mAudioTrack.write(data, 0, ret);
                }
            }

            try {
                fis.close();
            } catch (IOException e) {
                Log.e(TAG, "error: " + e.getMessage());
                e.printStackTrace();
            }

        }
    }

}
