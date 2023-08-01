package rogatkin.music_barrel.util;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import jcifs.smb.SmbFile;

public class MusicPath implements Comparable {
	Path path;
	SmbFile smbPath; // it should be enum path or string

	public MusicPath(Path p) {
		path = p;
	}

	public MusicPath(String p) throws IOException {
		if (p.startsWith(RemoteFile.SAMBA_PROT))
			smbPath = new SmbFile(p);
		else
			path = Paths.get(p);
	}

	public MusicPath(SmbFile p) {
		smbPath = p;
	}

	public String getFileName() {
		if (smbPath != null) {
			String name = smbPath.getName();
			System.out.printf("name for  %s is %s%n", smbPath, name);
			if (name.equals(RemoteFile.SAMBA_PROT))
				return null;
			return name;
		} else if (path.getFileName() == null)
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
		if (smbPath != null) {
			System.out.printf("samba %s%n", smbPath);
			return RemoteFile.SAMBA_PREF + smbPath.toString().substring(RemoteFile.SAMBA_PROT.length());
		} else if (path == null)
			return "/";
		return path.toString();

	}

	@Override
	public int compareTo(Object path2) {
		if (path2 instanceof MusicPath)
			return path.compareTo(((MusicPath) path2).path);
		return 0;
	}
}
