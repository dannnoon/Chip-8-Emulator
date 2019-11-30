package pl.krysinski.chip8.domain.command;

import pl.krysinski.chip8.domain.core.ChipSystem;
import pl.krysinski.chip8.domain.opcode.OpCode;
import pl.krysinski.chip8.domain.utilities.TypeUtilities;

public class StoreRegisterValuesAtMemory extends CpuCommand {

    StoreRegisterValuesAtMemory(OpCode opCode) {
        super(opCode);
    }

    @Override
    public void invoke(ChipSystem system) {
        final byte x = TypeUtilities.getXByte(opCode.getRaw());

        for (int i = 0; i < x; i++) {
            final short position = (short) (system.getIndexRegister().getValue() + i);
            final byte valueToSave = system.getRegisters().load(i);
            system.getMemory().save(position, valueToSave);
        }

        system.getProgramCounter().nextInstruction();
    }
}
