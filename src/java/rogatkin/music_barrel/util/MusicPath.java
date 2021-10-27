package rogatkin.music_barrel.util;

import java.nio.file.Path;

public class MusicPath implements Comparable {
	Path path;
	public MusicPath(Path p) {
		path = p;
	}
	public String getFileName() {

		return path.getFileName().toString();
	}

	
	public MusicPath getParent() {
		return new MusicPath(path.getParent());
	}
	
	public Path getParentPath() {
		return path.getParent();
	}
	
	@Override
	public String toString() {
		if (path == null)
			return "/";
		return path.toString();
		
	}
	
	@Override
	public int compareTo(Object path2) {
		if (path2 instanceof MusicPath)
			return path.compareTo(((MusicPath)path2).path);
		return 0;
	}
}
