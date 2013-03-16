package rogatkin.music_barrel.model;

import org.aldan3.annot.DBField;
import org.aldan3.annot.DataRelation;
import org.aldan3.annot.FormField;

import com.beegman.webbee.model.AppModel;
import com.beegman.webbee.util.SimpleCoordinator;

@DataRelation
public class mb_media_item extends SimpleCoordinator<MBModel> {

	public mb_media_item(MBModel arg0) {
		super(arg0);
	}
	@DBField(key = true, auto = 1)
	public long id;
	
	@DBField
	public String title;
	
	@DBField
	public String performer;
	
	@DBField
	public int set_id;
	
	@DBField
	public int track;
	
	@DBField
	public int set;

	@DBField
	public int year;
	
	@DBField
	public String genre;
	
	@DBField
	public int length;
	
	@DBField
	public int sample_rate;
	
	@DBField
	public boolean losed;
	
	@DBField
	public long artwork_id;
	
	@DBField
	public String path;
	
	@DBField
	public int play_count;
}
