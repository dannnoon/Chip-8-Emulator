package pl.krysinski.chip8.domain.command;

import pl.krysinski.chip8.domain.core.ChipSystem;
import pl.krysinski.chip8.domain.opcode.OpCode;

class ClearScreen extends CpuCommand {

    public ClearScreen(OpCode opCode) {
        super(opCode);
    }

    @Override
    public void invoke(ChipSystem system) {
        system.getGraphicsManager().clear();
        system.getDrawFlag().setValue(true);
        system.getProgramCounter().nextInstruction();
    }
}
