package pl.krysinski.chip8.domain.core;

public class Memory {

	private final byte[] memory;

	Memory(int size) {
		memory = new byte[size];
		clear();
	}

	public void clear() {
		for (int i = 0; i < memory.length; i++) {
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
		return (short) (((load(offset) << 8) | (load(offset + 1) & 0xff)));
	}
}
