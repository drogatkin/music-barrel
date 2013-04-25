package rogatkin.music_barrel.model;

import java.util.Date;
import org.aldan3.annot.DBField;
import org.aldan3.annot.DataRelation;
import com.beegman.webbee.util.SimpleCoordinator;

@DataRelation
public class mb_play_list_map extends SimpleCoordinator<MBModel> {
	public mb_play_list_map(MBModel model) {
		super(model);
	}

	// TODO provide functionality for the field
	public long relation_id;

	@DBField(index = true)
	public int list_id;
	@DBField
	public long item_id;

	@DBField(auto = -1)
	public Date related_on;

}
