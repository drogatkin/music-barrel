package rogatkin.music_barrel.ctrl;

import org.aldan3.annot.DataRelation;
import org.aldan3.model.DataObject;
import org.aldan3.data.DODelegator;

import rogatkin.music_barrel.model.MBModel;
import rogatkin.music_barrel.model.Name;
import rogatkin.music_barrel.model.mb_play_list;
import rogatkin.music_barrel.model.mb_play_list_map;

import com.beegman.webbee.block.SqlTabular;

@DataRelation(query = "select i.title, i.performer, track, year, l.title album, genre, path, i.id id, l.id list_id from mb_media_item i join mb_play_list_map m on m.item_id=i.id join mb_play_list l on l.id = m.list_id where l.title=':list_name'", keys = { "list_name" })
public class Playlist extends SqlTabular<DataObject, MBModel> implements Name {
	@Override
	protected String getTitle() {
		return getParameterValue("list_name", "Unknown", 0);
	}

	/////////////// Ajax handlers   ///////////////
	public String processdeleteItemCall() {
		mb_play_list_map conn = new mb_play_list_map(getAppModel());
		conn.list_id = getParameterValue(V_PLAY_LIST, 0, 0);
		if (conn.list_id > 0) {
			conn.item_id = getParameterValue(V_PLAY_ITEM, 0, 0);
			if (conn.item_id > 0)
				try {
					getAppModel().getDOService().deleteObjectLike(new DODelegator(conn, null, "", "list_id,item_id"));
					return OK;
				} catch (Exception e) {
					log("", e);
				}
		}
		return ERROR;
	}

	public String processdeleteListCall() {
		mb_play_list_map conn = new mb_play_list_map(getAppModel());
		conn.list_id = getParameterValue(V_PLAY_LIST, 0, 0);
		if (conn.list_id > 0) {
			try {
				getAppModel().getDOService().deleteObjectLike(new DODelegator(conn, null, "", "list_id"));
				mb_play_list pl = new mb_play_list(getAppModel());
				pl.id = conn.list_id;
				getAppModel().getDOService().deleteObjectLike(new DODelegator(pl, null, "", "id"));
				return OK;
			} catch (Exception e) {
				log("", e);
			}
		}
		return ERROR;
	}
	
}
