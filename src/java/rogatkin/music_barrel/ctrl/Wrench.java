package rogatkin.music_barrel.ctrl;

import rogatkin.music_barrel.model.MBModel;
import rogatkin.music_barrel.model.mb_setting;

import com.beegman.webbee.block.Form;

public class Wrench extends Form<mb_setting,MBModel> {
	protected mb_setting getFormModel() {
		return new mb_setting(getAppModel());
	}
}
