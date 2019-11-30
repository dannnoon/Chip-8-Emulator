package pl.krysinski.chip8.domain.command;

import pl.krysinski.chip8.domain.core.ChipSystem;
import pl.krysinski.chip8.domain.core.Memory;
import pl.krysinski.chip8.domain.opcode.OpCode;
import pl.krysinski.chip8.domain.utilities.TypeUtilities;

public class StoreBinaryCodedDecimalInMemory extends CpuCommand {

    StoreBinaryCodedDecimalInMemory(OpCode opCode) {
        super(opCode);
    }

    @Override
    public void invoke(ChipSystem system) {
        final Memory registers = system.getRegisters();
        final Memory memory = system.getMemory();

        final byte x = TypeUtilities.getXByte(opCode.getRaw());
        final short indexRegisterValue = system.getIndexRegister().getValue();

        memory.save(indexRegisterValue, (byte) (registers.load(x) / 100));
        memory.save(indexRegisterValue + 1, (byte) ((registers.load(x) / 10) % 10));
        memory.save(indexRegisterValue + 2, (byte) ((registers.load(x) % 100) % 10));

        system.getProgramCounter().nextInstruction();
    }
}
