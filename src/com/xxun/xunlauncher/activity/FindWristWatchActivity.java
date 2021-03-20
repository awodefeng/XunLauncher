package com.xxun.xunlauncher.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
//import android.support.annotation.RequiresApi;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.xxun.xunlauncher.R;

import java.lang.ref.WeakReference;

/**
 * Created by liaoyi on 12/22/17.
 */
public class FindWristWatchActivity extends Activity {

    private static final String TAG = "FindWristWatchActivity";

    SoundPool soundPool;
    int sound;

    //    private MediaPlayer mMediaPlayer;
    private PowerManager.WakeLock wakeLock;
    StopHandler sHandler;
    AudioManager audioManager;

    int maxAlarmVolumn;
    int currentAlarmVolumn;

    //    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_wristwatch);
        Log.i(TAG, "onCreate: lyly ");
        init();
//        playAlertSoundAndWakeup();

        if (!isDisturb()) {
            initSound();
        }
    }

    private void init() {
        Button btnOk = (Button) findViewById(R.id.find_wristwatch_ok);
        ImageView imgFind = (ImageView) findViewById(R.id.find_wristwatch_img);
        AnimationDrawable animationDrawable = (AnimationDrawable) imgFind.getDrawable();
        animationDrawable.start();
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxAlarmVolumn = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        currentAlarmVolumn = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);

        Log.i(TAG, "init: lyly max " + maxAlarmVolumn + " current " + currentAlarmVolumn);

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.xiaoxun.time.silence");
        registerReceiver(broadcastReceiver, filter);

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "findwatch");
        wakeLock.acquire();//可添加超时参数，这里保持常亮

        sHandler = new StopHandler(FindWristWatchActivity.this);
        sHandler.sendEmptyMessageDelayed(0, 30000);
    }


    //Add by lihaizhou begin on 2018.01.18
//    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
//    private void playAlertSoundAndWakeup() {
//        if (mMediaPlayer == null) {
//            requestAudioFocus();
//            mMediaPlayer = MediaPlayer.create(this, R.raw.find_wristwatch);
//            mMediaPlayer.setLooping(true);
//            mMediaPlayer.setVolume(1f, 1f);
//            mMediaPlayer.start();
//
//            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
////        boolean isWakeup = powerManager.isInteractive();
////        if (!isWakeup) {
//            wakeLock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "findwatch");
////            wakeLock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, "findwatch");
//            wakeLock.acquire();//可添加超时参数，这里保持常亮
////        }
//            sHandler = new StopHandler(FindWristWatchActivity.this);
//            sHandler.sendEmptyMessageDelayed(0, 30000);
//            Log.i(TAG, "playAlertSoundAndWakeup:------ lyly 30 miao kai shi le");
//        }
//    }

    private void initSound() {

        soundPool = new SoundPool(1, AudioManager.STREAM_ALARM, 0);
        sound = soundPool.load(this, R.raw.find_wristwatch, 1);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                playSound();
            }
        });
    }

    private void playSound() {
        requestAudioFocus();

        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxAlarmVolumn, 0);
        //播放
        soundPool.play(sound,     //声音资源
                maxAlarmVolumn,         //左声道
                maxAlarmVolumn,         //右声道
                1,             //优先级，0最低
                -1,         //循环次数，0是不循环，-1是永远循环
                1);            //回放速度，0.5-2.0之间。1为正常速度
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume: ");
        //当前音量
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxAlarmVolumn, 0);
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause: ");
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, currentAlarmVolumn, 0);
        super.onPause();
    }

    /**
     * 是否是免打扰模式
     *
     * @return
     */
    private boolean isDisturb() {
        String result = android.provider.Settings.System.getString(getApplicationContext().getContentResolver(), "SilenceList_result");
        boolean SilenceList_result = (result == null ? false : Boolean.parseBoolean(result));
        return SilenceList_result;
    }

    private boolean requestAudioFocus() {
        int result = audioManager.requestAudioFocus(afChangeListener,
                // Use the music stream.
                AudioManager.STREAM_MUSIC | AudioManager.STREAM_ALARM,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN);
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            Log.e("main2 focuschange", focusChange + "");
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                // Pause playback
                Log.i(TAG, "onAudioFocusChange: lyly AUDIOFOCUS_LOSS_TRANSIENT Pause");
                releaseSoundPool();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                Log.i(TAG, "onAudioFocusChange: lyly AUDIOFOCUS_LOSS");
                releaseSoundPool();
                // Stop playback
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                // Lower the volume
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // Resume playback or Raise it back to normal
                Log.i(TAG, "onAudioFocusChange: lyly AUDIOFOCUS_GAIN ");
                initSound();
            }
        }
    };

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive: lyly ");
            FindWristWatchActivity.this.finish();
        }
    };

    public static class StopHandler extends Handler {

        WeakReference<FindWristWatchActivity> weakReference;

        public StopHandler(FindWristWatchActivity activity) {
            //this.weakReference = new WeakReference<>(activity);//del by lihaizhou avoid compile error
            this.weakReference = new WeakReference(activity);//add by lihaizhou 20180706
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.i(TAG, "handleMessage: lyly 30 miao dao le activity === " + weakReference.get());
            weakReference.get().finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_POWER) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy: lyly 释放资源 sound stop");
        release();
        super.onDestroy();
    }

    @Override
    public void finish() {
        release();
        super.finish();
    }

    /**
     * 释放资源
     */
    private void release() {
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
        }
//        if (mMediaPlayer != null) {
//            mMediaPlayer.stop();
//            mMediaPlayer.reset();
//            mMediaPlayer.release();
//            mMediaPlayer = null;
//        }

        releaseSoundPool();

        if (wakeLock != null) {
            try {
                wakeLock.release();
            } catch (Exception e) {
                wakeLock = null;
            }
        }

        if (sHandler != null) {
            sHandler.removeCallbacksAndMessages(null);
        }

        if (audioManager != null) {
            audioManager.abandonAudioFocus(afChangeListener);
        }

    }

    private void releaseSoundPool() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }
}
