package rogatkin.music_barrel.util;

import java.nio.file.Path;

public class MusicPath implements Comparable {
	Path path;
	public MusicPath(Path p) {
		path = p;
	}
	public String getFileName() {
		if (path.getFileName() == null)
			return null;
		return path.getFileName().toString();
	}

	
	public MusicPath getParent() {
		return new MusicPath(path.getParent());
	}
	
	public static String getJustName(Path path) {
		String name = path.getFileName().toString();
		int pos = name.lastIndexOf(".");
		if (pos > 0 && pos < (name.length() - 1)) { // If '.' is not the first or last character.
		    name = name.substring(0, pos);
		}
		return name;
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
