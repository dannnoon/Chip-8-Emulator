package pl.krysinski.chip8.domain.command;

import pl.krysinski.chip8.domain.core.ChipSystem;
import pl.krysinski.chip8.domain.opcode.OpCode;
import pl.krysinski.chip8.domain.utilities.TypeUtilities;

class SetRegisterVXToParameter extends CpuCommand {

    public SetRegisterVXToParameter(OpCode opCode) {
        super(opCode);
    }

    @Override
    public void invoke(ChipSystem system) {
        byte x = TypeUtilities.getXByte(opCode.getRaw());
        byte NN = TypeUtilities.getNNByte(opCode.getRaw());

        system.getRegisters().save(x, NN);
        system.getProgramCounter().nextInstruction();
    }
}
