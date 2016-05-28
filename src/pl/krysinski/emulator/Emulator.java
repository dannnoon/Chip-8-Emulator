package pl.krysinski.emulator;

import pl.krysinski.emulator.core.CpuCore;

public class Emulator {

	public static void main(String[] args) {
		CpuCore core = new CpuCore();
		
		core.loadProgram("someProgram");

		while (true) { // main emulator loop

		}

	}

}
