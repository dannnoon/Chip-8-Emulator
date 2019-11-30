package pl.krysinski.chip8.domain.core;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProgramCounter {

    private short position;

    ProgramCounter(short initialPosition) {
        this.position = (short) initialPosition;
    }

    public void nextInstruction() {
        position += 2;
    }

    public void skipNextInstruction() {
        position += 4;
    }
}
