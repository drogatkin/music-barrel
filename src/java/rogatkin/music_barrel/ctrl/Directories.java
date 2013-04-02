package rogatkin.music_barrel.ctrl;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

import rogatkin.music_barrel.model.MBModel;

import com.beegman.webbee.block.Gadget;

public class Directories extends Gadget<Collection<Path>, MBModel> {

	@Override
	protected Collection<Path> getGadgetData() {
		ArrayList<Path> result = new ArrayList<>();
		FileSystem fs = FileSystems.getDefault();
		String ps = getParameterValue("path", "", 0);
		if (ps.isEmpty()) {
			for (Path p : fs.getRootDirectories()) {
				result.add(p);
			}
		} else {
			Path cp = fs.getPath(ps);
			result.add(cp.getParent());
			try {
				for (Path p : Files.newDirectoryStream(cp)) {
					if (Files.isDirectory(p))
						result.add(p);
				}
			} catch (Exception e) {
				log("", e);
			}
		}
		return result;
	}
}
