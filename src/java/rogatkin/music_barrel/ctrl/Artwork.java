package rogatkin.music_barrel.ctrl;

import java.io.IOException;
import java.nio.file.Path;

import java.nio.file.Paths;
import java.io.OutputStream;
import java.nio.file.Files;

import mediautil.gen.MediaFormat;
import photoorganizer.formats.MediaFormatFactory;

import rogatkin.music_barrel.model.MBModel;
import rogatkin.music_barrel.model.mb_accnt;
import rogatkin.music_barrel.util.RemoteFile;

import com.beegman.webbee.block.Stream;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;


public class Artwork extends Stream<MBModel> {
	
	@Override
	protected void fillStream(OutputStream os) throws IOException {
		String sp = getParameterValue("path", "", 0);
		if (sp.isEmpty()) {
			os.close();
			return;
		}
		System.out.printf("Pic %s%n",  sp);
		Path p = MBModel.getItemPath(sp); //.normalize();
		if (p == null) {
			os.close();
			return;
		}
		MediaFormat mf = MediaFormatFactory.createMediaFormat(p.toFile(), getAppModel().getCharEncoding(), true);
		//frontController.log("media for path: "+p+" is "+mf);
		if (mf != null && mf.isValid()) {
			os.write(mf.getThumbnailData(null));
		} else {
			rogatkin.music_barrel.model.Artwork aw = rogatkin.music_barrel.model.Artwork.create(p);
			if (aw != null) {
				if (sp.startsWith(RemoteFile.SAMBA_PREF)) {
					try {
						String smbPath = RemoteFile.SAMBA_PROT + sp.substring(RemoteFile.SAMBA_PREF.length());
						NtlmPasswordAuthentication auth = null;
						mb_accnt accnt = getAppModel().getShareAccnt(smbPath);
						if (accnt != null) {
							auth = new NtlmPasswordAuthentication("workgroup", accnt.name, accnt.password);
						}
						SmbFile pic = new SmbFile(smbPath, auth);
						SmbFileInputStream is = new SmbFileInputStream(pic);
						org.aldan3.util.Stream.copyStream(is, os);
						is.close(); // TODO move to finally
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						
					}
				} else
				   Files.copy(p, os);
			}
		}
		
		os.close();
	}
	
	@Override
	protected boolean canCache() {
		return true;
	}
}
