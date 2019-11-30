package pl.krysinski.chip8.domain.core;

import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import pl.krysinski.chip8.domain.command.CpuCommand;
import pl.krysinski.chip8.domain.command.CpuCommandFactory;
import pl.krysinski.chip8.domain.constants.Fontset;
import pl.krysinski.chip8.domain.constants.KeyConstants;
import pl.krysinski.chip8.domain.constants.MemoryConstants;
import pl.krysinski.chip8.domain.constants.RegistersConstants;
import pl.krysinski.chip8.domain.constants.StackConstants;
import pl.krysinski.chip8.domain.opcode.OpCode;
import pl.krysinski.chip8.domain.opcode.OpCodeDecoder;

public class CpuCore {
	public static final int GRAPHICS_WIDTH = 64;
	public static final int GRAPHICS_HEIGTH = 32;
	public static final int SPRITE_WIDTH = 8;

	private final ChipSystem system;

	public CpuCore() {
		system = new ChipSystem(new Memory(MemoryConstants.MEMORY_SIZE), new Memory(RegistersConstants.REGISTERS_SIZE),
				new Stack(StackConstants.STACK_SIZE), new Timer(), new Timer(),
				new KeyStatusManager(KeyConstants.KeyCodes.length), new DrawFlag(), new GraphicsManager(),
				new ProgramCounter((short) MemoryConstants.MEMORY_PROGRAM_COUNTER_OFFSET), new IndexRegister());

		loadFontsetIntoMemory(Fontset.DEFAULT_FONTSET_ID);
	}

	private void loadFontsetIntoMemory(int type) {
		byte[] fontset = Fontset.getFontset(type);

		System.out.printf("FONTSET SIZE: %d\n", fontset.length);

		system.getMemory().save(MemoryConstants.DEFAULT_FONT_MEMORY_OFFSET, fontset);
	}

	public void loadProgram(String path) {
		File file = new File(path);

		if (file.exists()) {
			try (FileInputStream fis = new FileInputStream(file)) {
				byte[] data = new byte[MemoryConstants.MEMORY_PROGRAM_MAX_SIZE];

				while (fis.read(data) != -1)
					;

				system.getMemory().save(MemoryConstants.MEMORY_PROGRAM_COUNTER_OFFSET, data);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void emulateCycle() {
		final short opcode = system.getMemory().loadTwoBytes(system.getProgramCounter().getPosition());

		final OpCode opCode = OpCodeDecoder.decode(opcode);
		final CpuCommand command = CpuCommandFactory.create(opCode);
		command.invoke(system);

		if (system.getSoundTimer().isInProgress()) {
			Toolkit.getDefaultToolkit().beep();
		}

		system.getDelayTimer().decrease();
		system.getSoundTimer().decrease();
	}

	public byte[][] drawGraphics() {
		if (system.getDrawFlag().isValue()) {
			system.getDrawFlag().setValue(false);
			return system.getGraphicsManager().getGraphics();
		}

		return null;
	}

	public void setKeys(int i) {
		system.getKeyStatusManager().setPressed(i);
	}
}
