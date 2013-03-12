package rogatkin.music_barrel.model;

import com.beegman.webbee.model.AppModel;
import com.beegman.webbee.util.SimpleCoordinator;

public class mb_media_item extends SimpleCoordinator<MBModel> {

	public mb_media_item(MBModel arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}
	
	public long id;
	
	public String title;
	
	public String performer;
	
	public int set_id;
	
	public int track;
	
	public int set;

	public int year;
	
	public String genre;
	
	public int length;
	
	public int sample_rate;
	
	public boolean losed;
	
	public long artwork_id;
	
	public String path;
	
	public int play_count;
}
