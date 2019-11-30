package pl.krysinski.chip8.domain.command;

import pl.krysinski.chip8.domain.core.ChipSystem;
import pl.krysinski.chip8.domain.opcode.OpCode;
import pl.krysinski.chip8.domain.utilities.TypeUtilities;

public class JumpToAddressRegisterV0PlusParameter extends CpuCommand {

    public JumpToAddressRegisterV0PlusParameter(OpCode opCode) {
        super(opCode);
    }

    @Override
    public void invoke(ChipSystem system) {
        final short nnn = TypeUtilities.getNNNShort(opCode.getRaw());
        final short address = (short) (nnn + system.getRegisters().load(0x0));
        system.getProgramCounter().setPosition(address);
    }
}
