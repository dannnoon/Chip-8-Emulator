package gui;

import java.awt.BorderLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import com.sun.corba.se.impl.oa.poa.ActiveObjectMap.Key;

import pl.krysinski.emulator.constants.KeyConstants;
import pl.krysinski.emulator.constants.Strings;
import pl.krysinski.emulator.core.CpuCore;

public class EmulatorWindow extends JFrame {
	private static final long serialVersionUID = -7408689545574276739L;

	private static final int DEFAULT_WIDTH = 800;
	private static final int DEFAULT_HEIGTH = 600;

	private static final int DEFAULT_DELAY_TIME = 5;

	private short[] program;

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

	RenderPanel renderPanel;

	private boolean isProgramLoaded = false;
	private boolean isEmulationRunning = false;
	private boolean isEmulationPaused = false;
	private boolean isKeyPressed = false;

	private int delayTime;

	private ThreadPoolExecutor executor;

	public EmulatorWindow() {
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGTH);

		setVisible(true);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setTitle(Strings.TITLE);

		setLayout(new BorderLayout());

		KeyboardFocusManager keyboardManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		keyboardManager.addKeyEventDispatcher(KeyEventDispatcher);

		frame = this;

		initializeComponents();
		initializeVariables();

	}

	private void initializeExecutor() {
		int processors = Runtime.getRuntime().availableProcessors();
		executor = new ThreadPoolExecutor(processors, processors + 2, 10, TimeUnit.SECONDS,
				new LinkedBlockingQueue<>());
	}

	private void shutdownExecutor() {
		executor.shutdownNow();
		executor = null;
	}

	private boolean executeThread(Runnable runnable) {
		if (executor != null) {
			executor.execute(runnable);

			return true;
		} else
			return false;
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
		stopEmulateMenuItem.addActionListener(onRunMenuStopEmulateClick());
		runMenu.add(stopEmulateMenuItem);

		runMenu.addSeparator();

		resumeEmulateMenuItem = new JMenuItem(Strings.RUN_MENU_ITEM_RESUME_EMULATE);
		resumeEmulateMenuItem.addActionListener(onRunMenuResumEmulateClick());
		runMenu.add(resumeEmulateMenuItem);

		pauseEmulateMenuItem = new JMenuItem(Strings.RUN_MENU_ITEM_PAUSE_EMULATE);
		pauseEmulateMenuItem.addActionListener(onRunMenuPauseEmulateClick());
		runMenu.add(pauseEmulateMenuItem);

		menuBar.add(runMenu);
	}

	private void initializeEditMenu() {
		editMenu = new JMenu(Strings.MENU_EDIT);

		showCodeMenuItem = new JMenuItem(Strings.EDIT_MENU_ITEM_SHOW_CODE);
		showCodeMenuItem.addActionListener(onEditMenuShowCodeClick());
		editMenu.add(showCodeMenuItem);

		menuBar.add(editMenu);
	}

	private void initializeHelpMenu() {
		helpMenu = new JMenu(Strings.MENU_HELP);

		menuBar.add(helpMenu);
	}

	private ActionListener onEditMenuShowCodeClick() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String code = "";

				for (int i = 0; i < program.length; i++) {
					if (i % 16 != 0) {
						code = String.format("%s %04x", code, program[i]);
					} else
						code = String.format("%s\n%04x", code, program[i]);
				}

				JOptionPane.showMessageDialog(getParent(), code);
			}
		};
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

					loadProgramCode();

					enableProgramLoadedOptions();
				}
			}
		};
	}

	private void loadProgramCode() {
		long position = 0;

		try (RandomAccessFile raf = new RandomAccessFile(programFile, "r")) {
			program = new short[(int) raf.length()];

			while (position < raf.length() / 2) {
				program[(int) position++] = raf.readShort();
			}

		} catch (IOException e) {
			System.out.printf("Reading error at: %d", position);
			e.printStackTrace();
		}

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
					isEmulationPaused = false;

					updateButtons();

					chip8 = new CpuCore();
					chip8.loadProgram(programFile.getAbsolutePath());

					emulate();
				}
			}
		};
	}

	private ActionListener onRunMenuStopEmulateClick() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				isEmulationRunning = false;
				isEmulationPaused = false;

				updateButtons();
			}
		};
	}

	private ActionListener onRunMenuPauseEmulateClick() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				isEmulationPaused = true;

				updateButtons();
			}
		};
	}

	private ActionListener onRunMenuResumEmulateClick() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				isEmulationPaused = false;

				updateButtons();

				emulate();
			}
		};
	}

	private void emulate() {
		initializeExecutor();

		if (renderPanel == null) {
			renderPanel = new RenderPanel();
			frame.setContentPane(renderPanel);
			frame.pack();
		}

		executeThread(new Runnable() {

			@Override
			public void run() {
				while (isEmulationRunning && !isEmulationPaused) {
					chip8.emulateCycle();

					renderPanel.setGraphics((chip8.drawGraphics()));
					renderPanel.repaint();

					if (isKeyPressed) {
						chip8.applyKeys();
						isKeyPressed = false;
						// TODO logs
					}

					try {
						Thread.sleep(delayTime);
					} catch (InterruptedException e) {
						isEmulationPaused = false;
						isEmulationRunning = false;
						updateButtons();

						shutdownExecutor();
						e.printStackTrace();
					}
				}
			}
		});
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

	private KeyEventDispatcher KeyEventDispatcher = new KeyEventDispatcher() {

		@Override
		public boolean dispatchKeyEvent(KeyEvent e) {

			switch (e.getID()) {
			case KeyEvent.KEY_PRESSED:
				switch (e.getKeyCode()) {
				case KeyEvent.VK_SPACE:
					if (isEmulationRunning && !isEmulationPaused) {
						isEmulationPaused = true;
					} else if (isEmulationRunning && isEmulationPaused) {
						isEmulationPaused = false;
						emulate();
					}

					if (isEmulationRunning && !isEmulationPaused) {
						for (int i = 0; i < KeyConstants.KeyCodes.length; i++) {
							if (e.getKeyCode() == KeyConstants.KeyCodes[i]) {
								chip8.setKeys(i);
								break;
							}
						}
						isKeyPressed = true;
					}

					updateButtons();
					break;
				}
				break;
			}

			return false;
		}
	};

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
