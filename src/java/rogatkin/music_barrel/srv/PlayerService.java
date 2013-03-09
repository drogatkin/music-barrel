package rogatkin.music_barrel.srv;

import java.net.URL;
import java.nio.file.Path;
import org.aldan3.model.ServiceProvider;

import com.beegman.webbee.util.AsyncUpdater;
import com.beegman.webbee.model.UIEvent;

import photoorganizer.formats.MediaFormatFactory;
import photoorganizer.media.MediaPlayer;
import photoorganizer.media.MediaPlayer.ProgressListener;
import photoorganizer.media.MediaPlayer.Status;
import rogatkin.music_barrel.model.MBModel;

public class PlayerService implements ServiceProvider<PlayerService>, ProgressListener {
	public static final String NAME = "MediaPlayer";
	
	MBModel appModel;
	private MediaPlayer mediaPlayer;

	public PlayerService(MBModel am) {
		appModel = am;
	}
	
	@Override
	public String getPreferredServiceName() {
		return NAME;
	}

	@Override
	public PlayerService getServiceProvider() {
		return this;
	}

	public synchronized PlayerService play(URL media) {
		// do head call to get content type
		// resolve player
		return getServiceProvider();
	}

	public synchronized PlayerService play(Path media) {
		if (mediaPlayer != null) {
			mediaPlayer.close();

		}
		// TODO resolve encoding from config
		mediaPlayer = MediaFormatFactory.getPlayer(MediaFormatFactory.createMediaFormat(media.toFile(), appModel.getCharEncoding()));
		//System.err.printf("Player %s for %s%n", mediaPlayer, media);
		mediaPlayer.setProgressListener(this);
		mediaPlayer.start();
		return getServiceProvider();
	}
	
	public void pause() {
		if (mediaPlayer != null)
			mediaPlayer.pause();
	}
	
	public void stop() {
		if (mediaPlayer != null)
			mediaPlayer.close();
	}
	
	public void resume() {
		//System.err.printf("Player %s resume %n", mediaPlayer);
		if (mediaPlayer != null)
			mediaPlayer.resume();
	}
	
	public Status getStatus() {
		if (mediaPlayer != null)
			mediaPlayer.getStatus();
		return null;
	}
	
	//////// progress listener interface  ////

	@Override
	public void finished() {
		AsyncUpdater updater = (AsyncUpdater) appModel.getService(AsyncUpdater.NAME);
		UIEvent uie = new UIEvent();
		uie.eventHandler = "songFinished";
		uie.parameters = new Object[0];
		updater.addEvent(appModel.getAppName(), uie);
	}

	@Override
	public void setMaximum(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setValue(int arg0) {
		// TODO Auto-generated method stub
		
	}
}
