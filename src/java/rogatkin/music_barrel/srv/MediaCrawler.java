package rogatkin.music_barrel.srv;

import org.aldan3.model.ServiceProvider;

import com.beegman.webbee.util.Cron;

public class MediaCrawler  extends Cron implements ServiceProvider<MediaCrawler> {

	@Override
	public String getPreferredServiceName() {
		return "Media-Crawler";
	}

	@Override
	public MediaCrawler getServiceProvider() {
		return this;
	}

}
