package rogatkin.music_barrel.model;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;

import org.aldan3.annot.NotNull;
import org.aldan3.model.Log;

import jcifs.smb.SmbFile;
import rogatkin.music_barrel.util.RemoteFile;

public class Artwork {
	static PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**.{jpg,jpeg,png,JPG,JPEG,PNG}");
	
	Path image;
	
	public static Artwork create(Path path) {
		//System.out.printf("testing: %s%n", path);
		if (matcher.matches(path)) {
			System.out.printf("art: %s%n", path);
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
