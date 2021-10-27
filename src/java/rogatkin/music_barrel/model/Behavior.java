package rogatkin.music_barrel.model;

import com.beegman.webbee.base.BaseBehavior;
import com.beegman.webbee.base.BaseBlock;
import mediautil.gen.MediaFormat;
import mediautil.gen.MediaInfo;
import org.aldan3.util.DataConv;

public class Behavior extends BaseBehavior<MBModel> implements Name {
	public Behavior() {
		isPublic = true;
		useLabels = true;
		useBreadCrumbs = false;
		ignoreSession = true;
	}
	
	@Override
	public String getTitle(BaseBlock<MBModel> baseBlock) {
		MBModel appModel = baseBlock.getAppModel();
		MediaFormat mf = appModel.getPlayer().getCurrentMedia();
		if (mf != null) {
			// assert mf.getMediaInfo() != null
			// TODO make it prorated to the current player position
			baseBlock.modelInsert(VV_SONGLENGTH, mf.getMediaInfo().getIntAttribute(MediaInfo.LENGTH));
			baseBlock.modelInsert(VV_PLAYPOSITION, appModel.getPlayer().getPlaybackPosition());
		} else {
			baseBlock.modelInsert(VV_SONGLENGTH, 0);
			baseBlock.modelInsert(VV_PLAYPOSITION, 0);
		}
		return DataConv.ifNull(mf, "") + " " + super.getTitle(baseBlock);
	}
}
