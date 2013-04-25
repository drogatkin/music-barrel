package rogatkin.music_barrel.ctrl;

import java.net.URLEncoder;

import org.aldan3.model.DataObject;
import org.aldan3.util.Sql;

import rogatkin.music_barrel.model.MBModel;

import com.beegman.webbee.block.Grid;
import com.beegman.webbee.block.Grid.CellModelExample;

public class Musicbarrel extends Grid<CellModelExample, MBModel> {
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
	protected CellModelExample getCellModel(int col, int row) {
		//log("requested %d,%d from %d", null, col, row, playQueue.length);
		CellModelExample result = new CellModelExample();
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
					result.comment = String.format("%s - %s - %d", dob.get("PERFORMER"), dob.get("ALBUM"),
							dob.get("YEAR"));
					result.content = String.format("<div><img src=\"Artwork?path=%s\"></div>",
							URLEncoder.encode("" + dob.get("PATH"), getCharSet()));
				}
			} catch (Exception e) {
				log("", e);
			}
		return result;
	}
}
