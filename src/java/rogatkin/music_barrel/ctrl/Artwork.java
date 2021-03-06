package rogatkin.music_barrel.ctrl;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;

import mediautil.gen.MediaFormat;
import photoorganizer.formats.MediaFormatFactory;

import rogatkin.music_barrel.model.MBModel;

import com.beegman.webbee.block.Stream;



public class Artwork extends Stream<MBModel> {
	@Override
	protected void fillStream(OutputStream os) throws IOException {
		String sp = getParameterValue("path", "", 0);
		if (sp.isEmpty()) {
			os.close();
			return;
		}
		Path p = Paths.get(sp);
		if (Files.isRegularFile(p) == false) {
			os.close();
			return;
		}
		MediaFormat mf = MediaFormatFactory.createMediaFormat(p.toFile(), getAppModel().getCharEncoding(), true);
		if (mf != null && mf.isValid()) {
			os.write(mf.getThumbnailData(null));
		}
		os.close();
	}
	
	@Override
	protected boolean canCache() {
		return true;
	}
}
