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

import org.aldan3.servlet.Constant;

import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import photoorganizer.formats.MediaFormatFactory;
import photoorganizer.formats.MP3;

import mediautil.gen.MediaFormat;
import mediautil.gen.MediaInfo;

import rogatkin.music_barrel.model.MBModel;
import rogatkin.music_barrel.util.MusicPath;
import rogatkin.music_barrel.util.RemoteFile;
import rogatkin.music_barrel.model.Artwork;
import rogatkin.music_barrel.model.mb_accnt;

import com.beegman.webbee.block.Tabular;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

import java.util.Comparator;

public class Navigator extends Tabular<List<MediaInfo>, MBModel> {

	static final String MEDIA_PATH = "MediaPath";

	@Override
	protected List<MediaInfo> getTabularData(long pos, int size) {
		List<MediaInfo> modelData = new ArrayList<>();
		List<Artwork> artworkData = new ArrayList<>();
		String enc = getAppModel().getCharEncoding();
		try {
			String ps = getParameterValue("path", getAppModel().getState(getClass().getName(), ""), 0);

			if (ps.isEmpty())
				return modelData;
			if (ps.startsWith(RemoteFile.SAMBA_PREF)) {
				try {
					String smbPath = RemoteFile.SAMBA_PROT + ps.substring(RemoteFile.SAMBA_PREF.length());
					NtlmPasswordAuthentication auth = null;
					mb_accnt accnt = getAppModel().getShareAccnt(smbPath);
					if (accnt != null) {
						auth = new NtlmPasswordAuthentication("workgroup", accnt.name, accnt.password);
					}
					SmbFile dir = new SmbFile(smbPath, auth);
					modelInsert("path", new MusicPath(dir));
					RemoteFile file = new RemoteFile(dir);
					File[] files = file.listFiles();
					if (files != null)
						for (File smb : files) {
							MediaFormat mf = MediaFormatFactory.createMediaFormat(smb, enc, true);
							Path entry = ((RemoteFile) smb).asPath();
							if (mf != null && mf.isValid() && (mf.getType() & MediaFormat.AUDIO) > 0) // and can play
																										// music
								modelData.add((MediaInfo) Proxy.newProxyInstance(this.getClass().getClassLoader(),
										new Class[] { MediaInfo.class },
										new MediaInfoProxyHandler(mf.getMediaInfo(), entry)));
							else { // try to add as image
								//System.out.printf("Adding pic %s%n", entry);
								Artwork aw = Artwork.create(entry);
								if (aw != null)
									artworkData.add(aw);
							}
						}
				} catch (Exception bad) {
					log("Can't process samba path %s", bad, ps);
				}

				return modelData;
			}
			Path p = Paths.get(ps);

			if (ps.startsWith(RemoteFile.SAMBA_PREF)) {
				// already added
			} else {
				if (Files.isDirectory(p) == false && p.getParent() != null)
					p = p.getParent();
				getAppModel().preserveSate(p.toString(), getClass().getName());
				DirectoryStream<Path> stream = Files.newDirectoryStream(p);

				// log("use encoding %s", null, enc);
				// System.out.printf("use encoding %s\n", enc);
				for (Path entry : stream) {
					MediaFormat mf = MediaFormatFactory.createMediaFormat(entry.toFile(), enc, true);
					if (mf != null && mf.isValid() && (mf.getType() & MediaFormat.AUDIO) > 0) // and can play music
						modelData.add((MediaInfo) Proxy.newProxyInstance(this.getClass().getClassLoader(),
								new Class[] { MediaInfo.class }, new MediaInfoProxyHandler(mf.getMediaInfo(), entry)));
					else { // maybe it is an artwork?
						Artwork aw = Artwork.create(entry);
						if (aw != null)
							artworkData.add(aw);
					}
				}
			}
			Collections.sort(modelData, new Comparator<MediaInfo>() {
				@Override
				public int compare(MediaInfo o1, MediaInfo o2) {
					String a1 = (String) o1.getAttribute(MediaInfo.ALBUM);
					String a2 = (String) o2.getAttribute(MediaInfo.ALBUM);
					int result = a1 != null ? a2 != null ? a1.compareTo(a2) : -1 : a2 == null ? 0 : 1;

					if (result == 0) {
						try {
							// result = o1.getIntAttribute(MediaInfo.PARTOFSET) ;
							a1 = (String) o1.getAttribute(MediaInfo.PARTOFSET);
							if (a1 != null) {
								int sp = a1.indexOf("/");
								if (sp > 0)
									a1 = a1.substring(0, sp);
								int ap1 = Integer.parseInt(a1);
								a2 = (String) o2.getAttribute(MediaInfo.PARTOFSET);

								if (a2 != null) {
									sp = a2.indexOf("/");
									if (sp > 0)
										a2 = a2.substring(0, sp);
									int ap2 = Integer.parseInt(a2);
									result = ap1 - ap2;
								}
							}
						} catch (Exception e) {
							if (isDebug())
								log("%s vs %s", e, o1, o2);
						}
					}

					if (result == 0)
						try {
							result = o1.getIntAttribute(MediaInfo.TRACK) - o2.getIntAttribute(MediaInfo.TRACK);
							if (result != 0)
								return result;
						} catch (Exception e) {
							if (isDebug())
								log("%s vs %s", e, o1, o2);
						}
					if (result == 0)
						try {
							return ((Path) o1.getAttribute(MEDIA_PATH)).getFileName()
									.compareTo(((Path) o2.getAttribute(MEDIA_PATH)).getFileName());
						} catch (Exception e) {
							if (isDebug())
								log("%s vs %s", e, o1, o2);
						}
					return result;

				}
			});
			modelInsert("path", new MusicPath(p));
			modelInsert("artwork", artworkData.size() == 0 ? null : artworkData.get(0));
			modelInsert("artworks", artworkData);
			// modelInsert("path", p);
		} catch (IOException ioe) {
			modelInsert("error", ioe);
		}
		return modelData;
	}

	@Override
	protected String getUIID() {
		return getAppModel().getAppName();
	}

	public String processNTLMCall() {
		mb_accnt accnt = new mb_accnt(getAppModel());
		accnt.name = getParameterValue("user", null, 0);
		accnt.password = getParameterValue("password", null, 0);
		accnt.share_path = getParameterValue("path", null, 0);
		accnt.share_path = RemoteFile.SAMBA_PROT + accnt.share_path.substring(RemoteFile.SAMBA_PREF.length());
		getAppModel().addAccnt(accnt);
		System.out.printf("Add: %s%n", accnt);
		return "Ok";
	}

	boolean isDebug() {
		return "1".equals(getProperties().getProperty(Constant.Property.DEBUG));
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
					if (MEDIA_PATH.equals(args[0]))
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
