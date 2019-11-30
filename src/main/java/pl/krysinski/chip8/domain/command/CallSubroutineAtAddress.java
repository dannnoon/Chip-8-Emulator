package pl.krysinski.chip8.domain.command;

import pl.krysinski.chip8.domain.core.ChipSystem;
import pl.krysinski.chip8.domain.opcode.OpCode;

class CallSubroutineAtAddress extends CpuCommand {

    public CallSubroutineAtAddress(OpCode opCode) {
        super(opCode);
    }

    @Override
    public void invoke(ChipSystem system) {
        system.getProgramCounter().nextInstruction();
        system.getStack().pop(system.getProgramCounter().getPosition());

        final JumpToAddress jumpCommand = new JumpToAddress(opCode);
        jumpCommand.invoke(system);
    }
}
