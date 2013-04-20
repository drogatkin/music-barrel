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
	public int id;
	
	@DBField(size = 200)
	public String title;
	
	@DBField
	public int year;
	
	@DBField(size = 200)
	public String studio;
	
	@DBField
	public int num_subsets;
	
	@DBField
	public int subset_num;

}
