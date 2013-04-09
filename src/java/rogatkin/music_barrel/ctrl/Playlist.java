package rogatkin.music_barrel.ctrl;

import org.aldan3.annot.DataRelation;
import org.aldan3.model.DataObject;

import rogatkin.music_barrel.model.MBModel;

import com.beegman.webbee.block.SqlTabular;
@DataRelation(query = "")
public class Playlist extends SqlTabular<DataObject, MBModel> {

}
