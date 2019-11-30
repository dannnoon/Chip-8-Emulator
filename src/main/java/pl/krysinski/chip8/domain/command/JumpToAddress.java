package pl.krysinski.chip8.domain.command;

import pl.krysinski.chip8.domain.core.ChipSystem;
import pl.krysinski.chip8.domain.opcode.OpCode;
import pl.krysinski.chip8.domain.utilities.TypeUtilities;

class JumpToAddress extends CpuCommand {

    public JumpToAddress(OpCode opCode) {
        super(opCode);
    }

    @Override
    public void invoke(ChipSystem system) {
        final short jumpPosition = TypeUtilities.getNNNShort(opCode.getRaw());
        system.getProgramCounter().setPosition(jumpPosition);
    }
}
