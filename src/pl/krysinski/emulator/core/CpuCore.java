package pl.krysinski.emulator.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import pl.krysinski.emulator.constants.MemoryConstants;
import pl.krysinski.emulator.constants.RegistersConstants;
import pl.krysinski.emulator.constants.StackConstants;

public class CpuCore {
	private static final int GRAPHICS_WIDTH = 64;
	private static final int GRAPHICS_HEIGTH = 32;
	
	private Memory memory;
	private Memory registers;
	private Memory graphics;

	private Stack stack;

	private int programCounter;

	private short opcode;
	private short indexRegister;
	
	private byte delayTimer;
	private byte soundTimer;
	
	private boolean drawFlag = false;

	public CpuCore() {
		initializeCpuCore();
	}

	private void initializeCpuCore() {
		memory = new Memory(MemoryConstants.MEMORY_SIZE);
		registers = new Memory(RegistersConstants.REGISTERS_SIZE);
		graphics = new Memory(GRAPHICS_WIDTH * GRAPHICS_HEIGTH);
		
		stack = new Stack(StackConstants.STACK_SIZE);		

		programCounter = MemoryConstants.MEMORY_PROGRAM_COUNTER_OFFSET;
		opcode = 0x000;
		indexRegister = 0x000;
		
		delayTimer = 0x000;
		soundTimer = 0x000;
	}

	public void loadProgram(String path) {
		File file = new File(path);

		if (file.exists()) {
			try (FileInputStream fis = new FileInputStream(file)) {
				byte[] data = new byte[MemoryConstants.MEMORY_PROGRAM_MAX_SIZE];
				int n = 0;

				while ((n = fis.read(data)) != -1)
					;

				memory.save(MemoryConstants.MEMORY_PROGRAM_COUNTER_OFFSET, data);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void emulateCycle() {
		opcode = memory.loadTwoBytes(programCounter);
		
		if (delayTimer > 0)
			delayTimer--;
		
		if(soundTimer > 0){
			//beep
			soundTimer--;
		}
	}

	public void drawGraphics() {
		if (drawFlag) {
			// draw
		}
	}

}
