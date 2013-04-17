package rogatkin.music_barrel.ctrl;import java.nio.file.Files;import java.nio.file.Path;import java.nio.file.FileSystems;import java.util.Date;import mediautil.gen.MediaFormat;import mediautil.gen.MediaInfo;import photoorganizer.formats.MediaFormatFactory;import photoorganizer.formats.MP3;import rogatkin.music_barrel.model.PlaybackCtrl;import rogatkin.music_barrel.model.mb_media_item;import rogatkin.music_barrel.model.mb_play_list;import rogatkin.music_barrel.model.mb_play_list_map;import rogatkin.music_barrel.model.Name;import com.beegman.webbee.block.Gadget;import rogatkin.music_barrel.model.MBModel;import rogatkin.music_barrel.srv.PlayerService;import org.aldan3.data.DODelegator;import org.aldan3.data.DOService;public class Player extends Gadget<PlaybackCtrl, MBModel> implements Name {	protected PlaybackCtrl getGadgetData() {		PlayerService player = getAppModel().getPlayer();		int cmd = getParameterValue("cmd", -1, 0);		if (cmd >= 0) {			log("Command %d", null, cmd);			if (cmd == 0)				player.stop();			else if (cmd == 1)				player.resume();			else if (cmd == 2)				player.pause();			else if (cmd == 4)				player.stopAll();		} else {			try {				Path play = getItemPath();				if (play != null) {					log("Playing %s", null, play);					player.play(play);				} else {					int list_id = getParameterValue(V_PLAY_LIST, 0, 0);					if (list_id > 0)						player.playList(list_id, getParameterValue(V_PLAY_ITEM, 0, 0),								getParameterValue(V_PLAY_MODE, 0, 0));				}			} catch (Exception e) {				log("", e);			}		}		PlaybackCtrl result = new PlaybackCtrl(getAppModel());		result.status = player.getStatus();		return result;	}	public String processaddItemCall() {		MBModel am = getAppModel();		mb_media_item li = new mb_media_item(am);		fillModel(li);		try {			am.addPlayItem(li, getParameterValue("listName", "", 0));			return "Ok";		} catch (Exception e) {			log("", e);		}		return "error";	}	Path getItemPath() {		return MBModel.getItemPath(getParameterValue(V_PLAY_PATH, null, 0));	}}