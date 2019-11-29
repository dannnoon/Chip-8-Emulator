package pl.krysinski.chip8;

import pl.krysinski.chip8.presentation.EmulatorWindow;

public class Emulator {

	public static void main(String[] args) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				@SuppressWarnings("unused")
				EmulatorWindow emulatorWindow = new EmulatorWindow();
			}
		});
	}
}
