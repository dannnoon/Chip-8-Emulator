package pl.krysinski.chip8.domain.command;

import pl.krysinski.chip8.domain.core.ChipSystem;
import pl.krysinski.chip8.domain.opcode.OpCode;
import pl.krysinski.chip8.domain.utilities.TypeUtilities;

public class SetIndexRegisterToParameter extends CpuCommand {

    public SetIndexRegisterToParameter(OpCode opCode) {
        super(opCode);
    }

    @Override
    public void invoke(ChipSystem system) {
        final short nnn = TypeUtilities.getNNNShort(opCode.getRaw());
        system.getIndexRegister().setValue(nnn);

        system.getProgramCounter().nextInstruction();
    }
}
