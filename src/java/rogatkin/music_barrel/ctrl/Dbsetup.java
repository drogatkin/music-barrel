package rogatkin.music_barrel.ctrl;

import rogatkin.music_barrel.model.MBModel;
import rogatkin.music_barrel.model.mb_setting;

import com.beegman.webbee.block.Systemsetup;
import com.beegman.webbee.model.Setup;

public class Dbsetup extends Systemsetup<Setup, MBModel> {

	protected String getDefaultModelPackage() {
		return mb_setting.class.getPackage().getName();
	}
	
	@Override
	protected Object storeModel(Setup setup) {
		setup.model_package_name = getDefaultModelPackage();
		setup.discardExisting = true; // TODO take it as force parameter
		return super.storeModel(setup);
	}
}
