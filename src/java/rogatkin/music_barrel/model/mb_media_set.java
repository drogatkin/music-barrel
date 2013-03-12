package rogatkin.music_barrel.model;

import com.beegman.webbee.util.SimpleCoordinator;

public class mb_media_set extends SimpleCoordinator<MBModel> {
	public mb_media_set(MBModel model) {
		super(model);
	}
	
	int id;
	
	String title;
	
	int year;
	
	String studio;
	
	int num_subsets;
	
	int subset_num;
	
	public long item_id;

}
