package rogatkin.music_barrel.ctrl;

import rogatkin.music_barrel.model.MBModel;
import rogatkin.music_barrel.model.mb_setting;

import com.beegman.webbee.block.Systemsetup;
import com.beegman.webbee.model.Setup;

public class Dbsetup extends Systemsetup<Dbsetup.MBDB, MBModel> {

	static class MBDB extends Setup {
		public MBDB() {
			model_package_name = mb_setting.class.getPackage().getName();
		}
	}
}
