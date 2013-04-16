package rogatkin.music_barrel.ctrl;

import org.aldan3.annot.DataRelation;
import org.aldan3.model.DataObject;

import rogatkin.music_barrel.model.MBModel;

import com.beegman.webbee.block.SqlTabular;

@DataRelation(query = "select i.title, i.performer, track, year, l.title album, genre, path, i.id id, l.id list_id from mb_media_item i join mb_play_list_map m on m.item_id=i.id join mb_play_list l on l.id = m.list_id where l.title=':list_name'", keys = { "list_name" })
public class Playlist extends SqlTabular<DataObject, MBModel> {
	@Override
    protected String getTitle() {
    	return getParameterValue("list_name", "Unknown", 0);
    }
}
