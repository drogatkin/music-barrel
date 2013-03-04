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
		PlayerService ps = (PlayerService)unregister(PlayerService.NAME);
		ps.stop();
		super.deactivateServices();
	}

	@Override
	protected String getServletName() {
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

	public PlayerService getPlayer() {
		return (PlayerService)getService(PlayerService.NAME);
	}
}
