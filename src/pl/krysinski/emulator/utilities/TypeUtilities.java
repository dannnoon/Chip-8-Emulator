package pl.krysinski.emulator.utilities;

public class TypeUtilities {
	public static int asByte(int value) {
		return (value &= 0xff);
	}
	
	public static int asShort(int value) {
		return (value &= 0xffff);
	}
}
