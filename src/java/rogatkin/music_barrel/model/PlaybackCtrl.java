package rogatkin.music_barrel.model;

import com.beegman.webbee.util.SimpleCoordinator;
import photoorganizer.media.MediaPlayer.Status;

public class PlaybackCtrl extends SimpleCoordinator<MBModel> {
	public Status status;
	
	public PlaybackCtrl(MBModel m) {
		super(m);
	}
}
