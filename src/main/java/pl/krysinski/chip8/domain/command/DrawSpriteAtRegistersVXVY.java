package pl.krysinski.chip8.domain.command;

import pl.krysinski.chip8.domain.constants.RegistersConstants;
import pl.krysinski.chip8.domain.core.ChipSystem;
import pl.krysinski.chip8.domain.core.GraphicsManager;
import pl.krysinski.chip8.domain.core.Memory;
import pl.krysinski.chip8.domain.opcode.OpCode;
import pl.krysinski.chip8.domain.utilities.TypeUtilities;

public class DrawSpriteAtRegistersVXVY extends CpuCommand {

    public DrawSpriteAtRegistersVXVY(OpCode opCode) {
        super(opCode);
    }

    @Override
    public void invoke(ChipSystem system) {
        final Memory registers = system.getRegisters();
        final GraphicsManager graphicsManager = system.getGraphicsManager();

        final byte x = TypeUtilities.getXByte(opCode.getRaw());
        final byte y = TypeUtilities.getYByte(opCode.getRaw());
        final byte N = TypeUtilities.getNByte(opCode.getRaw());

        final byte posX = (byte) (registers.load(x) % graphicsManager.getWidth());
        final byte posY = (byte) (registers.load(y) % graphicsManager.getHeight());

        final byte[] sprite = system.getMemory().load(system.getIndexRegister().getValue(), N);

        int jj = 0;
        int ii;

        registers.save(RegistersConstants.REGISTER_CARRY_FLAG, (byte) 0);

        for (int j = posY; jj < N; j = (j + 1) % graphicsManager.getHeight()) {
            ii = 7;

            System.out.printf("\n");

            for (int i = posX; ii >= 0; i = (i + 1) % graphicsManager.getWidth()) {
                byte tmp = (byte) ((sprite[jj] >>> ii) & 0x1);

                if (tmp == 1) {

                    if (graphicsManager.getGraphics()[j][i] == 1) {
                        registers.save(RegistersConstants.REGISTER_CARRY_FLAG, (byte) 1);
                    }

                    graphicsManager.xor(j, i, tmp);
                    System.out.printf("X");
                } else {
                    System.out.printf(" ");
                }

                ii--;
            }

            ii = 7;
            jj++;
        }

        system.getDrawFlag().setValue(true);
        system.getProgramCounter().nextInstruction();
    }
}
