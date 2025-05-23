package rogatkin.music_barrel.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.WatchKey;
import java.nio.file.LinkOption;
import java.nio.file.FileSystem;
import java.nio.file.WatchService;
import java.nio.file.WatchEvent ;
import java.util.Iterator;
import java.util.Objects;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbAuthException;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFilenameFilter;

public class RemoteFile extends File {
        public static final String SAMBA_PREF = "smb::";
	public static final String SAMBA_PROT = "smb://";

	SmbFile principal;

	SmbException lastError;

	Boolean directory;
	long l = -1;
	
	private static boolean IS_DEBUG = true;
	
	public RemoteFile(URI uri) {
		super(uri);
	}
	
	public RemoteFile(SmbFile file) {
		super("");
		principal = file;
	}

	@Override
	public File[] listFiles() {
		try {
			SmbFile[] files = principal.listFiles();
			RemoteFile[] result = new RemoteFile[files.length];
			for (int j = 0; j < files.length; j++)
				result[j] = new RemoteFile(files[j]); //, principal.getPrincipal());
			return result;
		} catch (SmbException e) {
			lastError = e;
			if (RemoteFile.IS_DEBUG)
				loge("remote-file", "error", e);
			if (e instanceof SmbAuthException)
				throw new SecurityException ("Authentication error", e);
		}
		return null;
	}

	@Override
	public File[] listFiles(final FilenameFilter filter) {
		try {
			SmbFile[] files = principal.listFiles(new SmbFilenameFilter() {

				@Override
				public boolean accept(SmbFile dir, String filename) throws SmbException {
					return filter.accept(RemoteFile.this, filename);
				}
			});
			RemoteFile[] result = new RemoteFile[files.length];
			for (int j = 0; j < files.length; j++)
				result[j] = new RemoteFile(files[j]); //, auth);
			return result;
		} catch (SmbException e) {
			lastError = e;
			if (RemoteFile.IS_DEBUG)
				loge("remote-file", "error", e);
		}
		return null;
	}

	@Override
	public File[] listFiles(final FileFilter filter) {
		try {
			SmbFile[] files = principal.listFiles(new SmbFilenameFilter() {
				@Override
				public boolean accept(SmbFile dir, String filename) {
					try {
						return filter.accept(new RemoteFile(new SmbFile(getPath() + filename, (NtlmPasswordAuthentication) principal.getPrincipal())));
					} catch (Exception e) {
						return false;
					}
				}
			});
			// TODO potentially this boilerplate code can be reused
			RemoteFile[] result = new RemoteFile[files.length];
			for (int j = 0; j < files.length; j++)
				result[j] = new RemoteFile(files[j]); //, auth);
			return result;
		} catch (SmbException e) {
			lastError = e;
			if (RemoteFile.IS_DEBUG)
				loge("remote-file", "error", e);
		}
		return null;
	}

	@Override
	public boolean exists() {
		try {
			return principal.exists();
		} catch (SmbException e) {
			lastError = e;
			if (RemoteFile.IS_DEBUG)
				loge("remote-file", "error", e);
		}
		return false;
	}

	@Override
	public boolean delete() {
		try {
			principal.delete();
			return true;
		} catch (SmbException e) {
			lastError = e;
			if (RemoteFile.IS_DEBUG)
				loge("remote-file", "error", e);
		}
		return false;
	}

	@Override
	public String getName() {
		return principal.getName();
	}

	@Override
	public boolean isDirectory() {
		if (directory == null) {
			try {
				directory = Boolean.valueOf(principal.isDirectory());
				if (l < 0)
					l = principal.length();
			} catch (SmbException e) {
				directory = Boolean.valueOf(false);
				lastError = e;
				if (RemoteFile.IS_DEBUG)
					loge("remote-file", "error", e);
			}
		}
		return directory;
	}

	@Override
	public boolean isFile() {
		if (directory != null)
			return !directory;
		try {
			return principal.isFile();
		} catch (SmbException e) {
			lastError = e;
			if (RemoteFile.IS_DEBUG)
				loge("remote-file", "error", e);
		}
		return false;
	}

	@Override
	public long lastModified() {
		try {
			return principal.lastModified();
		} catch (SmbException e) {
			lastError = e;
			if (RemoteFile.IS_DEBUG)
				loge("remote-file", "error", e);
		}
		return 0;
	}

	@Override
	public boolean setLastModified(long time) {
		try {
			principal.setLastModified(time);
			return true;
		} catch (SmbException e) {
			lastError = e;
			if (RemoteFile.IS_DEBUG)
				loge("remote-file", "error", e);
		}
		return false;
	}

	@Override
	public long length() {
		if (l < 0)
			try {
				l = principal.length();
			} catch (SmbException e) {
				lastError = e;
				if (RemoteFile.IS_DEBUG)
					loge("remote-file", "error", e);
			}
		return l;
	}

	@Override
	public File getParentFile() {
		try {
			return new RemoteFile(new SmbFile(principal.getParent(), (NtlmPasswordAuthentication) principal.getPrincipal()));
		} catch (IOException e) {
			// lastError = new SmbException(e);
			if (RemoteFile.IS_DEBUG)
				loge("remote-file", "error", e);
		}
		return null;
	}

	@Override
	public String getParent() {
		return principal.getParent();
	}

	@Override
	public String getPath() {
		return principal.getPath();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj instanceof RemoteFile)
			//return principal.equals(((RFSFile) obj).principal);
			return principal.getPath().equals(((RemoteFile) obj).principal.getPath());
		return false;
	}

	@Override
	public boolean renameTo(File newPath) {
		if (newPath instanceof RemoteFile == false)
			return false;
		try {
			principal.renameTo(((RemoteFile)newPath).getSmbFile());
			return true;
		} catch (SmbException e) {
			lastError = e;
			if (RemoteFile.IS_DEBUG)
				loge("remote-file", "error", e);
		}
		return false;
	}

	@Override
	public URI toURI() {
		try {
			return new URI(URLEncoder.encode(principal.getCanonicalPath(), "UTF-8"));
		} catch (URISyntaxException e) {
			if (RemoteFile.IS_DEBUG)
				loge("remote-file", "error", e);
		} catch (IOException e) {
			if (RemoteFile.IS_DEBUG)
				loge("remote-file", "error", e);
		}
		return null;
	}

	@Override
	public boolean createNewFile() throws IOException {
		principal.createNewFile();
		return true;
	}

	@Override
	public boolean mkdir() {
		NtlmPasswordAuthentication auth = (NtlmPasswordAuthentication) principal.getPrincipal();
		try {
			if (principal.getName().endsWith("/") == false)
				principal = auth == null ? new SmbFile(principal.getPath()+"/") : new SmbFile(principal.getPath()+"/", auth, SmbFile.FILE_SHARE_READ);
			principal.mkdir();
			return true;
		} catch (SmbException e) {
			lastError = e;
			if (RemoteFile.IS_DEBUG)
				loge("remote-file", "error", e);
		} catch (IOException e) {
			if (RemoteFile.IS_DEBUG)
				loge("remote-file", "error", e);
		}
		return false;
	}

	@Override
	public String toString() {
		return "RFSFile [principal=" + principal + ", lastError=" + lastError  + "]";
	}

	public boolean isValid() {
		return lastError == null;
	}

	public boolean isnAuth() {
		return lastError instanceof SmbAuthException;
	}

	public SmbFile getSmbFile() {
		return principal;
	}
	
	public Path asPath() {
	    try {
		    return Paths.get(new URI(SAMBA_PREF + getPath().substring(SAMBA_PROT.length())));
	//    } catch(URISyntaxException use) {
	//        System.err.println(use);
	    } catch (URISyntaxException | java.nio.file.FileSystemNotFoundException fsne) {
	        return new SMBPath(SAMBA_PREF + getPath().substring(SAMBA_PROT.length()));
	    }
	    //return null;
	}
	
	public static class SMBPath implements Path {

        private final SmbFile path;
    
        public SMBPath(String pathString) {
            try {
                this.path = new SmbFile(pathString);
            } catch(/*MalformedURL*/Exception use) {
                throw new RuntimeException(use);
            }
        }
    
        @Override
        public FileSystem getFileSystem() {
            return null;
        }
    
        @Override
        public boolean isAbsolute() {
            return true;
        }
    
        @Override
        public Path getRoot() {
            return new SMBPath(SAMBA_PREF);
        }
    
        @Override
        public Path getFileName() {
            return new SMBPath(path.getName());
        }
    
        @Override
        public Path getParent() {
            return new SMBPath(path.getParent());
        }
    
        @Override
        public int getNameCount() {
            return 1;
        }
    
        @Override
        public Path getName(int index) {
            return new SMBPath(path.getName());
        }
    
        @Override
        public Path subpath(int beginIndex, int endIndex) {
            return null;
        }
    
        @Override
        public boolean startsWith(Path other) {
            return false;
        }
    
        @Override
        public boolean startsWith(String other) {
            return false;
        }
    
        @Override
        public boolean endsWith(Path other) {
            return false;
        }
    
        @Override
        public boolean endsWith(String other) {
            return false;
        }
    
        @Override
        public Path normalize() {
            return this;
        }
    
        @Override
        public Path resolve(Path other) {
            return null;
        }
    
        @Override
        public Path resolve(String other) {
            return null;
        }
    
        @Override
        public Path resolveSibling(Path other) {
            return null;
        }
    
        @Override
        public Path resolveSibling(String other) {
            return null;
        }
    
        @Override
        public Path relativize(Path other) {
            return null;
        }
    
        @Override
        public URI toUri() {
            try {
                return new URI(path.toString());
            } catch(URISyntaxException use) {
                return null;
            }
        }
    
        @Override
        public Path toAbsolutePath() {
            return new SMBPath(path.toString());
        }
    
        @Override
        public Path toRealPath(LinkOption... options) throws IOException {
            return null;
        }
    
        @Override
        public File toFile() {
            return new File(toString());
        }
    
        @Override
        public WatchKey register(WatchService watcher, WatchEvent.Kind<?>[] events, WatchEvent.Modifier... modifiers) throws IOException {
            return null;
        }
    
        @Override
        public WatchKey register(WatchService watcher, WatchEvent.Kind<?>... events) throws IOException {
            return null;
        }
    
        @Override
        public Iterator<Path> iterator() {
            return null;
        }
    
        @Override
        public int compareTo(Path other) {
            return toString().compareTo(other.toString());
        }
    
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            SMBPath that = (SMBPath) obj;
            return Objects.equals(path, that.path);
        }
    
        @Override
        public int hashCode() {
            return Objects.hash(path);
        }
    
        @Override
        public String toString() {
            return path.toString();
        }

	}
	
	private void loge(String scope, String message, Exception e, Object ... args) {
		System.err.printf(message, args);
		if (e != null)
			e.printStackTrace();
	}
}
