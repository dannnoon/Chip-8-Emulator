package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JPanel;

import pl.krysinski.emulator.core.CpuCore;

public class RenderPanel extends JPanel {
	private static final long serialVersionUID = 2341217297217180510L;

	public static final int PIXEL_WIDTH = 20;
	public static final int PIXEL_HEIGTH = 20;

	private Rectangle rect = new Rectangle(0, 0, PIXEL_WIDTH, PIXEL_HEIGTH);

	byte[][] graphics;

	public void setGraphics(byte[][] graph) {
		graphics = graph;
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(PIXEL_WIDTH * CpuCore.GRAPHICS_WIDTH, PIXEL_HEIGTH * CpuCore.GRAPHICS_HEIGTH);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (graphics != null) {

			Graphics2D g2 = (Graphics2D) g.create();

			for (int i = 0; i < CpuCore.GRAPHICS_HEIGTH; i++) {
				for (int j = 0; j < CpuCore.GRAPHICS_WIDTH; j++) {
					if (graphics[i][j] == 1)
						g2.setColor(Color.BLACK);
					else
						g2.setColor(Color.WHITE);

					rect.x = PIXEL_WIDTH * j;
					g2.fill(rect);
				}
				
				rect.y = PIXEL_HEIGTH * i;
			}
			
			g2.dispose();
		}

	}
}
