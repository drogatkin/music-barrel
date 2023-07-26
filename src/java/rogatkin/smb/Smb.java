package rogatkin.smb;
import jcifs.smb.SmbFile;

public class Smb {

	public static void main(String[] args) {
		explore();

	}

	static void explore() {
		try {
		SmbFile file = new SmbFile("smb://WORKGROUP/");
		SmbFile[] files = file.listFiles();
		if (files != null)
			for (SmbFile smb: files) {
				System.out.printf("Share: %s%n", smb);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
