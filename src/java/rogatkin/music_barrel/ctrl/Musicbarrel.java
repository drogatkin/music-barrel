package rogatkin.music_barrel.ctrl;

import java.net.URLEncoder;
import java.nio.file.FileSystems;

import mediautil.gen.MediaFormat;
import mediautil.gen.MediaInfo;

import org.aldan3.model.DataObject;
import org.aldan3.util.DataConv;
import org.aldan3.util.Sql;

import photoorganizer.formats.MediaFormatFactory;

import rogatkin.music_barrel.model.MBModel;
import rogatkin.music_barrel.model.Name;
import rogatkin.music_barrel.model.mb_media_item;
import rogatkin.music_barrel.util.MusicPath;

import com.beegman.webbee.block.Grid;
import com.beegman.webbee.block.Grid.CellModelExample;
import com.beegman.webbee.model.Appearance;
import java.io.File;

public class Musicbarrel extends Grid<Musicbarrel.CellModel2, MBModel> {
	protected String[] playQueue;

	@Override
	protected int numRows() {
		int reminder = playQueue.length % numCols();
		return playQueue.length / numCols() + (reminder == 0 ? 0 : 1);
	}

	@Override
	protected int numCols() {
		return 3; // depends on mobile and orientation
	}

	@Override
	protected Object getModel() {
		MediaFormat cm = getAppModel().getPlayer().getCurrentMedia();
		if (cm == null)
			playQueue = getAppModel().getPlayer().getPlayQueue();
		else {
			String[] remainQueue = getAppModel().getPlayer().getPlayQueue();
			playQueue = new String[remainQueue.length + 1];
			System.arraycopy(remainQueue, 0, playQueue, 1, remainQueue.length);
			playQueue[0] = cm.getFile().getPath();
		}
		if (playQueue.length > 0) {

		}
		return super.getModel();
	}

	@Override
	protected CellModel2 getCellModel(int col, int row) {
		// log("requested %d,%d from %d", null, col, row, playQueue.length);
		CellModel2 result = new CellModel2();
		if (row * numCols() + col < playQueue.length) {
			try { // TODO read mb_media_item with possible inherited class with album field
				DataObject dob = getAppModel().getDOService().getObjectByQuery(String.format(
						"select i.title, performer, track, i.year, genre, s.title album, play_count, path from mb_media_item i left join mb_media_set s on i.set_id=s.id where path='%s'",
						/* do service get sql */Sql.escapeQuote(playQueue[row * numCols() + col])), null);
				if (dob != null) {
					result.title = "" + dob.get("TITLE");
					result.comment = String.format("%s - %s - %d", dob.get("PERFORMER"),
							DataConv.ifNull(dob.get("ALBUM"), ""), dob.get("YEAR"));
					result.content = String.format(
							"<div style=\"min-width:100%%\"><img src=\"Artwork?path=%s\" style=\"max-width: %2$dpx; max-height: %2$dpx;margin:auto;display:block\"></div>",
							URLEncoder.encode(new MusicPath("" + dob.get("PATH")).toString(), getCharSet()),
							appearance == Appearance.mobile ? 96 : 260);
					result.path = "" + dob.get("PATH");
				}
			} catch (Exception e) {
				log("Can't get a description from DB", e);
			}

			if (result.title == null) {
				try {
					/// log("No record found for %s %d", null, playQueue[row * numCols() + col],
					/// col);
					// not in library, generate record
					result.path = playQueue[row * numCols() + col];
					//System.out.printf("Play path %s%n", result.path);
					
					MediaFormat mf = MediaFormatFactory.createMediaFormat(getAppModel().fromWebPath(result.path), getCharSet(), true);
					if (mf != null) {
						mb_media_item i = new mb_media_item(getAppModel());
						MediaInfo mi;
						MBModel.fillMediaModel(i, mi = mf.getMediaInfo());
						result.title = i.title;
						result.comment = String.format("%s - %s - %d", i.performer, mi.getAttribute(MediaInfo.ALBUM),
								i.year);
						result.content = String.format(
								"<div style=\"min-width:100%%\"><img src=\"Artwork?path=%s\" style=\"max-width: %2$dpx; max-height: %2$dpx;margin:auto;display:block\"></div>",
								URLEncoder.encode(new MusicPath(result.path).toString(), getCharSet()),
								appearance == Appearance.mobile ? 96 : 260);
						try {mf.getAsStream().close();} catch(Exception e) {}
					}
				} catch (Exception e) {
					log("Can't get a description from the media", e);
				}
			}
			if (result.title == null || result.title.isEmpty()) {
				result.title = MusicPath.getJustName(FileSystems.getDefault().getPath(result.path));
			}
		}
		return result;
	}
	
	public String processversionCall() {
		resp.setContentType("text/plain");
		return "v1.6.04";
	}

	public static final class CellModel2 {
		public String path;
		public String content;
		public String comment;
		public String title;
	}
}
