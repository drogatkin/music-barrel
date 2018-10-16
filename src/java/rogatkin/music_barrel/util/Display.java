package rogatkin.music_barrel.util;

public class Display {
	
	public static String textIfZeroOrLess(int val, String text) {
		if (val <= 0)
			return text;
		return String.valueOf(val);
	}
	
	public static String textIfZeroOrLess(int val) {
		return textIfZeroOrLess(val, " ");
	}

}
