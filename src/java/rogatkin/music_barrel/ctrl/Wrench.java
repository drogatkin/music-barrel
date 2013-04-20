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
			if (model.rescan_soon){
				getAppModel().getSettings().last_scan = null;
			}
			getAppModel().saveSettings();
			if (model.output_type != null)
				switch (model.output_type) {
				case ANALOG:
					setOutType(1);
					break;
				case HDMI:
					setOutType(2);
					break;
				case AUTO:
					setOutType(0);
					break;
				}
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
	protected void loadModel(mb_setting model) {

	}

	void setOutType(int type) throws IOException {
		log("Setting out %d", null, type);
		// TODO check if not root, then add sudo
		Process p = Runtime.getRuntime().exec("amixer cset numid=3 " + type);
		try {
			p.waitFor();
		} catch (Exception e) {

		}
	}
}
