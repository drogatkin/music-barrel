package rogatkin.music_barrel.model;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

import org.aldan3.data.DODelegator;
import org.aldan3.data.DOService;

import photoorganizer.formats.MP3;
import photoorganizer.formats.MediaFormatFactory;

import mediautil.gen.MediaFormat;
import mediautil.gen.MediaInfo;

import rogatkin.music_barrel.srv.PlayerService;

import com.beegman.webbee.model.AppModel;

public class MBModel extends AppModel {

	@Override
	public String getAppName() {
		return "music-barrel";
	}

	public String getCharEncoding() {
		return "UTF-8";
	}

	@Override
	protected void deactivateServices() {
		PlayerService ps = (PlayerService) unregister(PlayerService.NAME);
		ps.stopAll();
		super.deactivateServices();
	}

	@Override
	protected String getServletName() {
		return super.getServletName();
	}

	@Override
	protected void initServices() {

		super.initServices();
		register(new PlayerService(this));
	}

	@Override
	public Behavior getCommonBehavior() {
		return new Behavior();
	}

	public PlayerService getPlayer() {
		return (PlayerService) getService(PlayerService.NAME);
	}

	public void addPlayItem(mb_media_item item, String listName) throws MBError {
		MediaFormat mf = MediaFormatFactory.createMediaFormat(getItemPath(item.path).toFile(), getCharEncoding());
		if (mf == null || mf.isValid() == false)
			throw new MBError("Ivalid format :" + item);
		mb_play_list pl = new mb_play_list(this);
		if (listName == null || listName.isEmpty())
			pl.title = "On The Go";
		else
			pl.title = listName;
		DOService dos = getDOService();
		try {
			dos.getObjectLike(new DODelegator(pl, null, "", "title") {
				@Override
				protected String normilizeFieldName(String fieldName) {
					return fieldName.toUpperCase();
				}
			});
			if (pl.id <= 0) {
				pl.created_on = new Date();
				dos.addObject(new DODelegator(pl, null, "", "id"), "id");
			}
			dos.getObjectLike(new DODelegator(item, null, "", "path") {
				@Override
				protected String normilizeFieldName(String fieldName) {
					return fieldName.toUpperCase();
				}
			});
			//log("Object %s for path %s", null, oo, li.path);
			if (item.id <= 0) {
				fillMediaModel(item, mf.getMediaInfo());
				dos.addObject(new DODelegator(item, null, "", "id"), "id");
			}
			mb_play_list_map plm = new mb_play_list_map(this);
			plm.item_id = item.id;
			plm.list_id = pl.id;
			plm.related_on = new Date();
			dos.addObject(new DODelegator(plm));
		} catch (Exception e) {
			throw new MBError("Add item error", e);
		}
	}

	public static void fillMediaModel(mb_media_item mi, MediaInfo info) {
		mi.title = (String) info.getAttribute(MediaInfo.TITLE);
		mi.performer = (String) info.getAttribute(MediaInfo.ARTIST);
		mi.track = intValue(info.getAttribute(MediaInfo.TRACK));
		mi.year = intValue(info.getAttribute(MediaInfo.YEAR));
		mi.genre = MP3.findGenre(info);
	}

	public static Path getItemPath(String path) {
		Path result = FileSystems.getDefault().getPath(path);
		if (Files.isRegularFile(result) && Files.isReadable(result))
			return result;
		return null;
	}
	
	public static int intValue(Object o) {
		if (o instanceof Number)
			return ((Number) o).intValue();
		else if (o instanceof String)
			return Integer.valueOf((String) o).intValue();
		return 0;
	}
}
