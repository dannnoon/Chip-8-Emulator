package pl.krysinski.emulator.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;
import java.awt.*;

import pl.krysinski.emulator.constants.MemoryConstants;
import pl.krysinski.emulator.constants.RegistersConstants;
import pl.krysinski.emulator.constants.StackConstants;
import pl.krysinski.emulator.utilities.TypeUtilities;

public class CpuCore {
	private static final int GRAPHICS_WIDTH = 64;
	private static final int GRAPHICS_HEIGTH = 32;
	private static final int SPRITE_WIDTH = 8;

	private Memory memory;
	private Memory registers;

	private byte[][] graphics;

	private Stack stack;

	private short programCounter;

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
		graphics = new byte[GRAPHICS_HEIGTH][GRAPHICS_WIDTH];

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

				while (fis.read(data) != -1)
					;

				memory.save(MemoryConstants.MEMORY_PROGRAM_COUNTER_OFFSET, data);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void emulateCycle() {
		opcode = memory.loadTwoBytes(programCounter);

		switchState();

		if (delayTimer > 0)
			delayTimer--;

		if (soundTimer > 0) {
			Toolkit.getDefaultToolkit().beep();
			soundTimer--;
		}
	}

	public void switchState() {
		byte stateA = (byte) ((opcode >>> 12) & 0xF);

		switch (stateA) {
		case 0x0:
			if (opcode == 0x00E0) {
				clearScreen();
			} else if (opcode == 0x00EE) {
				returnFromSubroutine();
			}
			break;

		case 0x1:
			jumpToAddress();
			break;

		case 0x2:
			callSubroutineAtAddress();
			break;

		case 0x3:
			skipNextInstructionIfVXEqualsNN();
			break;

		case 0x4:
			skipNextInstrucionIfVXNotEqualsNN();
			break;

		case 0x5:
			skipNextInstructionIfVXEqualsVY();
			break;

		case 0x6:
			setVXToNN();
			break;

		case 0x7:
			addNNToVX();
			break;

		case 0x8:
			byte stateD = TypeUtilities.getNByte(opcode);

			switch (stateD) {
			case 0x0:
				setVXToVY();
				break;

			case 0x1:
				setVXToVXORVY();
				break;

			case 0x2:
				setVXToVXANDVY();
				break;

			case 0x3:
				setVXToVXXORVY();
				break;

			case 0x4:
				addVYtoVX();
				break;

			case 0x5:
				subtractVYfromVX();
				break;

			case 0x6:
				shiftVXByOneToTheRight();
				break;

			case 0x7:
				subtractVXfromVY();
				break;

			case 0xE:
				shiftVXByOneToTheLeft();
				break;
			}
			break;

		case 0x9:
			skipNextInstructionIfVXNotEqualsVY();
			break;

		case 0xA:
			setIndexRegisterToNNN();
			break;

		case 0xB:
			jumpToAddressNNNPlusV0();
			break;

		case 0xC:
			setVXToRandomNumberANDNN();
			break;

		case 0xD:
			drawSpriteAtVXVYWithNRows();
			break;
		}
	}

	private void clearScreen() {
		clearGraphics();
		drawFlag = true;

		nextInstruction();
	}

	private void returnFromSubroutine() {
		programCounter = stack.pull();
	}

	private void jumpToAddress() {
		programCounter = TypeUtilities.getNNNShort(opcode);
	}

	private void callSubroutineAtAddress() {
		programCounter += 2;
		stack.pop(programCounter);
		jumpToAddress();
	}

	private void skipNextInstructionIfVXEqualsNN() {
		byte x = TypeUtilities.getXByte(opcode);
		byte NN = TypeUtilities.getNNByte(opcode);

		if (registers.load(x) == NN)
			skipInstruction();
	}

	private void skipNextInstrucionIfVXNotEqualsNN() {
		byte x = TypeUtilities.getXByte(opcode);
		byte NN = TypeUtilities.getNNByte(opcode);

		if (registers.load(x) != NN)
			skipInstruction();
	}

	private void skipNextInstructionIfVXEqualsVY() {
		byte x = TypeUtilities.getXByte(opcode);
		byte y = TypeUtilities.getYByte(opcode);

		if (registers.load(x) == registers.load(y))
			skipInstruction();
	}

	private void setVXToNN() {
		byte x = TypeUtilities.getXByte(opcode);
		byte NN = TypeUtilities.getNNByte(opcode);

		registers.save(x, NN);
		nextInstruction();
	}

	private void addNNToVX() {
		byte x = TypeUtilities.getXByte(opcode);
		byte NN = TypeUtilities.getNNByte(opcode);

		registers.save(x, (byte) (registers.load(x) + NN));

		nextInstruction();
	}

	private void setVXToVY() {
		byte x = TypeUtilities.getXByte(opcode);
		byte y = TypeUtilities.getYByte(opcode);

		registers.save(x, registers.load(y));

		nextInstruction();
	}

	private void setVXToVXORVY() {
		byte x = TypeUtilities.getXByte(opcode);
		byte y = TypeUtilities.getYByte(opcode);

		registers.save(x, (byte) (registers.load(x) | registers.load(y)));

		nextInstruction();
	}

	private void setVXToVXANDVY() {
		byte x = TypeUtilities.getXByte(opcode);
		byte y = TypeUtilities.getYByte(opcode);

		registers.save(x, (byte) (registers.load(x) & registers.load(y)));

		nextInstruction();
	}

	private void setVXToVXXORVY() {
		byte x = TypeUtilities.getXByte(opcode);
		byte y = TypeUtilities.getYByte(opcode);

		registers.save(x, (byte) (registers.load(x) ^ registers.load(y)));

		nextInstruction();
	}

	private void addVYtoVX() {
		short result = 0;

		byte x = TypeUtilities.getXByte(opcode);
		byte y = TypeUtilities.getYByte(opcode);

		short xv = TypeUtilities.byteAsShort(registers.load(x));
		short yv = TypeUtilities.byteAsShort(registers.load(y));

		result = (short) (xv + yv);

		if (isCarryBit(result))
			registers.save(RegistersConstants.REGISTER_CARRY_FLAG, (byte) 1);
		else
			registers.save(RegistersConstants.REGISTER_CARRY_FLAG, (byte) 0);

		nextInstruction();
	}

	private void subtractVYfromVX() {
		short result = 0;

		byte x = TypeUtilities.getXByte(opcode);
		byte y = TypeUtilities.getYByte(opcode);

		short xv = TypeUtilities.byteAsShort(registers.load(x));
		short yv = TypeUtilities.byteAsShort(registers.load(y));

		result = (short) (xv - yv);

		if (isSignBit(result))
			registers.save(RegistersConstants.REGISTER_CARRY_FLAG, (byte) 0);
		else
			registers.save(RegistersConstants.REGISTER_CARRY_FLAG, (byte) 1);

		nextInstruction();
	}

	private void shiftVXByOneToTheRight() {
		byte x = TypeUtilities.getXByte(opcode);

		byte leastBit = (byte) (registers.load(x) & 0x01);

		registers.save(RegistersConstants.REGISTER_CARRY_FLAG, leastBit);

		registers.save(x, (byte) (registers.load(x) >>> 1));

		nextInstruction();
	}

	private void subtractVXfromVY() {
		short result = 0;

		byte x = TypeUtilities.getXByte(opcode);
		byte y = TypeUtilities.getYByte(opcode);

		short xv = TypeUtilities.byteAsShort(registers.load(x));
		short yv = TypeUtilities.byteAsShort(registers.load(y));

		result = (short) (yv - xv);

		if (isSignBit(result))
			registers.save(RegistersConstants.REGISTER_CARRY_FLAG, (byte) 0);
		else
			registers.save(RegistersConstants.REGISTER_CARRY_FLAG, (byte) 1);

		nextInstruction();
	}

	private void shiftVXByOneToTheLeft() {
		byte x = TypeUtilities.getXByte(opcode);

		byte mostBit = (byte) ((registers.load(x) & 0x80) >> 7);

		registers.save(RegistersConstants.REGISTER_CARRY_FLAG, mostBit);

		registers.save(x, (byte) (registers.load(x) << 1));

		nextInstruction();
	}

	private void skipNextInstructionIfVXNotEqualsVY() {
		byte x = TypeUtilities.getXByte(opcode);
		byte y = TypeUtilities.getYByte(opcode);

		if (registers.load(x) != registers.load(y)) {
			skipInstruction();
		}
	}

	private void setIndexRegisterToNNN() {
		short NNN = TypeUtilities.getNNNShort(opcode);
		indexRegister = NNN;

		nextInstruction();
	}

	private void jumpToAddressNNNPlusV0() {
		short NNN = TypeUtilities.getNNNShort(opcode);

		programCounter = (short) (NNN + registers.load(0x0));
	}

	private void setVXToRandomNumberANDNN() {
		byte NN = TypeUtilities.getNNByte(opcode);
		byte x = TypeUtilities.getXByte(opcode);

		Random random = new Random();
		byte number = (byte) (random.nextInt() & NN);

		registers.save(x, number);

		nextInstruction();
	}

	private void drawSpriteAtVXVYWithNRows() {
		byte x = TypeUtilities.getXByte(opcode);
		byte y = TypeUtilities.getYByte(opcode);
		byte N = TypeUtilities.getNByte(opcode);

		byte posX = registers.load(x);
		byte posY = registers.load(y);

		byte[] sprite = new byte[N];
		sprite = memory.load(indexRegister, N);

		int jj = 0;
		int ii;

		registers.save(RegistersConstants.REGISTER_CARRY_FLAG, (byte) 0);

		for (int j = posY; (j < (posY + N) % GRAPHICS_HEIGTH)
				|| (j + 1 < (posY + N) % GRAPHICS_HEIGTH); j = (j + 1) % GRAPHICS_HEIGTH) {
			ii = 7;

			for (int i = posX; (i < (posX + SPRITE_WIDTH) % GRAPHICS_WIDTH)
					|| (i + 1 < (posX + SPRITE_WIDTH) % GRAPHICS_WIDTH); i = (i + 1) % GRAPHICS_WIDTH) {
				byte tmp = (byte) ((sprite[jj] >>> ii) & 0x1);

				if (tmp == 1) {
					registers.save(RegistersConstants.REGISTER_CARRY_FLAG, (byte) 1);
					graphics[j][i] ^= tmp;
				}

				ii--;
			}
			jj++;
		}

		drawFlag = true;

		nextInstruction();
	}

	public void drawGraphics() {
		if (drawFlag) {
			// draw
		}

		drawFlag = false;
	}

	private void clearGraphics() {
		for (int i = 0; i < GRAPHICS_HEIGTH; i++) {
			for (int j = 0; j < GRAPHICS_WIDTH; j++)
				graphics[i][j] = 0x0;
		}
	}

	private void nextInstruction() {
		programCounter += 2;
	}

	private void skipInstruction() {
		programCounter += 4;
	}

	private boolean isCarryBit(short value) {
		short carryBit = (short) ((value >>> 8) & 0x0001);

		return carryBit == 1 ? true : false;
	}

	private boolean isSignBit(short value) {
		return value < 0 ? false : true;
	}

}
