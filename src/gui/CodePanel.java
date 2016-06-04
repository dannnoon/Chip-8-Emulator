package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import pl.krysinski.emulator.constants.Strings;

public class CodePanel extends JPanel {

	private static final String TITLE = "Program code";
	
	private static final int COLUMNS_NUMBER = 16;

	short[] code;

	JTextArea codeArea;
	JButton closeButton;

	JPanel thisPanel;
	JRootPane root;
	JDialog dialog;

	public CodePanel(short[] code) {
		this.code = code;
		thisPanel = this;

		BorderLayout layout = new BorderLayout();

		codeArea = new JTextArea();
		codeArea.setEditable(false);
		codeArea.setBorder(null);
		codeArea.setBackground(Color.BLACK);
		codeArea.setForeground(Color.WHITE);
		codeArea.setSelectedTextColor(Color.RED);
		fillCodeArea();

		JScrollPane scrollPane = new JScrollPane(codeArea);
		add(scrollPane);
		layout.addLayoutComponent(scrollPane, BorderLayout.CENTER);

		closeButton = new JButton(Strings.DIALOG_CLOSE_BUTTON);
		closeButton.addActionListener(closeButtonClick());
		add(closeButton);
		layout.addLayoutComponent(closeButton, BorderLayout.SOUTH);

		this.setLayout(layout);
	}

	private void fillCodeArea() {	
		for (int j = 0; j < COLUMNS_NUMBER; j++) {
			codeArea.setText(String.format("%s0x%04X\t", codeArea.getText().toString(), j));
		}
		
		codeArea.setText(String.format("%s\n\n", codeArea.getText().toString()));
		
		for (int i = 0; i < code.length; i++) {
			if (i % COLUMNS_NUMBER != 0)
				codeArea.setText(String.format("%s\t0x%04X", codeArea.getText().toString(), code[i]));
			else
				codeArea.setText(String.format("%s\t\n0x%04X", codeArea.getText().toString(), code[i]));
		}
	}

	private ActionListener closeButtonClick() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
				dialog.getRootPane().setDefaultButton(null);
			}
		};
	}

	public boolean showDialog(Component parent) {
		Frame owner = null;
		if (parent instanceof Frame)
			owner = (Frame) parent;
		else
			owner = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);

		if (dialog == null || dialog.getOwner() != owner) {
			dialog = new JDialog(owner, true);
			dialog.add(this);
			dialog.getRootPane().setDefaultButton(closeButton);
			dialog.pack();
		}

		dialog.setTitle(TITLE);
		dialog.setVisible(true);
		return true;
	}

}
