package rogatkin.music_barrel.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.Iterator;
import java.lang.reflect.Field;

import javax.sql.DataSource;

import org.aldan3.data.DODelegator;
import org.aldan3.data.DOService;
import org.aldan3.model.Log;
import org.aldan3.model.ProcessException;
import org.aldan3.util.DataConv;
import org.aldan3.annot.Inject;
import org.aldan3.model.ServiceProvider;

import photoorganizer.formats.MP3;
import photoorganizer.formats.MediaFormatFactory;
import photoorganizer.formats.InputStreamFactory;

import mediautil.gen.MediaFormat;
import mediautil.gen.MediaInfo;

import rogatkin.music_barrel.ctrl.Navigator;
import rogatkin.music_barrel.srv.MediaCrawler;
import rogatkin.music_barrel.srv.PlayerService;
import rogatkin.music_barrel.util.RemoteFile;
import rogatkin.music_barrel.util.ApeFile;
import rogatkin.music_barrel.util.RemoteChannel;

import com.beegman.webbee.model.AppModel;
import com.beegman.buzzbee.NotificationServiceImpl;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbRandomAccessFile;
import net.didion.loopy.AccessStream;
import org.justcodecs.dsd.DSDStream;

public class MBModel extends AppModel implements Name {
	public static NotificationServiceImpl notifService;
	
	private mb_setting settings;
	private HashMap<String, Object> preserve;

	private ArrayList<mb_accnt> accounts;

	@Override
	public String getAppName() {
		return "music-barrel";
	}

	public String getCharEncoding() {
		if (settings.char_encoding != null)
			return settings.char_encoding;
		return "UTF-8";
	}

	@Override
	protected void deactivateServices() {
		PlayerService ps = (PlayerService) unregister(PlayerService.NAME);
		ps.terminate();

		MediaCrawler mc = (MediaCrawler) unregister(MediaCrawler.NAME);
		if (mc != null)
			mc.shutdown();
		try {
			saveSettings();
		} catch (ProcessException e) {
			Log.l.error("Save settings", e);
		}
		((NotificationServiceImpl) unregister(getService(notifService.getPreferredServiceName()))).destroy();
		notifService =  null;
		super.deactivateServices();
		preserve.clear();
	}

	public void saveSettings() throws ProcessException {
		// System.err.printf("--->Object: %s%n", new DODelegator(settings, null, "",
		// "id").get("output_type").getClass());
		getDOService().addObject(new DODelegator(settings, null, "id", ""), null,
				new DODelegator(settings, null, "", "id"));
	}

	@Override
	protected String getServletName() {
		return super.getServletName();
	}

	@Override
	protected void initServices() {
		preserve = new HashMap<>();
		super.initServices();
		register(notifService = new NotificationServiceImpl().init(new Properties(), this).start());
		settings = new mb_setting(this);
		settings.id = 1;
		try {
			getDOService().getObjectLike(new DODelegator(settings, null, "", "id") {
				@Override
				protected String normilizeFieldName(String fieldName) {
					return fieldName.toUpperCase();
				}
			});
			settings.last_directory = ""; // temporary until preserved
			preserveSate(settings.last_directory, Navigator.class.getName());
		} catch (ProcessException e) {
			Log.l.error("Load settings", e);
		}
		MediaFormatFactory.setInputStreamFactory(new StreamFactoryWithRemote());
		register(inject(new PlayerService(this)));
		if (settings.perform_scan)
			register(new MediaCrawler(this));

		
		accounts = new ArrayList<>(6);
	}

	@Override
	protected DOService createDataService(DataSource datasource) {
		return new DOService(datasource) {
			@Override
			protected int getInsertUpdateVariant() {
				return 2;
			}

			@Override
			protected boolean isCreateIndex() {
				return true;
			}
		};
	}

	@Override
	public Behavior getCommonBehavior() {
		return new Behavior();
	}

	public PlayerService getPlayer() {
		return (PlayerService) getService(PlayerService.NAME);
	}

	public mb_setting getSettings() {
		return settings;
	}

	public boolean dropShareAccnt(String path) {
		for (mb_accnt a : accounts) {
			if (path.startsWith(a.share_path)) {
			    accounts.remove(a);
				return true;
			}
		}
		return false;
	}
	
	public mb_accnt getShareAccnt(String path) {
		for (mb_accnt a : accounts) {
			if (path.startsWith(a.share_path))
				return a;
		}
		return null;
	}

	public void addAccnt(mb_accnt accnt) {
		for (mb_accnt a : accounts) {
			if (a.matches(accnt)) {
				a.password = accnt.password;
				return;
			}
		}
		accounts.add(accnt);
	}

	public void updateAccnt(mb_accnt accnt) {
		for (mb_accnt a : accounts) {
			if (a.matches(accnt)) {
				a.password = accnt.password;
				return;
			}
		}
	}

	synchronized public <T> T preserveSate(T state, String name) {
		T oldState = null;
		// TODO generally can be stored on session level but since no sessions
		if (preserve.containsKey(name)) {
			oldState = (T) preserve.remove(name);
		}
		preserve.put(name, state);
		return oldState;
	}

	synchronized public <T> T getState(String name, T defVal) {
		if (preserve.containsKey(name))
			return (T) preserve.get(name);
		return defVal;
	}

	public void addToPlayList(mb_media_item item, String listName) throws MBError {
		MediaFormat mf = MediaFormatFactory.createMediaFormat(getItemPath(item.path).toFile(), getCharEncoding(), true);
		if (mf == null || mf.isValid() == false)
			throw new MBError("Ivalid format :" + item);
		mb_play_list pl = new mb_play_list(this);
		if (listName == null || listName.isEmpty())
			pl.title = ON_THE_GO;
		else
			pl.title = listName;
		DOService dos = getDOService();
		// TODO synchronize playlist creation to avoid multiple
		try {
			// TODO use create or update
			dos.getObjectLike(new DODelegator(pl, null, "", "title") {
				@Override
				protected String normilizeFieldName(String fieldName) {
					return fieldName.toUpperCase();
				}
			});
			if (pl.id <= 0) {
				pl.created_on = new Date();
				pl.modified_on = pl.created_on;
				dos.addObject(new DODelegator(pl, null, "", "id"), "id");
			}
			item = addToLibrary(mf);
			// log("Object %s for path %s", null, oo, li.path);
			if (item.id <= 0) {
				throw new MBError("Can't add item " + mf + " to library");
			}
			mb_play_list_map plm = new mb_play_list_map(this);
			plm.item_id = item.id;
			plm.list_id = pl.id;
			plm.related_on = new Date();
			if (getSettings().allow_duplicates)
				dos.addObject(new DODelegator(plm));
			else
				dos.addObject(new DODelegator(plm, null, "item_id,list_id", "id"), null,
						new DODelegator(plm, null, "", "item_id,list_id"));
		} catch (Exception e) {
			throw new MBError("Add item to list error", e);
		} finally {
		    try {mf.getAsStream().close();} catch(Exception e) {}
		}
	}

	public mb_media_item addToLibrary(MediaFormat mf) throws MBError {
		mb_media_item item = new mb_media_item(this);
		mb_media_set set = new mb_media_set(this);
		fillMediaModel(item, set, mf.getMediaInfo());
		item.losed = "FLACWVM4AAPE".indexOf(mf.getDescription()) < 0;
		item.path = mf.getFile().toPath().toString();
		DODelegator dod;
		try {
			if (set.title != null && set.title.isEmpty() == false) {
				getDOService().addObject(dod = new DODelegator(set, null, "", "id,title,subset_num"), "id",
						new DODelegator(set, null, "id", "title,subset_num"));
				if (set.id == 0) {
					getDOService().getObjectLike(
							dod = new DODelegator(set, null, "num_subsets,studio,year", "title,subset_num") {
								@Override
								protected String normilizeFieldName(String fieldName) {
									return fieldName.toUpperCase();
								}
							});
				}
				item.set_id = set.id;
			}
		} catch (ProcessException e) {
			throw new MBError("Add set to library error: " + mf, e);
		}
		;
		try {
			getDOService().addObject(new DODelegator(item, null, "path", "id"), "id",
					dod = new DODelegator(item, null, "", "path") {

						@Override
						protected String normilizeFieldName(String fieldName) {
							return fieldName.toUpperCase();
						}
					});
			if (item.id <= 0) {
				if (getDOService().getObjectLike(dod) == null)
					throw new MBError("Can't resolve item at " + item.path);
			}
		} catch (

		Exception e) {
			throw new MBError("Add item to library error: " + mf, e);
		}
		return item;
	}
	
	public File fromWebPath(String path) {
		if (path.startsWith(RemoteFile.SAMBA_PREF)) {
			path = RemoteFile.SAMBA_PROT + path.substring(RemoteFile.SAMBA_PREF.length());
		}
		if (path.startsWith(RemoteFile.SAMBA_PROT)) {
			try {
				
				NtlmPasswordAuthentication auth = null;
				mb_accnt accnt = getShareAccnt(path);
				if (accnt != null) {
					auth = new NtlmPasswordAuthentication("workgroup", accnt.name, accnt.password);
				}
				return new RemoteFile(new SmbFile(path, auth));
			} catch (Exception e) {
				Log.l.error("Problem in file", e);
			} finally {
				
			}
		} else {
			Path p = getItemPath(path);
			if (p != null)
				return p.toFile();
		}
		//	System.out.printf("File null for %s%n", path);
		return null;
	}
	
	 @Override
	public <T> T inject(T obj) {
		if (obj == null) {
			return null;
		}
		for (Field fl : obj.getClass().getDeclaredFields()) { // use cl.getFields() for public with inheritance
			if (fl.getAnnotation(Inject.class) != null) {
				try {
					Class<?> type = fl.getType();
					Object serv = lookupService(type);
				//	System.err.printf("Injecting "+serv+" for "+fl+" of "+type+"\n");
					assureAccessible(fl).set(obj, serv);
				} catch (Exception e) {
					Log.l.error("Exception in injection for " + fl, e);
				}
			}
		}
		return obj;
	}
	 
	 public Object lookupService(Class<?> type) {
			Iterator<ServiceProvider> i = iterator();
			while (i.hasNext()) {
				ServiceProvider sp = i.next();
				// System.err.printf("Checking %s assign %b from %s%n", sp, type.isAssignableFrom(sp.getClass()), type );
				if (sp.getClass() == type || type.isAssignableFrom(sp.getClass()) ) 
					return sp;
			}
			return null;
		}
		
		static protected Field assureAccessible(Field fl) {
			if (fl.isAccessible())
				return fl;
			fl.setAccessible(true);
			return fl;
		}
		

	public static void fillMediaModel(mb_media_item mi, MediaInfo info) {
		fillMediaModel(mi, null, info);
	}

	public static void fillMediaModel(mb_media_item mi, mb_media_set set, MediaInfo info) {
		mi.title = (String) info.getAttribute(MediaInfo.TITLE);
		mi.performer = (String) info.getAttribute(MediaInfo.ARTIST);
		mi.track = intValue(info.getAttribute(MediaInfo.TRACK));
		mi.year = intValue(info.getAttribute(MediaInfo.YEAR));
		mi.genre = DataConv.ifNull(MP3.findGenre(info), "Unknown");
		mi.added_on = new Date();
		if (set != null) {
			set.title = (String) info.getAttribute(MediaInfo.ALBUM);
			String partofset = (String) info.getAttribute(MediaInfo.PARTOFSET);
			if (partofset != null && !partofset.isEmpty()) {
				String[] parts = partofset.split("/");
				set.subset_num = intValue(parts[0]);
				if (parts.length > 1)
					set.num_subsets = intValue(parts[1]);
			}
			set.studio = (String) info.getAttribute(MediaInfo.PUBLISHER);
			set.year = intValue(info.getAttribute(MediaInfo.YEAR));
		}
	}

	public static Path getItemPath(String path) {
		if (path != null) {
			Path result = null;
			try {
			    result = Paths.get(path);
    			if (path.startsWith(RemoteFile.SAMBA_PREF) || Files.isRegularFile(result) && Files.isReadable(result))
    				return result;
			} catch(java.nio.file.FileSystemNotFoundException | java.nio.file.InvalidPathException fsne) {
    	        return new RemoteFile.SMBPath(path);
    	    }
		}
		return null;
	}

	public static int intValue(Object o) {
		if (o instanceof Number)
			return ((Number) o).intValue();
		else if (o instanceof String)
			try {
				return Integer.valueOf((String) o).intValue();
			} catch (NumberFormatException e) {

			}
		return 0;
	}

	public static class StreamFactoryWithRemote extends InputStreamFactory {
		@Override
		public InputStream getInputStream(File file) throws IOException {
			if (file instanceof RemoteFile)
				return new SmbFileInputStream(((RemoteFile) file).getSmbFile());
			return new FileInputStream(file);
		}

		@Override
		public AccessStream getRandomAccessStream(File file) throws IOException {
			if (file instanceof RemoteFile)
				return new SmbAccessStream(file, "r");
			return super.getRandomAccessStream(file);
		}

		@Override
		public DSDStream getDSDStream(File file) throws IOException {
			if (file instanceof RemoteFile) {
				return new SmbDSDStream(file);
			}
			return super.getDSDStream(file);
		}

		@Override
		public davaguine.jmac.tools.File createApeFile(File file) throws IOException {
			if (file instanceof RemoteFile) {
				return new ApeFile(file);
			}
			return davaguine.jmac.tools.File.createFile(file.getPath(), "r");
		}
		
		public FileChannel getInputChannel(File file) throws IOException {
			if (file instanceof RemoteFile) {
				return new RemoteChannel(file);
			}
			   return super.getInputChannel(file);
		   }
	}

	static class SmbAccessStream extends SmbRandomAccessFile implements AccessStream {
		public SmbAccessStream(File file, String mode) throws IOException {
			super(((RemoteFile) file).getSmbFile(), mode);
		}

	}

	static class SmbDSDStream extends SmbRandomAccessFile implements DSDStream {

		protected byte[] buf = new byte[8];

		public SmbDSDStream(File f) throws IOException {
			super(((RemoteFile) f).getSmbFile(), "r");
		}

		@Override
		public boolean canSeek() {
			return true;
		}

		@Override
		public long readLong(boolean lsb) throws IOException {
			readFully(buf, 0, 8);
			if (lsb)
				return (((long) (buf[0] & 0xff) << 56) | ((long) (buf[1] & 0xff) << 48) | ((long) (buf[2] & 0xff) << 40)
						| ((long) (buf[3] & 0xff) << 32) | ((long) (buf[4] & 0xff) << 24)
						| ((long) (buf[5] & 0xff) << 16) | ((long) (buf[6] & 0xff) << 8) | ((long) (buf[7] & 0xff)));
			// System.out.printf("Buf 7 %d 0 - %d %n", buf[7], buf[0]);
			return ((long) (buf[7] & 255) << 56) + ((long) (buf[6] & 255) << 48) + ((long) (buf[5] & 255) << 40)
					+ ((long) (buf[4] & 255) << 32) + ((long) (buf[3] & 255) << 24) + ((long) (buf[2] & 255) << 16)
					+ ((long) (buf[1] & 255) << 8) + (buf[0] & 255);
		}

		@Override
		public int readInt(boolean lsb) throws IOException {
			readFully(buf, 0, 4);
			if (lsb)
				return (((buf[0] & 0xff) << 24) | ((buf[1] & 0xff) << 16) | ((buf[2] & 0xff) << 8) | (buf[3] & 0xff));
			return ((int) (buf[3] & 255) << 24) + ((int) (buf[2] & 255) << 16) + ((int) (buf[1] & 255) << 8)
					+ (buf[0] & 255);
		}

		@Override
		public long readIntUnsigned(boolean lsb) throws IOException {
			readFully(buf, 0, 4);
			long result = 0;
			if (lsb) {
				result += ((buf[0] & 255) << 24) + ((buf[1] & 255) << 16) + ((buf[2] & 255) << 8) + (buf[3] & 255);
			} else {
				result += ((buf[3] & 255) << 24) + ((buf[2] & 255) << 16) + ((buf[1] & 255) << 8) + (buf[0] & 255);
			}
			return result;
		}

		@Override
		public short readShort(boolean lsb) throws IOException {
			readFully(buf, 0, 2);
			if (lsb)
				return (short) ((buf[0] << 8) | (buf[1] & 0xff));
			return (short) (((short) (buf[1] & 255) << 8) + (buf[0] & 255));
		}

	}

}
