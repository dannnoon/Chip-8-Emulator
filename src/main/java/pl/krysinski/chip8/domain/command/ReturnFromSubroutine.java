package pl.krysinski.chip8.domain.command;

import pl.krysinski.chip8.domain.core.ChipSystem;
import pl.krysinski.chip8.domain.opcode.OpCode;

class ReturnFromSubroutine extends CpuCommand {

    public ReturnFromSubroutine(OpCode opCode) {
        super(opCode);
    }

    @Override
    public void invoke(ChipSystem system) {
        system.getProgramCounter().setPosition(system.getStack().pull());
    }
}
