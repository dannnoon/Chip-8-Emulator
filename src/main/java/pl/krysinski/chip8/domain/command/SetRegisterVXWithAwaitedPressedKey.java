package pl.krysinski.chip8.domain.command;

import pl.krysinski.chip8.domain.core.ChipSystem;
import pl.krysinski.chip8.domain.core.KeyStatusManager;
import pl.krysinski.chip8.domain.opcode.OpCode;
import pl.krysinski.chip8.domain.utilities.TypeUtilities;

public class SetRegisterVXWithAwaitedPressedKey extends CpuCommand {

    public SetRegisterVXWithAwaitedPressedKey(OpCode opCode) {
        super(opCode);
    }

    @Override
    public void invoke(ChipSystem system) {
        final KeyStatusManager keys = system.getKeyStatusManager();
        final byte x = TypeUtilities.getXByte(opCode.getRaw());

        for (int i = 0; i < keys.length(); i++) {
            if (keys.isKeyPressed(i)) {
                system.getRegisters().save(x, (byte) i);
                keys.resetKey(i);
                system.getProgramCounter().nextInstruction();
                break;
            }
        }
    }
}
