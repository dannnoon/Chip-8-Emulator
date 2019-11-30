package pl.krysinski.chip8.domain.command;

import pl.krysinski.chip8.domain.constants.RegistersConstants;
import pl.krysinski.chip8.domain.core.ChipSystem;
import pl.krysinski.chip8.domain.core.IndexRegister;
import pl.krysinski.chip8.domain.core.Memory;
import pl.krysinski.chip8.domain.opcode.OpCode;
import pl.krysinski.chip8.domain.utilities.TypeUtilities;

public class AddRegisterVXToIndexRegister extends CpuCommand {

    AddRegisterVXToIndexRegister(OpCode opCode) {
        super(opCode);
    }

    @Override
    public void invoke(ChipSystem system) {
        final Memory registers = system.getRegisters();
        final IndexRegister indexRegister = system.getIndexRegister();

        final byte x = TypeUtilities.getXByte(opCode.getRaw());
        final short regValue = (short) (registers.load(x) & 0xff);

        indexRegister.add(regValue);

        if (indexRegister.hasCarryBit()) {
            indexRegister.fix();
            registers.save(RegistersConstants.REGISTER_CARRY_FLAG, (byte) 1);
        } else {
            registers.save(RegistersConstants.REGISTER_CARRY_FLAG, (byte) 0);
        }

        system.getProgramCounter().nextInstruction();
    }
}
