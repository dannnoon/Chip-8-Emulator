package pl.krysinski.chip8.domain.command;

import pl.krysinski.chip8.domain.core.ChipSystem;
import pl.krysinski.chip8.domain.core.KeyStatusManager;
import pl.krysinski.chip8.domain.core.Memory;
import pl.krysinski.chip8.domain.opcode.OpCode;
import pl.krysinski.chip8.domain.utilities.TypeUtilities;

public class SkipNextIfKeyNotPressed extends CpuCommand {

    public SkipNextIfKeyNotPressed(OpCode opCode) {
        super(opCode);
    }

    @Override
    public void invoke(ChipSystem system) {
        final KeyStatusManager keyStatusManager = system.getKeyStatusManager();
        final Memory registers = system.getRegisters();

        final byte x = TypeUtilities.getXByte(opCode.getRaw());

        if (!keyStatusManager.isKeyPressed(registers.load(x))) {
            system.getProgramCounter().skipNextInstruction();
        } else {
            keyStatusManager.resetKey(registers.load(x));
            system.getProgramCounter().nextInstruction();
        }
    }
}
