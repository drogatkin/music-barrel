package rogatkin.music_barrel.ctrl;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import rogatkin.music_barrel.model.MBModel;

import com.beegman.webbee.block.Gadget;

public class Directories extends Gadget<Collection<Path>, MBModel> {

	@Override
	protected Collection<Path> getGadgetData() {
		ArrayList<Path> result = new ArrayList<>();
		FileSystem fs = FileSystems.getDefault();
		String ps = getParameterValue("path", "", 0);
		Path cp = null;
		if (ps.isEmpty() == false) {
			cp = fs.getPath(ps);
			try {
				for (Path p : Files.newDirectoryStream(cp)) {
					if (Files.isDirectory(p))
						result.add(p);
				}
			} catch (Exception e) {
				log("", e);
			}
			Collections.sort(result);
			cp = cp.getParent();
		}
		if (cp == null)
			for (Path p : fs.getRootDirectories()) {
				result.add(p);
			}
		else
			result.add(cp);
		return result;
	}
}
