package rogatkin.music_barrel.model;

import com.beegman.webbee.util.SimpleCoordinator;
import org.aldan3.annot.DBField;
import org.aldan3.annot.DataRelation;
import org.aldan3.annot.FormField;

@DataRelation
public class mb_media_set extends SimpleCoordinator<MBModel> {
	public mb_media_set(MBModel model) {
		super(model);
	}
	
	@DBField(key = true, auto = 1)
	int id;
	
	@DBField(size = 200)
	String title;
	
	@DBField
	int year;
	
	@DBField(size = 200)
	String studio;
	
	@DBField
	int num_subsets;
	
	@DBField
	int subset_num;
	
	@DBField
	public long item_id;

}
