package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import pl.krysinski.emulator.constants.KeyConstants;
import pl.krysinski.emulator.constants.Strings;
import pl.krysinski.emulator.core.CpuCore;

public class EmulatorWindow extends JFrame implements KeyListener {
	private static final long serialVersionUID = -7408689545574276739L;

	private static final int DEFAULT_WIDTH = 800;
	private static final int DEFAULT_HEIGTH = 600;
	
	private static final int DEFAULT_DELAY_TIME = 100;

	private JMenuBar menuBar;
	private JFrame frame;
	private JFileChooser fileChooser;

	private JMenu fileMenu;
	private JMenuItem newMenuItem;
	private JMenuItem openMenuItem;
	private JMenuItem closeMenuItem;

	private JMenu runMenu;
	private JMenuItem emulateMenuItem;
	private JMenuItem stopEmulateMenuItem;
	private JMenuItem resumeEmulateMenuItem;
	private JMenuItem pauseEmulateMenuItem;

	private JMenu editMenu;
	private JMenuItem showCodeMenuItem;

	private JMenu helpMenu;

	private CpuCore chip8;

	private File programFile;

	private boolean isProgramLoaded = false;
	private boolean isEmulationRunning = false;
	private boolean isEmulationPaused = false;
	private boolean isKeyPressed = false;
	
	private int delayTime;

	public EmulatorWindow() {
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGTH);

		setVisible(true);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setTitle(Strings.TITLE);

		frame = this;

		initializeComponents();
		initializeVariables();
	}
	
	private void initializeVariables() {
		setDelayTime(DEFAULT_DELAY_TIME);
	}

	private void initializeComponents() {
		menuBar = new JMenuBar();

		initializeFileMenu();
		initializeRunMenu();
		initializeEditMenu();
		initializeHelpMenu();

		frame.setJMenuBar(menuBar);

		turnOffProgramLoadedOptions();
		updateButtons();
	}

	private void initializeFileMenu() {
		fileMenu = new JMenu(Strings.MENU_FILE);

		newMenuItem = new JMenuItem(Strings.FILE_MENU_ITEM_NEW);
		newMenuItem.addActionListener(onFileMenuNewClick());
		newMenuItem.setEnabled(false);
		fileMenu.add(newMenuItem);

		openMenuItem = new JMenuItem(Strings.FILE_MENU_ITEM_OPEN);
		openMenuItem.addActionListener(onFileMenuOpenClick());
		fileMenu.add(openMenuItem);

		fileMenu.addSeparator();

		closeMenuItem = new JMenuItem(Strings.FILE_MENU_ITEM_CLOSE);
		closeMenuItem.addActionListener(onFileMenuCloseClick());
		fileMenu.add(closeMenuItem);

		menuBar.add(fileMenu);
	}

	private void initializeRunMenu() {
		runMenu = new JMenu(Strings.MENU_RUN);

		emulateMenuItem = new JMenuItem(Strings.RUN_MENU_ITEM_EMUlATE);
		emulateMenuItem.addActionListener(onEditMenuEmulateClick());
		runMenu.add(emulateMenuItem);

		stopEmulateMenuItem = new JMenuItem(Strings.RUN_MENU_ITEM_STOP_EMULATE);
		runMenu.add(stopEmulateMenuItem);

		runMenu.addSeparator();

		resumeEmulateMenuItem = new JMenuItem(Strings.RUN_MENU_ITEM_RESUME_EMULATE);
		runMenu.add(resumeEmulateMenuItem);

		pauseEmulateMenuItem = new JMenuItem(Strings.RUN_MENU_ITEM_PAUSE_EMULATE);
		runMenu.add(pauseEmulateMenuItem);

		menuBar.add(runMenu);
	}

	private void initializeEditMenu() {
		editMenu = new JMenu(Strings.MENU_EDIT);

		showCodeMenuItem = new JMenuItem(Strings.EDIT_MENU_ITEM_SHOW_CODE);
		editMenu.add(showCodeMenuItem);

		menuBar.add(editMenu);
	}

	private void initializeHelpMenu() {
		helpMenu = new JMenu(Strings.MENU_HELP);

		menuBar.add(helpMenu);
	}

	private ActionListener onFileMenuNewClick() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(getParent(), Strings.DIALOG_MESSAGE_OPTION_UNAVAILABLE);
			}
		};
	}

	private ActionListener onFileMenuOpenClick() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				fileChooser = new JFileChooser();
				// fileChooser.addChoosableFileFilter(new
				// FileNameExtensionFilter("Chip-8 program", "c8"));

				int result = fileChooser.showOpenDialog(getParent());
				if (result == fileChooser.APPROVE_OPTION) {
					programFile = fileChooser.getSelectedFile();
					isProgramLoaded = true;
					enableProgramLoadedOptions();
				}
			}
		};
	}

	private ActionListener onFileMenuCloseClick() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				isProgramLoaded = false;
				turnOffProgramLoadedOptions();
			}
		};
	}

	private ActionListener onEditMenuEmulateClick() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!isEmulationRunning) {
					isEmulationRunning = true;

					updateButtons();

					chip8 = new CpuCore();
					chip8.loadProgram(programFile.getAbsolutePath());

					emulate();
				}
			}
		};
	}

	private void emulate() {
		new Runnable() {

			@Override
			public void run() {
				while (isEmulationRunning && !isEmulationPaused) {
					chip8.emulateCycle();
					chip8.drawGraphics();

					if (isKeyPressed) {
						chip8.applyKeys();
						isKeyPressed = false;
					}

					try {
						Thread.sleep(delayTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
	}

	private void turnOffProgramLoadedOptions() {
		if (!isProgramLoaded) {
			closeMenuItem.setEnabled(false);
			editMenu.setEnabled(false);
			runMenu.setEnabled(false);
		}
	}

	private void enableProgramLoadedOptions() {
		if (isProgramLoaded) {
			closeMenuItem.setEnabled(true);
			editMenu.setEnabled(true);
			runMenu.setEnabled(true);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_ESCAPE:
			isEmulationPaused = true;
			updateButtons();
			break;
		}

		if (isEmulationRunning) {
			for (int i = 0; i < KeyConstants.KeyCodes.length; i++) {
				if (e.getKeyCode() == KeyConstants.KeyCodes[i]) {
					chip8.setKeys(i);
					break;
				}
			}
			isKeyPressed = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	private void updateButtons() {
		if (isEmulationPaused && isEmulationRunning) {
			resumeEmulateMenuItem.setEnabled(true);
			emulateMenuItem.setEnabled(false);
			stopEmulateMenuItem.setEnabled(true);
			pauseEmulateMenuItem.setEnabled(false);
		} else if (!isEmulationPaused && isEmulationRunning) {
			resumeEmulateMenuItem.setEnabled(false);
			emulateMenuItem.setEnabled(false);
			stopEmulateMenuItem.setEnabled(true);
			pauseEmulateMenuItem.setEnabled(true);
		} else if (!isEmulationPaused && !isEmulationRunning) {
			resumeEmulateMenuItem.setEnabled(false);
			emulateMenuItem.setEnabled(true);
			stopEmulateMenuItem.setEnabled(false);
			pauseEmulateMenuItem.setEnabled(false);
		} else {
			isEmulationPaused = false;
			resumeEmulateMenuItem.setEnabled(false);
			emulateMenuItem.setEnabled(true);
			stopEmulateMenuItem.setEnabled(false);
			pauseEmulateMenuItem.setEnabled(false);
		}
	}
	
	public void setDelayTime(int delay) {
		delayTime = delay;
	}
	
	public int getDelayTime() {
		return delayTime;
	}

	public static void main(String[] args) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				EmulatorWindow emulatorWindow = new EmulatorWindow();
			}
		});
	}

}
