package br.ufc.web2mobile;

import java.io.IOException;

import android.media.MediaPlayer;

public class AudioPlayer {

	private MediaPlayer mp = new MediaPlayer();

	public AudioPlayer()
	{		
	}

	public void start(String audioPath) {
		try {
			mp.setDataSource(audioPath);
			mp.prepare();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		mp.start();
	}

	public void pause() {
		mp.pause();
	}

	public void stop() {
		mp.stop();
		mp.reset();
	}
	
	public void mute(){
		mp.setVolume(0, 0);
	}
	
	public void unmute(){
		mp.setVolume(1, 1);
	}
}
