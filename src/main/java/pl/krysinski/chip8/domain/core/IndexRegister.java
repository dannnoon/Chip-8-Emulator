package pl.krysinski.chip8.domain.core;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IndexRegister {

    private short value = 0x000;

    IndexRegister() {
    }

    public void setValue(int newValue) {
        value = (short) newValue;
    }

    public void fix() {
        value = (short) (value & 0xffff);
    }

    public void add(short addValue) {
        value += addValue;
    }

    public boolean hasCarryBit() {
        short carryBit = (short) ((value >>> 15) & 0x0001);
        return carryBit == 1;
    }
}
