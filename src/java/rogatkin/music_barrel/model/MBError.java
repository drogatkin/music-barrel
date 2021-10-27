package rogatkin.music_barrel.model;

public class MBError extends Exception {
	public MBError(String msg) {
		super(msg);
	}

	public MBError(String msg, Throwable reason) {
		super(msg, reason);
	}
}
