package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import pl.krysinski.emulator.core.CpuCore;

public class RenderPanel extends JPanel {
	private static final long serialVersionUID = 2341217297217180510L;

	public static final int DEFAULT_PIXEL_WIDTH = 10;
	public static final int DEFAULT_PIXEL_HEIGTH = 10;

	private int pixelWidth;
	private int pixelHeigth;

	private Rectangle rect = new Rectangle(0, 0, DEFAULT_PIXEL_WIDTH, DEFAULT_PIXEL_HEIGTH);

	byte[][] graphics;

	public RenderPanel() {
		pixelWidth = DEFAULT_PIXEL_WIDTH;
		pixelHeigth = DEFAULT_PIXEL_HEIGTH;

		graphics = new byte[CpuCore.GRAPHICS_HEIGTH][CpuCore.GRAPHICS_WIDTH];

		this.setBackground(Color.BLACK);

		this.repaint();

		this.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				super.componentResized(e);

				int heigth = e.getComponent().getHeight();
				int width = e.getComponent().getWidth();

				repaint();

				// if (width < heigth){
				pixelWidth = width / CpuCore.GRAPHICS_WIDTH;
				// pixelHeigth = pixelWidth;
				// }
				// else {
				pixelHeigth = heigth / CpuCore.GRAPHICS_HEIGTH;
				// pixelWidth = pixelHeigth;
				// }
			}
		});
	}

	public void setGraphics(byte[][] graph) {
		if (graph != null)
			graphics = graph;
	}

	public void clearGraphics() {
		for (int i = 0; i < graphics.length; i++) {
			for (int j = 0; j < graphics[i].length; j++) {
				graphics[i][j] = 0;
			}
		}
	}

	@Override
	public Dimension getPreferredSize() {
		return getParent().getSize();
		// return new Dimension(pixelWidth * CpuCore.GRAPHICS_WIDTH, pixelHeigth
		// * CpuCore.GRAPHICS_HEIGTH);
	}

	@Override
	public void paint(Graphics g) {
		super.paintComponent(g);

		if (graphics != null) {

			Graphics2D g2 = (Graphics2D) g.create();

			rect.setSize(pixelWidth, pixelHeigth);

			for (int i = 0; i < CpuCore.GRAPHICS_HEIGTH; i++) {
				rect.y = pixelHeigth * i;
				for (int j = 0; j < CpuCore.GRAPHICS_WIDTH; j++) {
					if (graphics[i][j] == 1)
						g2.setColor(Color.WHITE);
					else
						g2.setColor(Color.BLACK);

					rect.x = pixelWidth * j;
					g2.fill(rect);
				}
			}

			g2.dispose();
		}

	}
}
