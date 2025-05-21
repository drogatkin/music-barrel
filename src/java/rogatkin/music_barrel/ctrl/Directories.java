package rogatkin.music_barrel.ctrl;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import rogatkin.music_barrel.model.MBModel;
import rogatkin.music_barrel.model.mb_accnt;
import rogatkin.music_barrel.util.MusicPath;
import rogatkin.music_barrel.util.RemoteFile;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;

import com.beegman.webbee.block.Gadget;

public class Directories extends Gadget<Collection<MusicPath>, MBModel> {
	static final boolean DEBUG_AUTH = false;

	@Override
	protected Collection<MusicPath> getGadgetData() {
		ArrayList<MusicPath> result = new ArrayList<>();
		FileSystem fs = FileSystems.getDefault();
		String ps = getParameterValue("path", "", 0);
		//System.out.printf("path %S%n",  ps);
		Path cp = null;
		if (ps.startsWith(RemoteFile.SAMBA_PREF)) {
		    String smbPath = RemoteFile.SAMBA_PROT + ps.substring(RemoteFile.SAMBA_PREF.length());
			try {
				NtlmPasswordAuthentication auth = null;
				mb_accnt accnt = getAppModel().getShareAccnt(smbPath);
				if (accnt != null) {
					auth = new NtlmPasswordAuthentication("workgroup", accnt.name, accnt.password);
					if (DEBUG_AUTH)
					    System.err.printf("auth %s pas %s for %s%n",  accnt.name, accnt.password);
				}
				
				SmbFile dir = new SmbFile(smbPath, auth);
				SmbFile[] files = dir.listFiles();
				if (files != null)
					for (SmbFile smb : files) {
						if (smb.isDirectory())
							result.add(new MusicPath(smb));
					}
				try {
    				ps = dir.getParent();
    				//System.out.printf("parent: %s%n", ps);
    				if (RemoteFile.SAMBA_PROT.equals(ps)) {
    					MusicPath mp = new MusicPath("");
    					mp.setCustom();
    					result.add(mp);
    					for (Path p : fs.getRootDirectories()) {
    						result.add(new MusicPath(p));
    					}
    				} else {
    					result.add(new MusicPath(ps));
    				}
				} catch(Exception e)	{
				    // parent can be null
				    log("No parent for samba path %s", e, ps);
				}
				return result;
			} catch (Exception bad) {
				log("Can't process samba path %s", bad, ps);
				// remove the account as good
				getAppModel().dropShareAccnt(smbPath);
			}
			 
		} else {
			if (ps.isEmpty() == false) {

				cp = fs.getPath(ps);
				try (java.nio.file.DirectoryStream<Path> stream = Files.newDirectoryStream(cp)){
					for (Path p : stream) {
						if (Files.isDirectory(p))
							result.add(new MusicPath(p));
					}
				} catch (Exception e) {
					log("", e);
				}
				Collections.sort(result);
				cp = cp.getParent();
			}
			
		}
		if (cp == null) {
			for (Path p : fs.getRootDirectories()) {
				result.add(new MusicPath(p));
			}
			try {
				result.add(new MusicPath(RemoteFile.SAMBA_PROT));
			} catch (Exception e) {
				log("Can't add samba path  %s", e, RemoteFile.SAMBA_PROT);
			}
		} else
			result.add(new MusicPath(cp));
		return result;
	}
}
