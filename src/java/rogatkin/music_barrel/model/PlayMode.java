package rogatkin.music_barrel.model;

public enum PlayMode {
	once(0), repeat(1), shuffle(2);
	int mode;

	PlayMode(int mi) {
		mode = mi;
	}

	public int getMode() {
		return mode;
	}
	
	public static PlayMode getMode(int m) {
		for(PlayMode result:values()) {
			if (m == result.getMode())
				return result;
		}
		throw new IllegalArgumentException("No entry for : "+m);
	}
}
