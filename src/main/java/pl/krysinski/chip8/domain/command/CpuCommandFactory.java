package pl.krysinski.chip8.domain.command;

import pl.krysinski.chip8.domain.opcode.OpCode;

public class CpuCommandFactory {

    public static CpuCommand create(OpCode opCode) {
        switch (opCode.getStateA()) {
        case 0x0:
            if (opCode.getRaw() == 0x00E0) {
                return new ClearScreen(opCode);
            } else if (opCode.getRaw() == 0x00EE) {
                return new ReturnFromSubroutine(opCode);
            }
            break;

        case 0x1:
            return new JumpToAddress(opCode);

        case 0x2:
            return new CallSubroutineAtAddress(opCode);

        case 0x3:
            return new SkipNextIfRegisterVXEqualsParameter(opCode);

        case 0x4:
            return new SkipNextIfRegisterVXNotEqualsParameter(opCode);

        case 0x5:
            return new SkipNextIfRegisterVXEqualsVY(opCode);

        case 0x6:
            return new SetRegisterVXToParameter(opCode);

        case 0x7:
            return new AddParameterToRegisterVX(opCode);

        case 0x8:

            switch (opCode.getStateD()) {
            case 0x0:
                return new SetRegisterVXToRegisterVY(opCode);

            case 0x1:
                return new SetRegisterVXToVXOrVY(opCode);

            case 0x2:
                return new SetRegisterVXToVXAndVY(opCode);

            case 0x3:
                return new SetRegisterVXToVXXorVY(opCode);

            case 0x4:
                return new AddRegisterVYToVX(opCode);

            case 0x5:
                return new SubtractRegisterVYFromVX(opCode);

            case 0x6:
                return new ShiftRegisterVXToTheRight(opCode);

            case 0x7:
                return new SubtractRegisterVXFromVY(opCode);

            case 0xE:
                return new ShiftRegisterVXToTheLeft(opCode);
            }
            break;

        case 0x9:
            return new SkipNextIfRegisterVXNotEqualsVY(opCode);

        case 0xA:
            return new SetIndexRegisterToParameter(opCode);

        case 0xB:
            return new JumpToAddressRegisterV0PlusParameter(opCode);

        case 0xC:
            return new SetRegisterVXToRandomAndParameter(opCode);

        case 0xD:
            return new DrawSpriteAtRegistersVXVY(opCode);

        case 0xE:
            if (opCode.getStateC() == 0x9 && opCode.getStateD() == 0xE) {
                return new SkipNextIfKeyPressed(opCode);
            } else if (opCode.getStateC() == 0xA && opCode.getStateD() == 0x1) {
                return new SkipNextIfKeyNotPressed(opCode);
            }
            break;

        case 0xF:

            if (opCode.getStateC() == 0x0 && opCode.getStateD() == 0x7) {
                return new SetRegisterVXToDelayValue(opCode);
            } else if (opCode.getStateC() == 0x0 && opCode.getStateD() == 0xA) {
                return new SetRegisterVXWithAwaitedPressedKey(opCode);
            } else if (opCode.getStateC() == 0x1 && opCode.getStateD() == 0x5) {
                return new SetDelayToRegisterVX(opCode);
            } else if (opCode.getStateC() == 0x1 && opCode.getStateD() == 0x8) {
                return new SetSoundTimerToRegisterVX(opCode);
            } else if (opCode.getStateC() == 0x1 && opCode.getStateD() == 0xE) {
                return new AddRegisterVXToIndexRegister(opCode);
            } else if (opCode.getStateC() == 0x2 && opCode.getStateD() == 0x9) {
                return new SetIndexRegisterToFontCharacter(opCode);
            } else if (opCode.getStateC() == 0x3 && opCode.getStateD() == 0x3) {
                return new StoreBinaryCodedDecimalInMemory(opCode);
            } else if (opCode.getStateC() == 0x5 && opCode.getStateD() == 0x5) {
                return new StoreRegisterValuesAtMemory(opCode);
            } else if (opCode.getStateC() == 0x6 && opCode.getStateD() == 0x5) {
                return new RecoverRegisterValuesFromMemory(opCode);
            }

            break;
        }

        throw new IllegalArgumentException(String.format("Operation code is invalid: %05X", opCode.getRaw()));
    }
}
