package rogatkin.music_barrel.srv;

import java.net.URL;
import java.nio.file.Path;
import org.aldan3.model.ServiceProvider;

import photoorganizer.formats.MediaFormatFactory;
import photoorganizer.media.MediaPlayer;
import photoorganizer.media.MediaPlayer.Status;
import rogatkin.music_barrel.model.MBModel;

public class PlayerService implements ServiceProvider<PlayerService> {
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
}
