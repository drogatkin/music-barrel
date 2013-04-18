package rogatkin.music_barrel.ctrl;

import org.aldan3.model.DataObject;
import rogatkin.music_barrel.model.MBModel;
import rogatkin.music_barrel.model.Name;

import com.beegman.webbee.block.SqlTabular;
import org.aldan3.annot.DataRelation;

@DataRelation(query = "select genre,year,track,title,performer,path,id from mb_media_item where genre like ':filter_genre%' or title like ':filter_title%' or performer like ':filter_performer%'",
keys = { "filter_title", "filter_genre", "filter_performer" })
public class Library extends SqlTabular<DataObject, MBModel> implements Name {

}
