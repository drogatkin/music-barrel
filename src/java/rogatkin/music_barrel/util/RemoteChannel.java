package rogatkin.music_barrel.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import jcifs.smb.SmbRandomAccessFile;

public class RemoteChannel extends FileChannel {
	
	ReadableByteChannel channel;
	RandomInputStream ris;
	
	public RemoteChannel(File file) throws IOException {
		//System.out.printf("Opening channel for %s%n", file);
		if (file instanceof RemoteFile == false)
			throw new IllegalArgumentException("Unsupported file type " + file);
		ris  = new RandomInputStream((RemoteFile)file);
		channel = Channels.newChannel(ris);
	}

	@Override
	public void force(boolean arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public FileLock lock(long arg0, long arg1, boolean arg2) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MappedByteBuffer map(MapMode arg0, long arg1, long arg2) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long position() throws IOException {
		return ris.position();
	}

	@Override
	public FileChannel position(long newPosition) throws IOException {
		ris.position(newPosition);
		return this;
	}

	@Override
	public int read(ByteBuffer buffer) throws IOException {
		return channel.read(buffer);
	}

	@Override
	public int read(ByteBuffer buffer, long position) throws IOException {
		ris.position(position);
		return read(buffer);
	}

	@Override
	public long read(ByteBuffer[] arg0, int arg1, int arg2) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long size() throws IOException {
		return ris.length();
	}

	@Override
	public long transferFrom(ReadableByteChannel arg0, long arg1, long arg2) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long transferTo(long arg0, long arg1, WritableByteChannel arg2) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public FileChannel truncate(long arg0) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FileLock tryLock(long arg0, long arg1, boolean arg2) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int write(ByteBuffer arg0) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int write(ByteBuffer arg0, long arg1) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long write(ByteBuffer[] arg0, int arg1, int arg2) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void implCloseChannel() throws IOException {
		channel.close();
		ris.close();
	}
	
	static class RandomInputStream extends InputStream {
		SmbRandomAccessFile raf;
		RandomInputStream(RemoteFile rf) throws IOException {
			raf = new SmbRandomAccessFile(rf.getSmbFile(), "r");
		}
		
		@Override
		public int read() throws IOException {
			return raf.read();
		}

		@Override
		public void close() throws IOException {
			raf.close();
		}

		@Override
		public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
			return raf.read(buffer, byteOffset, byteCount);
		}

		@Override
		public int read(byte[] buffer) throws IOException {
			return raf.read(buffer);
		}

		@Override
		public synchronized void reset() throws IOException {
			raf.seek(0);
		}

		@Override
		public long skip(long byteCount) throws IOException {
			long op = raf.getFilePointer();
			raf.seek(op+byteCount);
			return raf.getFilePointer()-op;
		}
		
		public long position() throws IOException {
			return raf.getFilePointer();
		}
		
		public long position(long newPosition) throws IOException {
			raf.seek(newPosition);
			return raf.getFilePointer();
		}
		
		public long length() throws IOException {
			return raf.length();
		}
	}

}
