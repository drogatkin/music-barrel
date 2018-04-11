package rogatkin.music_barrel.ctrl;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.nio.file.DirectoryStream;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import photoorganizer.formats.MediaFormatFactory;
import photoorganizer.formats.MP3;

import mediautil.gen.MediaFormat;
import mediautil.gen.MediaInfo;

import rogatkin.music_barrel.model.MBModel;

import com.beegman.webbee.block.Tabular;
import java.util.Comparator;

public class Navigator extends Tabular<List<MediaInfo>, MBModel> {

	@Override
	protected List<MediaInfo> getTabularData(long pos, int size) {
		List<MediaInfo> modelData = new ArrayList<>();
		try {
			String ps = getParameterValue("path", getAppModel().getState(getClass().getName(), ""), 0);
			if (ps.isEmpty()) 
				return modelData;
			
			Path p = Paths.get(ps);
			if (Files.isDirectory(p) == false && p.getParent() != null)
				p = p.getParent();
			getAppModel().preserveSate(p.toString(), getClass().getName());
			DirectoryStream<Path> stream = Files.newDirectoryStream(p);
			String enc = getAppModel().getCharEncoding();
			for (Path entry : stream) {
				MediaFormat mf = MediaFormatFactory.createMediaFormat(entry.toFile(), enc, true);
				if (mf != null && mf.isValid() && (mf.getType() & MediaFormat.AUDIO) > 0)  // and can play music
					modelData.add((MediaInfo) Proxy.newProxyInstance(this.getClass().getClassLoader(),
							new Class[] { MediaInfo.class }, new MediaInfoProxyHandler(mf.getMediaInfo(), entry)));				
			}
			Collections.sort(modelData, new Comparator<MediaInfo>() {
				@Override
			    public int compare(MediaInfo o1, MediaInfo o2) {
					String a1 = (String)o1.getAttribute(MediaInfo.ALBUM);
					String a2 = (String)o2.getAttribute(MediaInfo.ALBUM);
					int result = a1 != null?a1.compareTo(a2):a2==null?0:1;
					if (result == 0)
						try {
							result = o1.getIntAttribute(MediaInfo.TRACK) - o2.getIntAttribute(MediaInfo.TRACK);
							if (result != 0)
								return result;
						} catch(Exception e) {
							log("%s vs %s",e, o1, o2);
						}
					if (result == 0)
						try {
							return ((String)o1.getAttribute(MediaInfo.TITLE)).compareTo((String)o2.getAttribute(MediaInfo.TITLE));
						} catch (Exception e) {
							log("%s vs %s",e, o1, o2);
						}
					return result;
			    }
			});
			modelInsert("path", p);
		} catch (IOException ioe) {
			modelInsert("error", ioe);
		}
		return modelData;
	}

	@Override
	protected String getUIID() {
		return getAppModel().getAppName();
	}

	static class MediaInfoProxyHandler implements InvocationHandler {
		MediaInfo mediaInfo;
		Path mediaPath;

		MediaInfoProxyHandler(MediaInfo mi, Path mp) {
			mediaInfo = mi;
			mediaPath = mp;
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (method.getName().equals("getAttribute")) {
				if (args.length == 1) {
					if ("MediaPath".equals(args[0]))
						return mediaPath;
					else if (MediaInfo.TITLE.equals(args[0])) {
						String title = (String) method.invoke(mediaInfo, args);
						if (title != null && title.isEmpty() == false)
							return title;
						else
							return mediaPath.getFileName();
					} else if (MediaInfo.GENRE.equals(args[0])) {
						return MP3.findGenre(mediaInfo);
					}
				} 
			}
			return method.invoke(mediaInfo, args);
		}
	}
}
