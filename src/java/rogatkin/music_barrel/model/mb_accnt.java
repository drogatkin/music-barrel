package rogatkin.music_barrel.model;
import com.beegman.webbee.util.SimpleCoordinator;

import java.util.Date;

import org.aldan3.annot.DBField;
import org.aldan3.annot.DataRelation;
import org.aldan3.annot.FormField;

@DataRelation
public class mb_accnt extends SimpleCoordinator<MBModel> {
	public mb_accnt(MBModel model) {
		super(model);
	}
	
	@DBField(key = true, auto = 1)
	public int id;
	
	@DBField(size = 200, unique=true, index=true)
	public String share_path;
	
	@FormField
	@DBField(size = 64, unique=false, index=true)
	public String name;
	
	@FormField
	@DBField(size = 80)
	public String password;
	
	@DBField
	public Date modified_on;
	
	@DBField(auto = -1)
	public Date created_on;
	
	public boolean matches(mb_accnt other) {
		return name.equals(other.name)&&  share_path.equals(other.share_path);
	}

	public String toString() {
		return "user:" + name + ", pass : " + (password == null ? "none": password.charAt(0) + "***") +
				", share: " + share_path;
	}
}
