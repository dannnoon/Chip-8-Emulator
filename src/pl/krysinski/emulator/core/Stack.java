package pl.krysinski.emulator.core;

import pl.krysinski.emulator.constants.StackConstants;

public class Stack {
	private short[] stack;
	private byte position;

	public Stack(byte size) {
		stack = new short[size];
		position = -1;
	}

	public void pop(short value) {
		if (position < StackConstants.STACK_SIZE)
			stack[++position] = value;
	}

	public short pull() {
		if (position != -1)
			return stack[position--];
		else
			return 0;
	}
}
