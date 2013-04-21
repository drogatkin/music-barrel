package rogatkin.music_barrel.ctrl;

import org.aldan3.model.DataObject;
import rogatkin.music_barrel.model.MBModel;
import rogatkin.music_barrel.model.Name;

import com.beegman.webbee.block.SqlTabular;
import org.aldan3.annot.DataRelation;

@DataRelation(query = "select genre,i.year,track,i.title,performer,path,i.id,s.title album, subset_num from mb_media_item i left join mb_media_set s on i.set_id=s.id where s.title like ':filter_album%' and genre like ':filter_genre%' and i.title like ':filter_title%' and performer like ':filter_performer%'",
keys = { "filter_album", "filter_title", "filter_genre", "filter_performer" })
public class Library extends SqlTabular<DataObject, MBModel> implements Name {
	@Override
	protected boolean doPreserveKeys() {
		return true;
	}
}