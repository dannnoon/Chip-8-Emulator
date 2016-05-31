package pl.krysinski.emulator.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

import jdk.nashorn.internal.runtime.regexp.RegExp;

import java.awt.*;

import pl.krysinski.emulator.constants.Fontset;
import pl.krysinski.emulator.constants.KeyConstants;
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

	private boolean[] tmpKeys;
	private boolean[] keys;

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

		keys = new boolean[KeyConstants.KeyCodes.length];
		tmpKeys = new boolean[KeyConstants.KeyCodes.length];

		stack = new Stack(StackConstants.STACK_SIZE);

		programCounter = MemoryConstants.MEMORY_PROGRAM_COUNTER_OFFSET;
		opcode = 0x000;
		indexRegister = 0x000;

		delayTimer = 0x000;
		soundTimer = 0x000;

		loadFontsetIntoMemory(Fontset.DEFAULT_FONTSET_ID);
	}

	private void loadFontsetIntoMemory(int type) {
		byte[] fontset = Fontset.getFontset(type);

		memory.save(MemoryConstants.DEFAULT_FONT_MEMORY_OFFSET, fontset);
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
		byte stateC = TypeUtilities.getYByte(opcode);
		byte stateD = TypeUtilities.getNByte(opcode);

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

		case 0xE:
			if (stateC == 0x9 && stateD == 0xE)
				skipInstructionIfVXKeyPressed();
			else if (stateC == 0xA && stateD == 0x1)
				skipInstructionIfVXKeyNotPressed();
			break;

		case 0xF:

			if (stateC == 0x0 && stateD == 0x7)
				setVXToDelayTime();
			else if (stateC == 0x0 && stateD == 0xA)
				setVXByKeyPress();
			else if (stateC == 0x1 && stateD == 0x5)
				setDelayTimeToVX();
			else if (stateC == 0x1 && stateD == 0x8)
				setSoundTimerToVX();
			else if (stateC == 0x1 && stateD == 0xE)
				addVXToIndexRegister();
			else if (stateC == 0x2 && stateD == 0x9)
				setIndexRegisterToVXCharacter();
			else if (stateC == 0x3 && stateD == 0x3)
				storeBinaryCodedDecimalVXInMemory();
			else if (stateC == 0x5 && stateD == 0x5)
				storeRegisterValueAtMemory();
			else if (stateC == 0x6 && stateD == 0x5)
				setRegistersToValueOfMemory();

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
		nextInstruction();
		stack.pop(programCounter);
		jumpToAddress();
	}

	private void skipNextInstructionIfVXEqualsNN() {
		byte x = TypeUtilities.getXByte(opcode);
		byte NN = TypeUtilities.getNNByte(opcode);

		if (registers.load(x) == NN)
			skipInstruction();
		else
			nextInstruction();
	}

	private void skipNextInstrucionIfVXNotEqualsNN() {
		byte x = TypeUtilities.getXByte(opcode);
		byte NN = TypeUtilities.getNNByte(opcode);

		if (registers.load(x) != NN)
			skipInstruction();
		else
			nextInstruction();
	}

	private void skipNextInstructionIfVXEqualsVY() {
		byte x = TypeUtilities.getXByte(opcode);
		byte y = TypeUtilities.getYByte(opcode);

		if (registers.load(x) == registers.load(y))
			skipInstruction();
		else
			nextInstruction();
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

		if (registers.load(x) != registers.load(y))
			skipInstruction();
		else
			nextInstruction();
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

	private void skipInstructionIfVXKeyPressed() {
		byte x = TypeUtilities.getXByte(opcode);

		if (keys[registers.load(x)])
			skipInstruction();
		else
			nextInstruction();
	}

	private void skipInstructionIfVXKeyNotPressed() {
		byte x = TypeUtilities.getXByte(opcode);

		if (!keys[registers.load(x)])
			skipInstruction();
		else
			nextInstruction();
	}

	private void setVXToDelayTime() {
		byte x = TypeUtilities.getXByte(opcode);

		registers.save(x, delayTimer);

		nextInstruction();
	}

	private void setVXByKeyPress() {
		boolean isKeyPressed = false;

		for (int i = 0; i < keys.length; i++)
			if (keys[i] == true) {
				byte x = TypeUtilities.getXByte(opcode);

				registers.save(x, (byte) i);

				isKeyPressed = true;

				nextInstruction();

				break;
			}
	}

	private void setDelayTimeToVX() {
		byte x = TypeUtilities.getXByte(opcode);

		delayTimer = registers.load(x);

		nextInstruction();
	}

	private void setSoundTimerToVX() {
		byte x = TypeUtilities.getXByte(opcode);

		soundTimer = registers.load(x);

		nextInstruction();
	}

	private void addVXToIndexRegister() {
		byte x = TypeUtilities.getXByte(opcode);

		indexRegister += registers.load(x);

		if (isCarryBit(indexRegister)) {
			indexRegister = (short) (indexRegister & 0xff);
			registers.save(RegistersConstants.REGISTER_CARRY_FLAG, (byte) 1);
		} else {
			registers.save(RegistersConstants.REGISTER_CARRY_FLAG, (byte) 0);
		}

		nextInstruction();
	}

	private void setIndexRegisterToVXCharacter() {
		byte x = TypeUtilities.getXByte(opcode);

		indexRegister = registers.loadTwoBytes(x);

		nextInstruction();
	}

	private void storeBinaryCodedDecimalVXInMemory() {
		byte x = TypeUtilities.getXByte(opcode);

		memory.save(indexRegister, (byte) (registers.load(x) / 100));
		memory.save(indexRegister + 1, (byte) ((registers.load(x) / 100) % 10));
		memory.save(indexRegister + 2, (byte) ((registers.load(x) % 100) % 10));

		nextInstruction();
	}

	private void storeRegisterValueAtMemory() {
		byte x = TypeUtilities.getXByte(opcode);

		for (int i = 0; i < x; i++)
			memory.save(indexRegister + i, registers.load(i));

		nextInstruction();
	}

	private void setRegistersToValueOfMemory() {
		byte x = TypeUtilities.getXByte(opcode);

		for (int i = 0; i < x; i++)
			registers.save(i, memory.load(indexRegister + i));

		nextInstruction();
	}

	public void drawGraphics() {
		if (drawFlag) {
			// draw

			drawFlag = false;
		}
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

	public void applyKeys() {
		keys = tmpKeys;

		for (int i = 0; i < tmpKeys.length; i++) {
			tmpKeys[i] = false;
		}
	}

	public void setKeys(int i) {
		tmpKeys[i] = true;
	}
}
