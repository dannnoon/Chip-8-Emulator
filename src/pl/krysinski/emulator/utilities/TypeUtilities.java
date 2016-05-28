package pl.krysinski.emulator.utilities;

public class TypeUtilities {
	public static int intAsByte(int value) {
		return (value &= 0xff);
	}

	public static int intAsShort(int value) {
		return (value &= 0xffff);
	}
	
	public static short byteAsShort(byte value) {
		short v = 0;
		v = (short) (value & 0xff);
		v = (short) (v & 0x00ff);
		
		return v;
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
