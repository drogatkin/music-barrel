package rogatkin.music_barrel.ctrl;

import java.io.IOException;
import rogatkin.music_barrel.model.MBModel;
import rogatkin.music_barrel.model.mb_setting;
import com.beegman.webbee.block.Form;

public class Wrench extends Form<mb_setting, MBModel> {
	
	protected mb_setting getFormModel() {
		return getAppModel().getSettings();
	}

	@Override
	protected Object storeModel(mb_setting model) {
		navigation = "Navigator";
		try {
			if (model.rescan_soon) {
				getAppModel().getSettings().last_scan = null;
			}
			getAppModel().saveSettings();
			if (model.output_type != null)
				setOutType(model.output_type);
			else
				return getResourceString("error_no_audio", "Audio output type is required");
			return "";
		} catch (Exception e) {
			log("", e);
			return "" + e;
		}

	}

	@Override
	protected boolean needFillBeforeLoad() {
		return false;
	}

	@Override
	protected mb_setting loadModel(mb_setting model) {
		return model;
	}

	void setOutType(mb_setting.output_type type) throws IOException {
		log("Setting out %s", null, type.toString());
		Process p = null;
		switch (type) {
		case AUTO:
			// p = Runtime.getRuntime().exec("amixer cset numid=3 " + type);
			break;
		case ANALOG:
			// p = Runtime.getRuntime().exec("amixer cset numid=3 " + type);
			break;
		case HDMI:
			// p = Runtime.getRuntime().exec("amixer cset numid=3 " + type);
			break;
		case USB:
		case SPDIF:
			// p = Runtime.getRuntime().exec("amixer cset numid=3 " + type);
			break;
		}
		 
		if (p != null)
			try {
				p.waitFor();
			} catch (Exception e) {
				log("Error: %s", e, e.toString());
			}
	}
}
