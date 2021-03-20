package com.xxun.xunlauncher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/*
 * am start -n com.xxun.audioft/.AudioPlayActivity 
 * -e path "$1" --ei volume "$2" --ei output "$3"
 */
public class AudioCommandsReceiver extends BroadcastReceiver {

	final String TAG = "AudioCommandsReceiver";

	public static final int DEFAULT_VOL = 15;
	public static final String ACTION_AUDIO_PLAY = "android.action.xun.audio.play";
	public static final String ACTION_AUDIO_RECORD = "android.action.xun.audio.record";
	public static final String ACTION_AUDIO_PLAYRECORD = "android.action.xun.audio.playrecord";
	String mSn;
	public void onReceive(Context context, Intent data) {
//		am broadcast -a android.action.lct.audio.play -e path /sdcard/newalert.wav 
//		--ei volume 15 --ei output 1
		if (ACTION_AUDIO_PLAY.equals(data.getAction())) {
			String path = data.getStringExtra("path");
			int volume = data.getIntExtra("volume", DEFAULT_VOL);
			int device = data.getIntExtra("output", 1);
			Log.i(TAG, "play file " + path + " vol " + volume + " output " + device);
            Intent newIntent = new Intent(Intent.ACTION_MAIN);
            newIntent.setClassName("com.xxun.audioft", "com.xxun.audioft.AudioPlayActivity");
			newIntent.putExtra("path", path);
			newIntent.putExtra("volume", volume);
			newIntent.putExtra("output", device);
			newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(newIntent);
        }
//		am broadcast -a android.action.lct.audio.record -e path /sdcard/record.wav 
//		--ei mic 1 --ei samplerate 44100 --ei bit 16 --ei channel 1 --ei time 8 
		if (ACTION_AUDIO_RECORD.equals(data.getAction())) {
			String path = data.getStringExtra("path");
			int mic = data.getIntExtra("mic", 1);
			int samplerate = data.getIntExtra("samplerate", 44100);
			int bitrate = data.getIntExtra("bit", 16);
			int channel = data.getIntExtra("channel", 1);
			int len = data.getIntExtra("time", 0);
			Log.i(TAG, "record file " + path + " sample " + samplerate + " with mic " + mic);
            Intent newIntent = new Intent(Intent.ACTION_MAIN);
            newIntent.setClassName("com.xxun.audioft", "com.xxun.audioft.AudioRecordActivity");
			newIntent.putExtra("path", path);
			newIntent.putExtra("mic", mic);
			newIntent.putExtra("samplerate", samplerate);
			newIntent.putExtra("channel", channel);
			newIntent.putExtra("time", len);
			newIntent.putExtra("bit", bitrate);
			newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(newIntent);
        }

        if (ACTION_AUDIO_PLAYRECORD.equals(data.getAction()) ) {
            Intent newIntent = new Intent(Intent.ACTION_MAIN);
            newIntent.setClassName("com.xxun.xunlauncher", "com.xxun.xunlauncher.activity.AudioPlayRecordActivity");

            String music_path = data.getStringExtra("music_path");
            int volume = data.getIntExtra("volume", DEFAULT_VOL);
            int device = data.getIntExtra("output", 1);
            Log.i(TAG, "play file " + music_path + " vol " + volume + " output " + device);

            String path = data.getStringExtra("path");
            int mic = data.getIntExtra("mic", 1);
            int samplerate = data.getIntExtra("samplerate", 44100);
            int bitrate = data.getIntExtra("bit", 16);
            int channel = data.getIntExtra("channel", 1);
            int len = data.getIntExtra("time", 0);
            Log.i(TAG, "record file " + path + " sample " + samplerate + " with mic " + mic);

            newIntent.putExtra("music_path", music_path);
            newIntent.putExtra("volume", volume);
            newIntent.putExtra("output", device);
            // record
            newIntent.putExtra("path", path);
            newIntent.putExtra("mic", mic);
            newIntent.putExtra("samplerate", samplerate);
            newIntent.putExtra("channel", channel);
            newIntent.putExtra("time", len);
            newIntent.putExtra("bit", bitrate);

            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(newIntent);
        }

    }
}
