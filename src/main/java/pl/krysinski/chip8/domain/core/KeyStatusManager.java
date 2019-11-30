package pl.krysinski.chip8.domain.core;

import lombok.Setter;

@Setter
public class KeyStatusManager {

    private boolean[] keys;

    KeyStatusManager(int keysCount) {
        keys = new boolean[keysCount];
    }

    public boolean isKeyPressed(int keyNumber) {
        return keys[keyNumber];
    }

    public void setPressed(int keyNumber) {
        keys[keyNumber] = true;
    }

    public void resetKey(int keyNumber) {
        keys[keyNumber] = false;
    }

    public int length() {
        return keys.length;
    }
}
