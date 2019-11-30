package pl.krysinski.chip8.domain.core;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class ChipSystem {
    private final Memory memory;
    private final Memory registers;
    private final Stack stack;
    private final Timer delayTimer;
    private final Timer soundTimer;
    private final KeyStatusManager keyStatusManager;
    private final DrawFlag drawFlag;
    private final GraphicsManager graphicsManager;
    private final ProgramCounter programCounter;
    private final IndexRegister indexRegister;
}