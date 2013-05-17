package rogatkin.music_barrel.model;

import com.beegman.webbee.util.SimpleCoordinator;
import photoorganizer.media.MediaPlayer.Status;

public class PlaybackCtrl extends SimpleCoordinator<MBModel> {
	public Status status;
	
	public long length;
	
	public long position;
	
	public PlaybackCtrl(MBModel m) {
		super(m);
	}
	
	@Override
	public String toString() {
		return String.valueOf(status)+" "+length+" - "+position;
	}
}
