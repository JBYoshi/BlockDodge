/*
 * Copyright 2015 JBYoshi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jbyoshi.blockdodge;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.prefs.*;

import javax.swing.*;

public final class BlockDodge extends JPanel {
	private static final long serialVersionUID = 6904399199721821562L;
	final Random rand = new Random();
	final JComponent pauseScreen = Box.createVerticalBox();
	private static final int FRAME_TIME = 1000 / 75;
	private volatile BufferedImage buffer;
	private final Set<DodgeShape> shapes = new HashSet<DodgeShape>();
	private final PlayerDodgeShape player = new PlayerDodgeShape(this);
	private final AtomicBoolean stop = new AtomicBoolean(false);
	private volatile double score;
	private static final String COPYRIGHT_TEXT = "Copyright 2015 JBYoshi        github.com/JBYoshi/BlockDodge";
	private static final RandomChooser<Color> COLORS = new RandomChooser<>(Color.BLUE, Color.CYAN, Color.GREEN, Color.MAGENTA,
			new Color(255, 127, 0), new Color(0, 140, 0), Color.RED, Color.YELLOW);

	public BlockDodge() {
		this.buffer = createBuffer();

		addKeyListener(player);
		addFocusListener(player);

		setLayout(new BorderLayout());
		pauseScreen.setVisible(false);
		add(pauseScreen);
		pauseScreen.add(Box.createVerticalGlue());
		pauseScreen.add(label("Paused"));
		pauseScreen.add(Box.createVerticalStrut(32));
		pauseScreen.add(label("Press Escape, Space or Enter to continue."));
		pauseScreen.add(label("Press Delete to exit."));
		pauseScreen.add(Box.createVerticalGlue());
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
			score = 0;
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
						one.onCollided(two);
						two.onCollided(one);
					}
				}
			}

			if (timer % 12 == 0) {
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
				Color c = COLORS.next();
				add(new BounceDodgeShape(this, x, y, w, h, c, (float) (dir * 2 * Math.PI),
						includePlayer ? timer / 2500.0 + 1 : 1.5));
			}

			if (contains(player)) {
				score += 500000.0 / getWidth() / getHeight();
			}

			BufferedImage buffer = createBuffer();
			Graphics2D g = buffer.createGraphics();
			for (DodgeShape shape : shapes) {
				g.setColor(shape.color);
				g.fill(shape.shape);
			}

			g.setColor(Color.WHITE);
			g.setFont(g.getFont().deriveFont(20.0f));
			try {
				int highScore = Data.getHighScore();
				String highScoreText = "High Score: " + highScore;
				g.drawString(highScoreText, getWidth() - 50 - g.getFontMetrics().stringWidth(highScoreText), 50);
				if (score >= highScore + 1) {
					g.setColor(Color.YELLOW);
				}
			} catch (BackingStoreException e) {
				e.printStackTrace();
			}
			g.drawString("Score: " + (int) score, 50, 50);

			if (!includePlayer) {
				g.setFont(g.getFont().deriveFont(10.0f));
				int textWidth = g.getFontMetrics().stringWidth(COPYRIGHT_TEXT);
				int textHeight = g.getFontMetrics().getHeight();
				g.drawString(COPYRIGHT_TEXT, getWidth() / 2 - textWidth / 2, getHeight() - 10 - textHeight);
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
		try {
			Data.updateHighScore((int) score);
		} catch (BackingStoreException e) {
			e.printStackTrace();
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
		frame.enableInputMethods(false);
		final BlockDodge game = new BlockDodge();
		frame.setContentPane(game);
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setVisible(true);

		Box info = Box.createVerticalBox();
		info.add(Box.createVerticalGlue());
		info.add(label("Block Dodge", 32));
		info.add(Box.createVerticalStrut(32));
		info.add(label("You are the white box in the center of the screen."));
		info.add(label("Use the arrow keys to move."));
		info.add(label("Avoid the colored blocks flying at you."));
		info.add(label("Press Space or Enter to start."));
		info.add(label("You can press Escape to pause and Delete to quit."));
		info.add(Box.createVerticalGlue());

		Box infoContainer = Box.createHorizontalBox();
		infoContainer.add(Box.createHorizontalGlue());
		infoContainer.add(info);
		infoContainer.add(Box.createHorizontalGlue());
		frame.setGlassPane(infoContainer);
		infoContainer.setVisible(false);

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
				case KeyEvent.VK_F11:
					GraphicsDevice device = frame.getGraphicsConfiguration().getDevice();
					if (device.isFullScreenSupported() && device.getFullScreenWindow() != frame) {
						frame.dispose();
						frame.setUndecorated(true);
						device.setFullScreenWindow(frame);
					} else {
						frame.dispose();
						device.setFullScreenWindow(null);
						frame.setUndecorated(false);
						frame.setVisible(true);
					}
					frame.revalidate();
				}
			}
		});
		while (true) {
			infoContainer.setVisible(true);
			frame.revalidate();
			game.go(false);
			infoContainer.setVisible(false);
			frame.revalidate();
			isPlaying.set(true);
			game.go(true);
			isPlaying.set(false);
		}
	}

	private static JLabel label(String text) {
		JLabel label = new JLabel(text);
		label.setForeground(Color.WHITE);
		label.setAlignmentX(0.5f);
		return label;
	}

	private static JLabel label(String text, float size) {
		JLabel label = label(text);
		label.setFont(label.getFont().deriveFont(size));
		return label;
	}
}
