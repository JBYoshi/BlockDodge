package jbyoshi.blockdodge.core;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import javax.swing.*;

import jbyoshi.blockdodge.*;

public final class BlockDodge extends JPanel {
	private final int width, height;
	final Random rand = new Random();
	private static final Color[] COLORS = new Color[] { Color.BLUE, Color.CYAN, Color.GREEN, Color.MAGENTA,
			Color.ORANGE, Color.PINK, Color.RED, Color.YELLOW };
	private static final int FRAME_TIME = 1000 / 40;
	private BufferedImage buffer;
	private final Set<DodgeShape> shapes = new HashSet<DodgeShape>();
	private final PlayerDodgeShape player = new PlayerDodgeShape(this);

	public BlockDodge(int width, int height) {
		this.width = width;
		this.height = height;
		this.buffer = createBuffer();
	}

	private BufferedImage createBuffer() {
		return new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	}

	public void add(DodgeShape shape) {
		shapes.add(shape);
	}

	public void remove(DodgeShape shape) {
		shapes.remove(shape);
		if (shape.getDropCount() == 0) {
			shape.onRemoved();
		}
	}

	public boolean contains(DodgeShape shape) {
		return shapes.contains(shape);
	}

	public PlayerDodgeShape getPlayer() {
		return player;
	}

	public void go() {
		player.reset();
		shapes.clear();
		shapes.add(player);
		int timer = 0;

		while (true) {
			long start = System.currentTimeMillis();

			for (DodgeShape shape : new HashSet<DodgeShape>(shapes)) {
				shape.move();
			}
			for (DodgeShape one : new HashSet<DodgeShape>(shapes)) {
				if (!shapes.contains(one)) {
					continue; // Removed during a previous iteration
				}
				for (DodgeShape two : new HashSet<DodgeShape>(shapes)) {
					if (!shapes.contains(two) || one == two) {
						continue;
					}
					if (one.collides(two)) {
						// Drops take care of it themselves.
						boolean handled = false;
						if (one instanceof DodgeShape.Drop) {
							one.onCollided(two);
							handled = true;
						}
						if (two instanceof DodgeShape.Drop) {
							two.onCollided(one);
							handled = true;
						}

						if (!handled) {
							one.onCollided(two);
							two.onCollided(one);
						}
					}
				}
			}

			if (timer % 100 == 0) {
				int w = rand.nextInt(25) + 8;
				int h = rand.nextInt(25) + 8;
				int x = 0; // TODO
				int y = 0; // TODO
				float dir = (float) (rand.nextFloat() * Math.PI);
				Color c = COLORS[rand.nextInt(COLORS.length)];
				add(new BadDodgeShape(this, x, y, w, h, c, dir));
			}

			BufferedImage buffer = createBuffer();
			Graphics2D g = buffer.createGraphics();
			for (DodgeShape shape : shapes) {
				g.setColor(shape.color);
				g.fill(shape.shape);
			}
			g.dispose();
			this.buffer = buffer;
			repaint();

			timer++;
			if (!contains(player) && player.getDropCount() == 0) {
				return;
			}
			long sleep = FRAME_TIME - (System.currentTimeMillis() - start);
			if (sleep > 0) {
				try {
					Thread.sleep(sleep);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(buffer, 0, 0, null);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(width, height);
	}

	@Override
	public Dimension getMinimumSize() {
		return new Dimension(width, height);
	}

	@Override
	public Dimension getMaximumSize() {
		return new Dimension(width, height);
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Block Dodge");
		BlockDodge game = new BlockDodge(800, 600);
		frame.setContentPane(game);
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setVisible(true);

		game.go();
	}
}
