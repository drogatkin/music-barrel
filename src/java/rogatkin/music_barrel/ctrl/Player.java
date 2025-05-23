package rogatkin.music_barrel.ctrl;

import java.nio.file.Files;

import java.nio.file.Path;
import java.util.Date;

import mediautil.gen.MediaFormat;
import mediautil.gen.MediaInfo;

import photoorganizer.formats.MP3;
import photoorganizer.media.MediaPlayer.Status;
import rogatkin.music_barrel.model.PlaybackCtrl;
import rogatkin.music_barrel.model.mb_media_item;
import rogatkin.music_barrel.model.mb_play_list;
import rogatkin.music_barrel.model.mb_play_list_map;
import rogatkin.music_barrel.model.Name;

import com.beegman.webbee.block.Gadget;
import rogatkin.music_barrel.model.MBModel;
import rogatkin.music_barrel.srv.PlayerService;
import org.aldan3.data.DODelegator;
import org.aldan3.data.DOService;

public class Player extends Gadget<PlaybackCtrl, MBModel> implements Name {

	protected PlaybackCtrl getGadgetData() {
		PlayerService player = getAppModel().getPlayer();
		PlaybackCtrl result = new PlaybackCtrl(getAppModel());
		int cmd = getParameterValue("cmd", -1, 0);
		if (cmd >= 0) {
			//log("Command %d", null, cmd);
			// TODO why 3 is missed
			if (cmd == 0)
				player.stop();
			else if (cmd == 1)
				player.resume();
			else if (cmd == 2)
				player.pause();
			else if (cmd == 4)
				player.stopAll();
		} else {
			try {
				Path play = getItemPath();
				//log("Playing %s", null, play);
				if (play != null) {
					player.play(play);
				} else {
					int list_id = getParameterValue(V_PLAY_LIST, 0, 0);
					if (list_id > 0)
						player.playList(list_id, getParameterValue(V_PLAY_ITEM, 0, 0),
								getParameterValue(V_PLAY_MODE, 0, 0));
					else {
						result.status = Status.inerror;
						return result;
					}
				}
			} catch (Exception e) {
				log("playback:error", e);
			}
		}
		result.status = player.getStatus();
		MediaFormat mf = player.getCurrentMedia();
		if (mf != null)
			result.length = mf.getMediaInfo().getIntAttribute(MediaInfo.LENGTH);
		result.position = player.getPlaybackPosition();
		return result;
	}

	//////////////////////// Ajax handlers  ////////////////////////////
	public String processaddItemCall() {
		MBModel am = getAppModel();
		mb_media_item li = new mb_media_item(am);
		boolean result = false;
		for (int i = 0;; i++) {
			fillModel(li, i);
			if (li.path == null || li.path.isEmpty())
				break;
			try {
				am.addToPlayList(li, getParameterValue("listName", "", 0));
				result = true;
			} catch (Exception e) {
				log("", e);
			}
		}
		return result ? OK : ERROR;
	}

	public String processaddPlayCall() {
		String sp;
		for (int i = 0; (sp = getParameterValue(V_PLAY_PATH, null, i)) != null; i++) {
			getAppModel().getPlayer().playItem(sp, getParameterValue(V_PLAY_ITEM, -1l, 0));
		}
		return "Ok";
	}
	
	public String processremovePlayCall() {
		getAppModel().getPlayer().removePlay(getParameterValue(V_PLAY_PATH, null, 0));
		return "Ok";
	}

	Path getItemPath() {
		return getItemPath(0);
	}

	Path getItemPath(int ix) {
		return MBModel.getItemPath(getParameterValue(V_PLAY_PATH, null, ix));
	}
}
