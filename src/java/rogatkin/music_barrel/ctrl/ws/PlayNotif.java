package rogatkin.music_barrel.ctrl.ws;

import com.beegman.buzzbee.web.NotifEndPoint;

import rogatkin.music_barrel.model.MBModel;

import com.beegman.buzzbee.LogImpl;
import com.beegman.buzzbee.WebEvent;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/notif/web/", encoders = NotifEndPoint.WebEventEncoder.class)
public class PlayNotif extends NotifEndPoint {
    @OnOpen
  	public void fromClient( Session s) {
  	    ses = s;
  	    try {
              MBModel.notifService.subscribe("updatePlayList", this);
  	    } catch (Exception e) {
  	        LogImpl.log.error(e, "error");
    	}
  	}
  	
  	@Override
    public void notify(WebEvent event) {
    	if (ses != null)
    		try {
    			ses.getBasicRemote().sendObject(event);
    		} catch (Exception e) {
    		    LogImpl.log.error(e, "error");
    		}
    }

}
