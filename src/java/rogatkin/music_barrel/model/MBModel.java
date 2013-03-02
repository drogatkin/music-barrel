package rogatkin.music_barrel.model;

import rogatkin.music_barrel.srv.PlayerService;

import com.beegman.webbee.model.AppModel;

public class MBModel extends AppModel {
	
	@Override
	public String getAppName() {
		return "music-barrel";
	}

	public String getCharEncoding() {
		return "UTF-8";
	}

	@Override
	protected void deactivateServices() {
		// TODO Auto-generated method stub
		super.deactivateServices();
	}

	@Override
	protected String getServletName() {
		// TODO Auto-generated method stub
		return super.getServletName();
	}

	@Override
	protected void initServices() {
		
		super.initServices();
		register(new PlayerService(this));
	}
	
	@Override
	public Behavior getCommonBehavior() {
		return new Behavior ();
	}

}
