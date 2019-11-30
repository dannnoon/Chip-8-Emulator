package pl.krysinski.chip8.domain.command;

import pl.krysinski.chip8.domain.constants.RegistersConstants;
import pl.krysinski.chip8.domain.core.ChipSystem;
import pl.krysinski.chip8.domain.core.Memory;
import pl.krysinski.chip8.domain.opcode.OpCode;
import pl.krysinski.chip8.domain.utilities.TypeUtilities;

public class ShiftRegisterVXToTheRight extends CpuCommand {

    public ShiftRegisterVXToTheRight(OpCode opCode) {
        super(opCode);
    }

    @Override
    public void invoke(ChipSystem system) {
        final Memory registers = system.getRegisters();

        final byte x = TypeUtilities.getXByte(opCode.getRaw());
        final byte leastBit = (byte) (registers.load(x) & 0x01);

        registers.save(RegistersConstants.REGISTER_CARRY_FLAG, leastBit);
        registers.save(x, (byte) (registers.load(x) >>> 1));

        system.getProgramCounter().nextInstruction();
    }
}