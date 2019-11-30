package pl.krysinski.chip8.domain.command;

import pl.krysinski.chip8.domain.constants.RegistersConstants;
import pl.krysinski.chip8.domain.core.ChipSystem;
import pl.krysinski.chip8.domain.core.Memory;
import pl.krysinski.chip8.domain.opcode.OpCode;
import pl.krysinski.chip8.domain.utilities.TypeUtilities;

public class AddRegisterVYToVX extends CpuCommand {

    public AddRegisterVYToVX(OpCode opCode) {
        super(opCode);
    }

    @Override
    public void invoke(ChipSystem system) {
        final byte x = TypeUtilities.getXByte(opCode.getRaw());
        final byte y = TypeUtilities.getYByte(opCode.getRaw());

        final Memory registers = system.getRegisters();
        final short xv = TypeUtilities.byteAsShort(registers.load(x));
        final short yv = TypeUtilities.byteAsShort(registers.load(y));

        final short result = (short) (xv + yv);
        registers.save(x, (byte) result);

        if (TypeUtilities.hasCarryBite(result)) {
            registers.save(RegistersConstants.REGISTER_CARRY_FLAG, (byte) 1);
        } else {
            registers.save(RegistersConstants.REGISTER_CARRY_FLAG, (byte) 0);
        }

        system.getProgramCounter().nextInstruction();
    }
}
