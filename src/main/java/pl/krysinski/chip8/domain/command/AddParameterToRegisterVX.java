package pl.krysinski.chip8.domain.command;

import pl.krysinski.chip8.domain.core.ChipSystem;
import pl.krysinski.chip8.domain.opcode.OpCode;
import pl.krysinski.chip8.domain.utilities.TypeUtilities;

class AddParameterToRegisterVX extends CpuCommand {

    public AddParameterToRegisterVX(OpCode opCode) {
        super(opCode);
    }

    @Override
    public void invoke(ChipSystem system) {
        byte x = TypeUtilities.getXByte(opCode.getRaw());
        byte NN = TypeUtilities.getNNByte(opCode.getRaw());

        system.getRegisters().save(x, (byte) (system.getRegisters().load(x) + NN));
        system.getProgramCounter().nextInstruction();
    }
}
