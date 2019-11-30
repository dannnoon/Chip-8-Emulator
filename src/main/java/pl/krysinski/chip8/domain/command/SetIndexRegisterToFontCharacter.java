package pl.krysinski.chip8.domain.command;

import pl.krysinski.chip8.domain.constants.Fontset;
import pl.krysinski.chip8.domain.core.ChipSystem;
import pl.krysinski.chip8.domain.opcode.OpCode;
import pl.krysinski.chip8.domain.utilities.TypeUtilities;

public class SetIndexRegisterToFontCharacter extends CpuCommand {

    SetIndexRegisterToFontCharacter(OpCode opCode) {
        super(opCode);
    }

    @Override
    public void invoke(ChipSystem system) {
        final byte x = TypeUtilities.getXByte(opCode.getRaw());
        final short fontCharacter = (short) (system.getRegisters().load(x) * Fontset.DEFAULT_FONT_HEIGTH);
        system.getIndexRegister().setValue(fontCharacter);
        system.getProgramCounter().nextInstruction();
    }
}
