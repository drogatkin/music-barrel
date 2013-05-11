package rogatkin.music_barrel.model;

import com.beegman.webbee.base.BaseBehavior;
import com.beegman.webbee.base.BaseBlock;

public class Behavior extends BaseBehavior {
	public Behavior() {
		isPublic = true;
		useLabels = true;
		useBreadCrumbs = false;
		ignoreSession = true;
	}
	
	@Override
	public String getTitle(BaseBlock baseBlock) {
		return super.getTitle(baseBlock);
	}
}
