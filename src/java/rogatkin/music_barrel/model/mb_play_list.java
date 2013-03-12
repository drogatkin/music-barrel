package rogatkin.music_barrel.model;

import java.util.Date;
import com.beegman.webbee.util.SimpleCoordinator;


public class mb_play_list extends SimpleCoordinator<MBModel> {
	public mb_play_list(MBModel model) {
		super(model);
	}

	public int id;
	
	public String title;
	
	public long item_id;
	
	public int play_mode;
	
	public int start_pos;
	
	public int play_duration;
	
	public int play_count;
	
	public Date modified_on;
	
	public Date created_on;
}
