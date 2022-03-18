package rogatkin.music_barrel.model;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;

import org.aldan3.annot.NotNull;
import org.aldan3.model.Log;

public class Artwork {
	static PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**.{jpg,jpeg,png}");
	
	Path image;
	
	public static Artwork create(Path path) {
		//Log.l.debug("testing: %s", path);
		if (matcher.matches(path)) {
			//Log.l.debug("art: %s", path);
			return new Artwork(path);
		}
		return null;
	}
	
	private Artwork(@NotNull Path path) {
		image = path;
	}

	@Override
	public String toString() {
		return image.toString();
	}
	
}
