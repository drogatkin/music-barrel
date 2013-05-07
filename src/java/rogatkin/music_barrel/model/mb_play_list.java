package rogatkin.music_barrel.model;

import java.util.Date;
import com.beegman.webbee.util.SimpleCoordinator;
import org.aldan3.annot.DBField;
import org.aldan3.annot.DataRelation;
import org.aldan3.annot.FormField;

@DataRelation
public class mb_play_list extends SimpleCoordinator<MBModel> {
	public mb_play_list(MBModel model) {
		super(model);
	}
	@DBField(key = true, auto = 1)
	public int id;
	
	@DBField(size = 200, unique=true)
	public String title;

	@DBField
	public int play_mode;
	
	@DBField
	public int start_pos;
	
	@DBField
	public int play_duration;
	
	@DBField
	public int play_count;
	
	@DBField
	public Date modified_on;
	
	@DBField(auto = -1)
	public Date created_on;
}
