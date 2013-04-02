package rogatkin.music_barrel.ctrl;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
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

public class Navigator extends Tabular<List<MediaInfo>, MBModel> {
	@Override
	protected List<MediaInfo> getTabularData(long pos, int size) {
		List<MediaInfo> modelData = new ArrayList<>();
		try {
			String ps = getParameterValue("path", "", 0);
			if (ps.isEmpty())
				return modelData;
			Path p = FileSystems.getFileSystem(new URI("file:///")).getPath(ps);
			if (Files.isDirectory(p) == false && p.getParent() != null)
				p = p.getParent();
			DirectoryStream<Path> stream = Files.newDirectoryStream(p);
			for (Path entry : stream) {
				MediaFormat mf = MediaFormatFactory.createMediaFormat(entry.toFile());
				if (mf != null && mf.isValid() && (mf.getType() & MediaFormat.AUDIO) > 0)  // and can play music
					modelData.add((MediaInfo) Proxy.newProxyInstance(this.getClass().getClassLoader(),
							new Class[] { MediaInfo.class }, new MediaInfoProxyHandler(mf.getMediaInfo(), entry)));				
			}
			modelInsert("path", p);
		} catch (IOException | URISyntaxException ioe) {
			modelInsert("error", ioe);
		}
		return modelData;
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
