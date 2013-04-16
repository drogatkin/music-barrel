package rogatkin.music_barrel.ctrl;

import java.util.Collection;

import org.aldan3.model.DataObject;

import com.beegman.webbee.block.Gadget;
import rogatkin.music_barrel.model.MBModel;

public class Listsdirectory extends Gadget<Collection<DataObject>, MBModel> {
	@Override
	protected Collection<DataObject> getGadgetData() {
		try {
			return getAppModel().getDOService().getObjectsByQuery("select id, title, play_mode, start_pos from mb_play_list", 0, -1, null);
		} catch (Exception e) {
			log("", e);
		}
		return null;
	}
}
