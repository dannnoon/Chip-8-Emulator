package pl.krysinski.chip8.domain.opcode;

import pl.krysinski.chip8.domain.utilities.TypeUtilities;

public class OpCodeDecoder {

    public static OpCode decode(short opcode) {
        byte stateA = (byte) ((opcode >>> 12) & 0xF);
        byte stateC = TypeUtilities.getYByte(opcode);
        byte stateD = TypeUtilities.getNByte(opcode);

        return new OpCode(opcode, stateA, stateC, stateD);
    }
}