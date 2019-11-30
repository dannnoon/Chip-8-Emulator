package pl.krysinski.chip8.domain.command;

import pl.krysinski.chip8.domain.core.ChipSystem;
import pl.krysinski.chip8.domain.opcode.OpCode;
import pl.krysinski.chip8.domain.utilities.TypeUtilities;

public class RecoverRegisterValuesFromMemory extends CpuCommand {

    RecoverRegisterValuesFromMemory(OpCode opCode) {
        super(opCode);
    }

    @Override
    public void invoke(ChipSystem system) {
        final byte x = TypeUtilities.getXByte(opCode.getRaw());

        for (byte i = 0; i < x; i++) {
            final short position = (short) (system.getIndexRegister().getValue() + i);
            final byte valueToRecover = system.getMemory().load(position);
            system.getRegisters().save(i, valueToRecover);
        }

        system.getProgramCounter().nextInstruction();
    }
}
