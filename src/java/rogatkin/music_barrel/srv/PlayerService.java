package rogatkin.music_barrel.srv;

import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

import org.aldan3.model.DataObject;
import org.aldan3.model.ServiceProvider;

import com.beegman.webbee.util.AsyncUpdater;
import com.beegman.webbee.model.UIEvent;

import photoorganizer.formats.MediaFormatFactory;
import photoorganizer.media.MediaPlayer;
import photoorganizer.media.MediaPlayer.ProgressListener;
import photoorganizer.media.MediaPlayer.Status;
import rogatkin.music_barrel.model.MBModel;

public class PlayerService implements ServiceProvider<PlayerService>, ProgressListener, Runnable {
	public static final String NAME = "MediaPlayer";

	public static String GET_LIST_Q = "select i.title, i.performer, track, year, l.title album, genre, path, i.id id, l.id list_id from mb_media_item i join mb_play_list_map m on m.item_id=i.id join mb_play_list l on l.id = m.list_id ";

	MBModel appModel;
	private MediaPlayer mediaPlayer;
	Thread listPlayer;
	LinkedBlockingQueue<String> playQueue;

	public PlayerService(MBModel am) {
		appModel = am;
		playQueue = new LinkedBlockingQueue<>(1024);
		listPlayer = new Thread(this, NAME);
		listPlayer.setDaemon(true);
		listPlayer.start();
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
		mediaPlayer = MediaFormatFactory.getPlayer(MediaFormatFactory.createMediaFormat(media.toFile(),
				appModel.getCharEncoding()));
		//System.err.printf("Player %s for %s%n", mediaPlayer, media);
		mediaPlayer.setProgressListener(this);
		mediaPlayer.start();
		return getServiceProvider();
	}

	public PlayerService playList(int list_id, long start_item, int mode) {
		try {
			Collection<DataObject> list = appModel.getDOService().getObjectsByQuery(
					GET_LIST_Q + " where l.id=" + list_id, 0, -1);
			LinkedList<String> playList = new LinkedList<>();
			boolean add = start_item <= 0;
			for (DataObject dob : list) {
				if (add == false) {
					int iid = (int) dob.get("ID");
					add = start_item == iid;
				}
				if (add)
					playQueue.add(""+ dob.get("PATH"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	public void stopAll() {
		playQueue.clear();
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
			return mediaPlayer.getStatus();
		return Status.closed;
	}

	//////// progress listener interface  ////

	@Override
	public void finished() {
		AsyncUpdater updater = (AsyncUpdater) appModel.getService(AsyncUpdater.NAME);
		UIEvent uie = new UIEvent();
		uie.eventHandler = "songFinished";
		uie.parameters = new Object[] { getStatus() };
		updater.addEvent(appModel.getAppName(), uie);
		//System.err.printf("event %s dropped for %s%n", uie, mediaPlayer);
	}

	@Override
	public void setMaximum(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setValue(int arg0) {
		// TODO Auto-generated method stub

	}

	Path getNext() throws InterruptedException {
		return FileSystems.getDefault().getPath(playQueue.take());
	}

	@Override
	public void run() {
		do {
			try {
				play(getNext());
			} catch (InterruptedException ie) {
				break;
			}
			if (mediaPlayer != null)
				mediaPlayer.waitPlayEnds();
		} while (true);
	}
}
