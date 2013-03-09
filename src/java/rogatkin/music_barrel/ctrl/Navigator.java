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
		Path p = FileSystems.getDefault().getPath(getParameterValue("path", "/", 0));
		if (Files.isDirectory(p) == false)
			p = p.getParent();
		try {
			DirectoryStream<Path> stream = Files.newDirectoryStream(p);
			for (Path entry : stream) {
				MediaFormat mf = MediaFormatFactory.createMediaFormat(entry.toFile());
				if (mf != null && mf.isValid()) // and can play music
					modelData.add((MediaInfo) Proxy.newProxyInstance(this.getClass().getClassLoader(),
							new Class[] { MediaInfo.class }, new MediaInfoProxyHandler(mf.getMediaInfo(), entry)));
			}
		} catch (IOException ioe) {
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
