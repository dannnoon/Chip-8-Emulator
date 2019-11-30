package pl.krysinski.chip8.domain.command;

import pl.krysinski.chip8.domain.core.ChipSystem;
import pl.krysinski.chip8.domain.opcode.OpCode;
import pl.krysinski.chip8.domain.utilities.TypeUtilities;

class SetRegisterVXToRegisterVY extends CpuCommand {

    public SetRegisterVXToRegisterVY(OpCode opCode) {
        super(opCode);
    }

    @Override
    public void invoke(ChipSystem system) {
        byte x = TypeUtilities.getXByte(opCode.getRaw());
        byte y = TypeUtilities.getYByte(opCode.getRaw());

        system.getRegisters().save(x, system.getRegisters().load(y));
        system.getProgramCounter().nextInstruction();
    }
}
