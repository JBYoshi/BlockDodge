package jbyoshi.blockdodge.core;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import java.util.concurrent.atomic.*;

import javax.swing.*;

import jbyoshi.blockdodge.*;

public final class BlockDodge extends JPanel {
	final Random rand = new Random();
	private static final Color[] COLORS = new Color[] { Color.BLUE, Color.CYAN, Color.GREEN, Color.MAGENTA,
			new Color(255, 127, 0), new Color(0, 140, 0), Color.RED, Color.YELLOW };
	private static final int FRAME_TIME = 1000 / 75;
	private BufferedImage buffer;
	private final Set<DodgeShape> shapes = new HashSet<DodgeShape>();
	private final PlayerDodgeShape player = new PlayerDodgeShape(this);
	private final AtomicBoolean stop = new AtomicBoolean(false);

	public BlockDodge() {
		this.buffer = createBuffer();
		addKeyListener(player);
	}

	private BufferedImage createBuffer() {
		return new BufferedImage(Math.max(1, getWidth()), Math.max(1, getHeight()), BufferedImage.TYPE_INT_RGB);
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

	public void go(boolean includePlayer) {
		if (includePlayer) {
			shapes.clear();
			player.reset();
			shapes.add(player);
		} else {
			shapes.remove(player);
		}
		int timer = 0;

		stop.set(false);
		while (!stop.get()) {
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
				int pos = rand.nextInt(getWidth() + w + getWidth() + w + getHeight() + h + getHeight() + h);
				int x, y;
				float dirChg;
				if (pos < getWidth() + w) {
					// From top
					x = pos - w + 1;
					y = -h;
					dirChg = 0.25f;
				} else {
					pos -= getWidth() + w;
					if (pos < getWidth() + w) {
						// From bottom
						x = pos - w;
						y = getHeight();
						dirChg = 0.75f;
					} else {
						pos -= getWidth() + w;
						if (pos < getHeight() + h) {
							// From left
							x = -w;
							y = pos - h;
							dirChg = 0;
						} else {
							// From right
							pos -= getHeight() + h;
							x = getWidth();
							y = pos - h + 1;
							dirChg = 0.5f;
						}
					}
				}
				float dir = (rand.nextFloat() / 2 + dirChg) % 1;
				Color c = COLORS[rand.nextInt(COLORS.length)];
				add(new BadDodgeShape(this, x, y, w, h, c, (float) (dir * 2 * Math.PI)));
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
			requestFocusInWindow();
			long sleep = FRAME_TIME - (System.currentTimeMillis() - start);
			if (sleep > 0) {
				try {
					Thread.sleep(sleep);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	public void stop() {
		stop.set(true);
	}

	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(buffer, 0, 0, null);
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Block Dodge");
		final BlockDodge game = new BlockDodge();
		frame.setContentPane(game);
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setVisible(true);

		final AtomicBoolean isPlaying = new AtomicBoolean(false);
		game.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (isPlaying.get()) {
					return;
				}
				switch (e.getKeyCode()) {
				case KeyEvent.VK_ENTER:
				case KeyEvent.VK_SPACE:
					game.stop();
					break;
				}
			}
		});
		while (true) {
			game.go(false);
			isPlaying.set(true);
			game.go(true);
			isPlaying.set(false);
		}
	}
}
