package rogatkin.music_barrel.srv;

import java.nio.file.FileVisitResult;
import java.util.Date;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.FileSystems;
import java.nio.file.FileSystem;
import java.nio.file.FileVisitResult;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.AccessDeniedException;
import java.nio.file.attribute.BasicFileAttributes;
import java.io.IOException;

import org.aldan3.model.ServiceProvider;
import mediautil.gen.MediaFormat;
import photoorganizer.formats.MediaFormatFactory;

import rogatkin.music_barrel.model.MBModel;

import com.beegman.webbee.util.Cron;

public class MediaCrawler extends Cron<Object, MBModel> implements ServiceProvider<MediaCrawler>, Runnable {
	public final static String NAME = "Media-Crawler";

	String[] skip_directories = { "Windows", "Program Files", "dell", "Program Files (x86)", "Drivers", "$RECYCLE.BIN",
			"$Recycle.Bin", "MSOCache", "ProgramData",
		 	"etc","lib", "lost+found", "opt", "proc", "run", "sbin", "srv", "sys", "tmp", "usr", "var"};

	private Object ctrl;

	public MediaCrawler(MBModel m) {
		super(m);
	}

	@Override
	public String getPreferredServiceName() {
		return NAME;
	}

	@Override
	public MediaCrawler getServiceProvider() {
		return this;
	}

	@Override
	protected Runnable getTask() {
		return this;
	}

	@Override
	protected long getInterval() {
		return 4 * 24;
	}
	
	@Override
	protected long getInitialInterval() {
		Date last = appModel.getSettings().last_scan;
		if (last == null)
			return 0;
		long result = System.currentTimeMillis() - last.getDate() - getInterval()*1000*60*60;
		if (result < 0)
			return 0;
		return result;
	}

	@Override
	public void run() {
		crawl();
	}

	@Override
	public synchronized void suspend() {
		if (ctrl != null)
			return;
		ctrl = new Object();
	}

	public synchronized void resume() {
		if (ctrl != null)
			synchronized (ctrl) {
				ctrl.notify();
			}
	}

	protected void crawl() {
		FileSystem fs = FileSystems.getDefault();
		for (Path p : fs.getRootDirectories()) {
			try {
				crawl(p);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// TODO put mark last run
		appModel.getSettings().last_scan = new Date();
	}

	protected void crawl(Path path) throws IOException {
		Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				if (checkEligablePath(dir))
					return FileVisitResult.CONTINUE;
				return FileVisitResult.SKIP_SUBTREE;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				if (ctrl != null)
					synchronized (ctrl) {
						try {
							ctrl.wait();
						} catch (Exception e) {
							return FileVisitResult.TERMINATE;
						}
						ctrl = null;
					}
				MediaFormat mf = MediaFormatFactory.createMediaFormat(file.toFile(), appModel.getCharEncoding(), true);
				if (mf != null && mf.isValid() && (mf.getType() & MediaFormat.AUDIO) > 0) {
					try {
						appModel.addToLibrary(mf, file);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
				if (exc instanceof AccessDeniedException)
					return FileVisitResult.SKIP_SUBTREE;
				throw exc;
			}
		});
	}

	protected boolean checkEligablePath(Path path) {
		FileSystem fs = path.getFileSystem();
		boolean result = true;
		for (String excl : skip_directories) {
			if (path.startsWith(excl) || (path.getNameCount() > 0 && path.subpath(0, 1).startsWith(excl)))
				return false;

		}
		//System.err.printf("Accepting %s, %d - %s%n", path, path.getNameCount(),
			//	path.getNameCount() > 1 ? path.subpath(1, 2) : path);
		return result;
	}

}
