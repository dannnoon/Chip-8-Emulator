package pl.krysinski.chip8.domain.core;

import lombok.Getter;

@Getter
public class GraphicsManager {

    public static final int DEFAULT_GRAPHICS_WIDTH = 64;
    public static final int DEFAULT_GRAPHICS_HEIGTH = 32;

    final private byte[][] graphics;
    final private int height;
    final private int width;

    GraphicsManager() {
        this.height = DEFAULT_GRAPHICS_HEIGTH;
        this.width = DEFAULT_GRAPHICS_WIDTH;
        graphics = new byte[height][width];
    }

    GraphicsManager(int height, int width) {
        this.height = height;
        this.width = width;
        graphics = new byte[height][width];
    }

    public void clear() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++)
                graphics[i][j] = 0x0;
        }
    }

    public void xor(int heightPosition, int widthPosition, byte value) {
        graphics[heightPosition][widthPosition] ^= value;
    }
}