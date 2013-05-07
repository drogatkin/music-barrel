package rogatkin.music_barrel.ctrl;

import java.net.URLEncoder;

import org.aldan3.model.DataObject;
import org.aldan3.util.DataConv;
import org.aldan3.util.Sql;

import rogatkin.music_barrel.model.MBModel;

import com.beegman.webbee.block.Grid;
import com.beegman.webbee.block.Grid.CellModelExample;
import com.beegman.webbee.model.Appearance;

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
		playQueue = getAppModel().getPlayer().getPlayQueue();
		if (playQueue.length > 0) {

		}
		return super.getModel();
	}

	@Override
	protected CellModel2 getCellModel(int col, int row) {
		//log("requested %d,%d from %d", null, col, row, playQueue.length);
		CellModel2 result = new CellModel2() ;
		if (row * numCols() + col < playQueue.length)
			try {
				DataObject dob = getAppModel()
						.getDOService()
						.getObjectByQuery(
								String.format(
										"select i.title, performer, track, i.year, genre, s.title album, play_count, path from mb_media_item i left join mb_media_set s on i.set_id=s.id where path='%s'",
										/*do service get sql*/Sql.escapeQuote(playQueue[row * numCols() + col])), null);
				if (dob != null) {

					result.title = "" + dob.get("TITLE");
					result.comment = String.format("%s - %s - %d", dob.get("PERFORMER"), DataConv.ifNull(dob.get("ALBUM"), ""),
							dob.get("YEAR"));
					result.content = String.format("<div style=\"min-width:100%%\"><img src=\"Artwork?path=%s\" style=\"max-width: %2$dpx; max-height: %2$dpx;margin:auto;display:block\"></div>",
							URLEncoder.encode("" + dob.get("PATH"), getCharSet()), appearance == Appearance.mobile?96:260);
					result.path = "" + dob.get("PATH");
				}
			} catch (Exception e) {
				log("", e);
			}
		return result;
	}
	
	@Override
	protected String getTitle() {
		// TODO consider using String format
		return super.getTitle() + " - " + DataConv.ifNull(getAppModel().getPlayer().getCurrentMedia(), getResourceString("idle", "Idle"));
	}
	
	public static final class CellModel2 {
		public String path;
		public String content;
		public String comment;
		public String title;
	}
}
