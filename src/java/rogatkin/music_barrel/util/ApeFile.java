package rogatkin.music_barrel.util;

import davaguine.jmac.tools.File;
import jcifs.smb.SmbRandomAccessFile;
import java.io.IOException;

public class ApeFile extends File {
	SmbRandomAccessFile raf;
	String name;
	private long markPosition = -1;
	protected byte[] buf = new byte[8];
	
	public ApeFile( java.io.File file ) throws IOException {
		if (file instanceof RemoteFile == false)
			throw new IllegalArgumentException("Only remote file can be specified in the constructor");
	raf = new SmbRandomAccessFile(((RemoteFile)file).getSmbFile(), "r");
	name = file.getName();
}
	
	@Override
	public void close() throws IOException {
		raf.close();
	}

	@Override
	public long getFilePointer() throws IOException {
		return raf.getFilePointer();
	}

	@Override
	public String getFilename() {
		return name;
	}

	@Override
	public boolean isLocal() {
		return false;
	}

	@Override
	public long length() throws IOException {
		return raf.length();
	}

	@Override
	public void mark(int limit) throws IOException {
		markPosition = raf.getFilePointer();
	}

	@Override
	public int read() throws IOException {
		return raf.read();
	}

	@Override
	public int read(byte[] arg0) throws IOException {
		return raf.read(arg0);
	}

	@Override
	public int read(byte[] buf, int off, int len) throws IOException {
		return raf.read(buf, off, len);
	}

	@Override
	public boolean readBoolean() throws IOException {
		return raf.readBoolean();
	}

	@Override
	public byte readByte() throws IOException {
		return raf.readByte();
	}

	@Override
	public char readChar() throws IOException {
		return raf.readChar();
	}

	@Override
	public double readDouble() throws IOException {
		return raf.readDouble();
	}

	@Override
	public float readFloat() throws IOException {
		return raf.readFloat();
	}

	@Override
	public void readFully(byte[] buf) throws IOException {
		readFully(buf, 0, buf.length);
	}

	@Override
	public void readFully(byte[] buf, int off, int len) throws IOException {
		int c=0;
		while (c < len) {
			int i = raf.read(buf, off+c, len - c);
			if (i > 0)
				c += i;
			else
				throw new IOException("EOF");
		}
	}

	@Override
	public int readInt() throws IOException {
		readFully(buf, 0, 4);
		return (((buf[0] & 0xff) << 24) | ((buf[1] & 0xff) << 16) |
				((buf[2] & 0xff) << 8) | (buf[3] & 0xff));
	}

	@Override
	public String readLine() throws IOException {
		return raf.readLine();
	}

	@Override
	public long readLong() throws IOException {
		readFully(buf, 0, 8);
		return (((long)(buf[0] & 0xff) << 56) |
				((long)(buf[1] & 0xff) << 48) |
				((long)(buf[2] & 0xff) << 40) |
				((long)(buf[3] & 0xff) << 32) |
				((long)(buf[4] & 0xff) << 24) |
				((long)(buf[5] & 0xff) << 16) |
				((long)(buf[6] & 0xff) <<  8) |
				((long)(buf[7] & 0xff)));
	}

	@Override
	public short readShort() throws IOException {
		readFully(buf, 0, 2);
		return (short)(((buf[0]&255) << 8) | (buf[1] & 0xff));
	}

	@Override
	public String readUTF() throws IOException {
		return raf.readUTF();
	}

	@Override
	public int readUnsignedByte() throws IOException {
		return raf.readUnsignedByte();
	}

	@Override
	public int readUnsignedShort() throws IOException {
		readFully(buf, 0, 2);
		return (short)(((buf[0] & 255) << 8) | (buf[1] & 0xff));
	}

	@Override
	public void reset() throws IOException {
		if (markPosition >= 0)
            raf.seek(markPosition);
	}

	@Override
	public void seek(long pos) throws IOException {
		raf.seek(pos);
	}

	@Override
	public void setLength(long newLength) throws IOException {
		raf.setLength(newLength);
	}

	@Override
	public int skipBytes(int n) throws IOException {
		return raf.skipBytes(n);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		raf.write(b, off, len);
	}
}
