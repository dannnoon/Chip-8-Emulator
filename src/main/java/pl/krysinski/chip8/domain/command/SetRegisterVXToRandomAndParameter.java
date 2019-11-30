package pl.krysinski.chip8.domain.command;

import java.util.Random;

import pl.krysinski.chip8.domain.core.ChipSystem;
import pl.krysinski.chip8.domain.opcode.OpCode;
import pl.krysinski.chip8.domain.utilities.TypeUtilities;

public class SetRegisterVXToRandomAndParameter extends CpuCommand {

    public SetRegisterVXToRandomAndParameter(OpCode opCode) {
        super(opCode);
    }

    @Override
    public void invoke(ChipSystem system) {
        final byte nn = TypeUtilities.getNNByte(opCode.getRaw());
        final byte x = TypeUtilities.getXByte(opCode.getRaw());

        final Random random = new Random();
        final byte number = (byte) (random.nextInt() & nn);
        system.getRegisters().save(x, number);

        system.getProgramCounter().nextInstruction();
    }
}
