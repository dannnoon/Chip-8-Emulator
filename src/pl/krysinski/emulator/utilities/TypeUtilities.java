package pl.krysinski.emulator.utilities;

public class TypeUtilities {
	public static int asByte(int value) {
		return (value &= 0xff);
	}

	public static int asShort(int value) {
		return (value &= 0xffff);
	}

	public static short getNNNShort(short value) {
		return (short) (value & 0x0FFF);
	}

	public static byte getNNByte(short value) {
		return (byte) (value & 0xFF);
	}

	public static byte getNByte(short value) {
		return (byte) (value & 0xF);
	}

	public static byte getXByte(short value) {
		return (byte) ((value >>> 8) & 0xF);
	}

	public static byte getYByte(short value) {
		return (byte) ((value >>> 4) & 0xF);
	}
}
