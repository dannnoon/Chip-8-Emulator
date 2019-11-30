package pl.krysinski.chip8.domain.core;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class Timer {

    private int value = 0;

    public boolean isInProgress() {
        return value > 0;
    }

    public void decrease() {
        if (value > 0) {
            value -= 1;
        }
    }
}
