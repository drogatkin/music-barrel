package rogatkin.music_barrel.model;

import com.beegman.webbee.model.AppModel;
import com.beegman.webbee.util.SimpleCoordinator;

public class mb_setting extends SimpleCoordinator<MBModel> {
	public enum output_type {
		ANALOG, HDMI, SPDIF
	};

	public mb_setting(MBModel arg0) {
		super(arg0);
	}

}
