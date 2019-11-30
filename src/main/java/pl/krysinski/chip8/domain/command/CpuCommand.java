package pl.krysinski.chip8.domain.command;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import pl.krysinski.chip8.domain.core.ChipSystem;
import pl.krysinski.chip8.domain.opcode.OpCode;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class CpuCommand {

    protected final OpCode opCode;

    public abstract void invoke(ChipSystem system);
}
