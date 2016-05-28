package pl.krysinski.emulator.core;

public class Memory {
	private int memorySize;

	private byte[] memory;

	public Memory(int size) {
		memory = new byte[memorySize];
		clear();
	}

	public void clear() {
		for (int i = 0; i < memorySize; i++) {
			memory[i] = 0x00;
		}
	}

	public void save(int position, byte value) {
		memory[position] = value;
	}

	public void save(int offset, byte[] data) {
		for (int i = 0; i < data.length; i++) {
			save(offset + i, data[i]);
		}
	}

	public byte load(int position) {
		return memory[position];
	}

	public byte[] load(int offset, int size) {
		byte[] data = new byte[size];
		for (int i = 0; i < size; i++) {
			data[i] = load(offset + i);
		}

		return data;
	}
	
	public short loadTwoBytes(int offset) {
		short value = 0;
		
		value = (short) ((load(offset) << 8) | load(offset + 1));
		
		return value;
	}
}
