package pl.krysinski.chip8.domain.command;

import pl.krysinski.chip8.domain.core.ChipSystem;
import pl.krysinski.chip8.domain.core.Memory;
import pl.krysinski.chip8.domain.opcode.OpCode;
import pl.krysinski.chip8.domain.utilities.TypeUtilities;

public class SetRegisterVXToVXOrVY extends CpuCommand {

    public SetRegisterVXToVXOrVY(OpCode opCode) {
        super(opCode);
    }

    @Override
    public void invoke(ChipSystem system) {
        byte x = TypeUtilities.getXByte(opCode.getRaw());
        byte y = TypeUtilities.getYByte(opCode.getRaw());

        final Memory registers = system.getRegisters();
        final byte orResult = (byte) ((registers.load(x) | registers.load(y)) & 0xffff);
        registers.save(x, orResult);

        system.getProgramCounter().nextInstruction();
    }
}
