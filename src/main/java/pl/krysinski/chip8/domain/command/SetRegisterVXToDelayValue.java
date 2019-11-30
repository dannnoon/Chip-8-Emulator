package pl.krysinski.chip8.domain.command;

import pl.krysinski.chip8.domain.core.ChipSystem;
import pl.krysinski.chip8.domain.opcode.OpCode;
import pl.krysinski.chip8.domain.utilities.TypeUtilities;

public class SetRegisterVXToDelayValue extends CpuCommand {

    public SetRegisterVXToDelayValue(OpCode opCode) {
        super(opCode);
    }

    @Override
    public void invoke(ChipSystem system) {
        final byte x = TypeUtilities.getXByte(opCode.getRaw());
        system.getRegisters().save(x, (byte) system.getDelayTimer().getValue());
        system.getProgramCounter().nextInstruction();
    }
}
