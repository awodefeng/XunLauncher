package com.xxun.xunlauncher.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.media.AudioSystem;
import android.os.Build;
import com.xxun.xunlauncher.R;

/*
 * am start -n com.xxun.audioft/.AudioRecordActivity -e path "$1"
 * --ei mic "$2" --ei samplerate "$3" --ei bit "$4" --ei channel "$5" --ei time "$6"
 */

public class AudioPlayRecordActivity extends Activity {
    static final String TAG = "AudioPlayRecord";
    private AudioManager mAudioManager;
    private TextView tv;

    public final static int INPUT_BOTH = 0;
    public final static int INPUT_MIC1 = 1;
    public final static int INPUT_MIC2 = 2;
    public final static int INPUT_END = 2;

    private int audioSource = MediaRecorder.AudioSource.MIC;
    // sample rate: 44100, 22050, 16000, 11025
    private static int sampleRateInHz = 44100;
    // CHANNEL_IN_STEREO
    private static int channum = 1;
    private static int channel = AudioFormat.CHANNEL_IN_MONO;
    // 8BIT seems not supported in audioRecorder
    private static int audioFormat = AudioFormat.ENCODING_PCM_16BIT; // only support 16bit
    private int device = INPUT_MIC2;
    private int length = 1;
    private String path = "sdcard/test.wav"; // "sdcard/test.wav";
    private String tempFile = "/sdcard/recdata"; // "sdcard/recdata";
    private int bufferSizeInBytes = 0;
    private int mSystemVol = 0;
    private int mMaxvol = 0;

    private int mHeadphoneState, mHeadsetState;
    PowerManager.WakeLock mWakeLock = null;
    PowerManager mPM;

    private boolean isRecord = false;
    private AudioRecord audioRecord = null;
    private Handler mHandler = new Handler();
    private Runnable task = new Runnable() {
        public void run() {
            ///stopRecord();
            finish();
        }
    };
    // play
    private MediaPlayer mMediaPlayer;
    private String musicPath;
    private static int playIndex = 0;
    private int outputDevice = 0; //1: speaker, 2: receiver
    private int volume = 15;
    public final static int OUTPUT_BEGIN = 1;
    public final static int OUTPUT_RECEIVER = 1;
    public final static int OUTPUT_EARPHONE = 2;
    public final static int OUTPUT_LEFTEAR = 3;
    public final static int OUTPUT_RIGHTEAR = 4;
    public final static int OUTPUT_SPK_BEGIN = 5;
    public final static int OUTPUT_SPEAKER = 5;
    public final static int OUTPUT_LEFTSPK = 6;
    //public final static int OUTPUT_LEFTSPK2 = 7;
    public final static int OUTPUT_RIGHTSPK = 8;
    //public final static int OUTPUT_RIGHTSPK2 = 9;
    public final static int OUTPUT_SPK1 = 11;
    public final static int OUTPUT_SPK2 = 12;
    public final static int OUTPUT_SPK3 = 13;
    public final static int OUTPUT_SPK4 = 14;
    public final static int OUTPUT_SPK_END = 14;
    public final static int OUTPUT_END = 15;

    private void initAudioRecord() {
        mHeadphoneState = AudioSystem.getDeviceConnectionState(AudioSystem.DEVICE_OUT_WIRED_HEADPHONE,"");
        mHeadsetState = AudioSystem.getDeviceConnectionState(AudioSystem.DEVICE_OUT_WIRED_HEADSET,"");
        Log.i(TAG, "audio play record: start");
        //android.os.SystemProperties.set("persist.sys.audioft.rec_end", "0");
        Intent i = getIntent();
        if (parseIntent(i)) {
            if (device == INPUT_MIC1) {
                mAudioManager.setParameters("record_select_mic=handset-mic");
            } else if (device == INPUT_MIC2) {
                mAudioManager.setParameters("record_select_mic=secondary-mic");
            } else {
                mAudioManager.setParameters("record_select_mic=none");
            }
            if (device == INPUT_MIC1 || device == INPUT_MIC2) {
                if (mHeadphoneState != AudioSystem.DEVICE_STATE_UNAVAILABLE)
                    AudioSystem.setDeviceConnectionState(AudioSystem.DEVICE_OUT_WIRED_HEADPHONE,AudioSystem.DEVICE_STATE_UNAVAILABLE,
                AudioSystem.DEVICE_OUT_WIRED_HEADPHONE_NAME);
                if (mHeadsetState != AudioSystem.DEVICE_STATE_UNAVAILABLE)
                    AudioSystem.setDeviceConnectionState(AudioSystem.DEVICE_OUT_WIRED_HEADSET,AudioSystem.DEVICE_STATE_UNAVAILABLE,
                AudioSystem.DEVICE_OUT_WIRED_HEADSET_NAME);
            }
            if (startRecord()) {
                tv.setText("Record file " + path + " for time " + length);
                mHandler.postDelayed(task, length * 1000 + 120);
            }
            else {
                tv.setText("create audiorecord fail!");
            }
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    finish();
                }
            }, 1000);
        }
    }

    private boolean startRecord() {
        channel = (channum == 1) ? (AudioFormat.CHANNEL_IN_MONO) : AudioFormat.CHANNEL_IN_STEREO;
        bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz,
        channel, audioFormat);
        Log.i(TAG, "AudioRecord getMinBufferSize " + bufferSizeInBytes);
        if (isRecord) {
            return false;
        }
        if (audioRecord == null) {
            try {
                audioRecord = new AudioRecord(audioSource, sampleRateInHz,
                channel, audioFormat, bufferSizeInBytes);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return false;
            }
            if (audioRecord == null) return false;
            if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
                Log.i(TAG, "AudioRecord != STATE_INITIALIZED");
                return false;
            }
        }
        Log.i(TAG, "start Record");
        audioRecord.startRecording();
        isRecord = true;
        new Thread(new AudioRecordThread()).start();
        return true;
    }

    private boolean parseIntent(Intent data) {
        if (data == null) return false;

        path = data.getStringExtra("path");
        length = data.getIntExtra("time", 0);
        channum = data.getIntExtra("channel", 2);
        device = data.getIntExtra("mic", INPUT_BOTH);
        sampleRateInHz = data.getIntExtra("samplerate", 0);
/*
        int bitrate = data.getIntExtra("bit", 0);
        if (bitrate == 8) {
            audioFormat = AudioFormat.ENCODING_PCM_8BIT;
        } else if (bitrate == 16) {
            audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        }
*/
        if (path == null) return false;
        if (device < INPUT_BOTH || device > INPUT_END) {
            if (tv != null)
                tv.setText("output device error!");
            Log.e(TAG, "output device error!");
            return false;
        }
        if (sampleRateInHz == 0) {
            if (tv != null)
                tv.setText("sampleRate error! " + sampleRateInHz);
                Log.e(TAG, "sampleRate error! " + sampleRateInHz);
                return false;
            }
/*
            if (audioFormat == AudioFormat.ENCODING_INVALID) {
            	if (tv != null)
            		tv.setText("audioFormat error! " + audioFormat);
            	Log.e(TAG, "audioFormat error! " + audioFormat);
            	return false;
            }
*/
        if (channum > 2) {
            if (tv != null)
                tv.setText("channel set error! " + channum);
            Log.e(TAG, "channel error! " + channum);
            return false;
        }
        if (length == 0) {
            if (tv != null)
                tv.setText("length error! " + length);
            Log.e(TAG, "sampleRate error! " + length);
            return false;
        }

        Log.i(TAG, "record " + path + " with mic " + device + " sample " + sampleRateInHz + " channel " + channum + " time " + length);
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        try {
            if (!file.createNewFile())
                return false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        file = new File(tempFile);
        if (file.exists()) {
            file.delete();
        }
        try {
            if (!file.createNewFile())
                return false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //return true;

        // play
        musicPath = data.getStringExtra("music_path");

        mMaxvol = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        Log.i(TAG, "parseIntent | mMaxvol=" + mMaxvol);
        volume = data.getIntExtra("volume", /*AudioCommandsReceiver.DEFAULT_VOL*/mMaxvol);
        outputDevice = data.getIntExtra("output", 1);

        if (musicPath == null) return false;

        if (outputDevice < OUTPUT_SPK_BEGIN || outputDevice > OUTPUT_SPK_END) {
            if (tv != null)
                tv.setText("output device error!");
            Log.e(TAG, "output device error!");
            return false;
        }

        File fileOutput = new File(musicPath);
        if (fileOutput.exists() && fileOutput.canRead()) {
            //
        } else {
            Log.e(TAG, "file can't play: exist " + fileOutput.exists() + " readable " + fileOutput.canRead());
            if (tv != null)
                tv.setText("file can't be played");
            return false;
        }
        return true;
    }

    class AudioRecordThread implements Runnable {
        @Override
        public void run() {
            writeDateTOFile();
        }
    }

    private void stopRecord() {
        Log.i(TAG, "audio record: stop");
        if (isRecord && audioRecord != null) {
        isRecord = false;
        audioRecord.stop();
        audioRecord.release();
        audioRecord = null;
        copyWaveFile(tempFile, path);
        //   android.os.SystemProperties.set("persist.sys.audioft.rec_end", "1");
        }
    }

    private void deinitAudioRecord() {
        if (mHeadphoneState != AudioSystem.DEVICE_STATE_UNAVAILABLE)
            AudioSystem.setDeviceConnectionState(AudioSystem.DEVICE_OUT_WIRED_HEADPHONE, mHeadphoneState,
            AudioSystem.DEVICE_OUT_WIRED_HEADPHONE_NAME);
        if (mHeadsetState != AudioSystem.DEVICE_STATE_UNAVAILABLE)
           // if(Build.LCT_PROJECT_NAME.contains("p2200")){
                AudioSystem.setDeviceConnectionState(AudioSystem.DEVICE_OUT_WIRED_HEADSET,AudioSystem.DEVICE_STATE_UNAVAILABLE,
                        AudioSystem.DEVICE_OUT_WIRED_HEADSET_NAME);
            /*}else{
                AudioSystem.setDeviceConnectionState(AudioSystem.DEVICE_OUT_WIRED_HEADSET,mHeadsetState,
                        AudioSystem.DEVICE_OUT_WIRED_HEADSET_NAME, "");
            }*/
        mAudioManager.setParameters("ForceUseSpecificMic=0");
    }

    // play
    private void initAudioPlay() {
        //mAudioManager.setMode(AudioManager.MODE_NORMAL);
        mAudioManager.setMode(AudioManager.MODE_NORMAL);
        Log.i(TAG, "audio play: start");
        Intent i = getIntent();
        mHeadsetState = AudioSystem.getDeviceConnectionState(AudioSystem.DEVICE_OUT_WIRED_HEADSET,"");
        mHeadphoneState = AudioSystem.getDeviceConnectionState(AudioSystem.DEVICE_OUT_WIRED_HEADPHONE,"");
        Log.i(TAG, "mHeadsetState:  " + mHeadsetState + " mHeadphoneState: " + mHeadphoneState);
        if (parseIntent(i)) {
            if (tv != null)
                tv.setText("Play file " + musicPath);
                playFile();
            } else {
                new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    finish();
                }
            }, 1000);
        }
    }

    private void deinitAudioPlay() {
        if (outputDevice >= OUTPUT_SPK_BEGIN && outputDevice <= OUTPUT_SPK_END) {
            mAudioManager.setSpeakerphoneOn(false);
            mAudioManager.setMode(AudioManager.MODE_NORMAL);
            //mAudioManager.setSpeakerphoneOn(false);
            //mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            if (mHeadsetState != AudioSystem.DEVICE_STATE_UNAVAILABLE)
                //if(Build.LCT_PROJECT_NAME.contains("p2200")){
                    AudioSystem.setDeviceConnectionState(AudioSystem.DEVICE_OUT_WIRED_HEADSET,AudioSystem.DEVICE_STATE_UNAVAILABLE,
                            AudioSystem.DEVICE_OUT_WIRED_HEADSET_NAME);
               /* }else {
                    AudioSystem.setDeviceConnectionState(AudioSystem.DEVICE_OUT_WIRED_HEADSET, mHeadsetState,
                            AudioSystem.DEVICE_OUT_WIRED_HEADSET_NAME, "");
                }*/
            if (mHeadphoneState != AudioSystem.DEVICE_STATE_UNAVAILABLE)
                AudioSystem.setDeviceConnectionState(AudioSystem.DEVICE_OUT_WIRED_HEADPHONE,mHeadphoneState,
            AudioSystem.DEVICE_OUT_WIRED_HEADPHONE_NAME);
/*
            final int curIndex = playIndex;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "audio play: restore speaker, index = " + playIndex + " curIndex =" + curIndex);
                    //Log.i(TAG, "now play state = " + playState);
                if (curIndex == playIndex)
                    mAudioManager.setSpeakerphoneOn(true);
                }
            }, 3200);
*/
        } else if (outputDevice == OUTPUT_RECEIVER) {
            mAudioManager.setMode(AudioManager.MODE_NORMAL);
            if (mHeadsetState != AudioSystem.DEVICE_STATE_UNAVAILABLE)
                //if(Build.LCT_PROJECT_NAME.contains("p2200")){
                    AudioSystem.setDeviceConnectionState(AudioSystem.DEVICE_OUT_WIRED_HEADSET,AudioSystem.DEVICE_STATE_UNAVAILABLE,
                            AudioSystem.DEVICE_OUT_WIRED_HEADSET_NAME);
                /*}else {
                    AudioSystem.setDeviceConnectionState(AudioSystem.DEVICE_OUT_WIRED_HEADSET, mHeadsetState,
                            AudioSystem.DEVICE_OUT_WIRED_HEADSET_NAME, "");
                }*/
            if (mHeadphoneState != AudioSystem.DEVICE_STATE_UNAVAILABLE)
                AudioSystem.setDeviceConnectionState(AudioSystem.DEVICE_OUT_WIRED_HEADPHONE,mHeadphoneState,
            AudioSystem.DEVICE_OUT_WIRED_HEADPHONE_NAME);
            //mAudioManager.setSpeakerphoneOn(true);
        }
        Log.i(TAG, "audio play: stop");
        ///finish();
    }

    private void playFile() {
        Log.i(TAG, "to play " + musicPath);
        playIndex ++;
        mMediaPlayer = new MediaPlayer();
        if ((mMediaPlayer != null) && (!mMediaPlayer.isPlaying())) {
            try {
                mSystemVol = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                Log.i(TAG, "playFile | mSystemVol=" + mSystemVol);
                Log.i(TAG, "playFile | mHeadphoneState=" + mHeadphoneState);
                Log.i(TAG, "playFile | mHeadsetState=" + mHeadphoneState);
                volume = volume > mMaxvol ? mMaxvol : volume;
                if (outputDevice >= OUTPUT_SPK_BEGIN && outputDevice <= OUTPUT_SPK_END) {
                    if (mHeadsetState != AudioSystem.DEVICE_STATE_UNAVAILABLE)
                        AudioSystem.setDeviceConnectionState(AudioSystem.DEVICE_OUT_WIRED_HEADSET,AudioSystem.DEVICE_STATE_UNAVAILABLE,
                            AudioSystem.DEVICE_OUT_WIRED_HEADSET_NAME);
                    if (mHeadphoneState != AudioSystem.DEVICE_STATE_UNAVAILABLE)
                        AudioSystem.setDeviceConnectionState(AudioSystem.DEVICE_OUT_WIRED_HEADPHONE,AudioSystem.DEVICE_STATE_UNAVAILABLE,
                            AudioSystem.DEVICE_OUT_WIRED_HEADPHONE_NAME);

                        mAudioManager.setMode(AudioManager.MODE_NORMAL);
                    mAudioManager.setSpeakerphoneOn(true);
                    setVolumeControlStream(AudioManager.STREAM_MUSIC);
                    //AudioSystem.setForceUse(AudioSystem.FOR_MEDIA, AudioSystem.FORCE_SPEAKER);
                    Log.i(TAG, "playFile | volume =" + volume);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
                } else if (outputDevice == OUTPUT_RECEIVER) {
                    int maxvolcall = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
                    Log.i(TAG, "maxvol of call " + maxvolcall);
                    setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
                    int vol_max_music = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, vol_max_music, 0);

                    float vol = ((float) volume / 15) * maxvolcall;
                    //mAudioManager.setMode(AudioManager.MODE_IN_CALL);
                    mAudioManager.setSpeakerphoneOn(false);
                    // AudioSystem.setForceUse(AudioSystem.FOR_MEDIA, AudioSystem.FORCE_HEADPHONES);
                    if (mHeadsetState != AudioSystem.DEVICE_STATE_UNAVAILABLE)
                        AudioSystem.setDeviceConnectionState(AudioSystem.DEVICE_OUT_WIRED_HEADSET,AudioSystem.DEVICE_STATE_UNAVAILABLE,
                            AudioSystem.DEVICE_OUT_WIRED_HEADSET_NAME);
                    if (mHeadphoneState != AudioSystem.DEVICE_STATE_UNAVAILABLE)
                        AudioSystem.setDeviceConnectionState(AudioSystem.DEVICE_OUT_WIRED_HEADPHONE,AudioSystem.DEVICE_STATE_UNAVAILABLE,
                            AudioSystem.DEVICE_OUT_WIRED_HEADPHONE_NAME);

                    mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, (int) vol, 0);
                } else {
                    Log.i(TAG, "play from earpiece");
                    mAudioManager.setMode(AudioManager.MODE_NORMAL);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
                    mAudioManager.setSpeakerphoneOn(false);
                }

                if (OUTPUT_LEFTEAR == outputDevice || OUTPUT_LEFTSPK == outputDevice) {
                    mMediaPlayer.setVolume(1.0f, 0.0f);
                } else if (OUTPUT_RIGHTEAR == outputDevice || OUTPUT_RIGHTSPK == outputDevice) {
                    mMediaPlayer.setVolume(0.0f, 1.0f);
                } else if (OUTPUT_SPK1 == outputDevice) {
                    Log.i(TAG, "OUTPUT_SPK1 : leftup");
                    mAudioManager.setParameters("speaker_mic_test=start");
                    mAudioManager.setParameters("speaker_mic_test=leftup");
                    //mMediaPlayer.setVolume(1.0f, 1.0f);
                } else if (OUTPUT_SPK2 == outputDevice) {
                    Log.i(TAG, "OUTPUT_SPK1 : rightup");
                    mAudioManager.setParameters("speaker_mic_test=start");
                    mAudioManager.setParameters("speaker_mic_test=rightup");
                    //mMediaPlayer.setVolume(1.0f, 1.0f);
                } else if (OUTPUT_SPK3 == outputDevice) {
                    Log.i(TAG, "OUTPUT_SPK1 : leftdown");
                    mAudioManager.setParameters("speaker_mic_test=start");
                    mAudioManager.setParameters("speaker_mic_test=leftdown");
                    //mMediaPlayer.setVolume(1.0f, 1.0f);
                } else if (OUTPUT_SPK4 == outputDevice) {
                    Log.i(TAG, "OUTPUT_SPK1 : rightdown");
                    mAudioManager.setParameters("speaker_mic_test=start");
                    mAudioManager.setParameters("speaker_mic_test=rightdown");
                    //mMediaPlayer.setVolume(1.0f, 1.0f);
                } else
                    mMediaPlayer.setVolume(1.0f, 1.0f);

                if (outputDevice == OUTPUT_RECEIVER) {
                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
                } else {
                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                }
                mMediaPlayer.setDataSource(musicPath);
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion( MediaPlayer paramMediaPlayer) {
                        Log.i(TAG, "play complete ");
                        // android.os.SystemProperties.set("persist.sys.audioft.play_end", "1");
                        /*mMediaPlayer.stop();
                        mMediaPlayer.release();
                        mMediaPlayer = null;*/
                        mAudioManager.setParameters("speaker_mic_test=stop");
                    }
                });

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(200);
                            mMediaPlayer.prepare();
                            mMediaPlayer.setLooping(true);
                            mMediaPlayer.start();
                            Log.i(TAG, "play start ");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void stopPlay() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            //android.os.SystemProperties.set("persist.sys.audioft.play_end", "1");
        }
        Log.i(TAG, "stopPlay");
        mAudioManager.setParameters("speaker_mic_test=stop");
    }

    @Override
    public void onCreate(Bundle icycle) {
        Log.d(TAG, "onCreate");

        super.onCreate(icycle);
        setContentView(R.layout.audio_main);
        tv = (TextView) findViewById(R.id.text);
        mPM = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = mPM.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK |  PowerManager.ACQUIRE_CAUSES_WAKEUP, "audioplay");
        //mWakeLock = mPM.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "AudioPlayRecord");
        mWakeLock.acquire();
        mAudioManager = ((AudioManager)getSystemService(Context.AUDIO_SERVICE));
        initAudioPlay();
        initAudioRecord();
    }

    @Override
    protected void onStart() {
        super.onResume();
        Log.d(TAG, "onStart");
/*
        mAudioManager.setParameter("SmartPA_algo", "disable");
        initAudioPlay();
        initAudioRecord();
*/
    }

    @Override
    public void onStop() {
        //stopRecord();
        Log.d(TAG, "onStop");
/*
        stopRecord();
        deinitAudioRecord();
        stopPlay();
        deinitAudioPlay();
        deinitAudio();
        mAudioManager.setParameter("SmartPA_algo", "enable");
*/
        super.onStop();
    }

    protected void onDestroy() {
        Log.d(TAG, "onDestroy");

        stopRecord();
        deinitAudioRecord();
        stopPlay();
        deinitAudioPlay();
        deinitAudio();

        
        if (mWakeLock != null) {
            mWakeLock.release();
            mWakeLock = null;
        }
        super.onDestroy();
    }

    private void deinitAudio() {
        AudioSystem.setForceUse(AudioSystem.FOR_MEDIA, AudioSystem.FORCE_NONE);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mSystemVol, 0);
        mAudioManager.setSpeakerphoneOn(false);
        if (mWakeLock != null) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }

    private void writeDateTOFile() {
        byte[] audiodata = new byte[bufferSizeInBytes];
        FileOutputStream fos = null;
        int readsize = 0;
        try {
            File file = new File(tempFile);
            if (file.exists()) {
                file.delete();
            }
            fos = new FileOutputStream(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (isRecord == true) {
            readsize = audioRecord.read(audiodata, 0, bufferSizeInBytes);
            //Log.i(TAG, "read buffer " + readsize);
            if (AudioRecord.ERROR_INVALID_OPERATION != readsize && fos!=null) {
                try {
                    fos.write(audiodata);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
        	if(fos != null)
        		fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void copyWaveFile(String inFilename, String outFilename) {
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = sampleRateInHz;
        int channels = channum;
        long byteRate = 16 * sampleRateInHz * channels / 8;
        byte[] data = new byte[bufferSizeInBytes];
        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;
            WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
                    longSampleRate, channels, byteRate);
            while (in.read(data) != -1) {
                out.write(data);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        new File(inFilename).delete();
    }

    private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen,
            long totalDataLen, long longSampleRate, int channels, long byteRate)
            throws IOException {
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f';
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (channels * 16 / 8); // block align
        header[33] = 0;
        header[34] = 16; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        out.write(header, 0, 44);
    }

}
