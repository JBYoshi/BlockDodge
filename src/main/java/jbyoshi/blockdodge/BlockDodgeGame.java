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
import java.awt.image.*;
import java.util.*;
import java.util.concurrent.atomic.*;

public abstract class BlockDodgeGame {

	final Random rand = new Random();
	private static final int FRAME_TIME = 1000 / 75;
	private Dimension size = new Dimension(0, 0);
	private final Set<DodgeShape> shapes = new HashSet<DodgeShape>();
	private final PlayerDodgeShape player = new PlayerDodgeShape(this);
	private final AtomicBoolean stop = new AtomicBoolean(false);
	private volatile double score;
	private static final String COPYRIGHT_TEXT = "Copyright 2015 JBYoshi        github.com/JBYoshi/BlockDodge";
	private static final RandomChooser<Color> COLORS = new RandomChooser<>(Color.BLUE, Color.CYAN, Color.GREEN,
			Color.MAGENTA, new Color(255, 127, 0), new Color(0, 140, 0), Color.RED, Color.YELLOW);

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

			calculateSize(size);

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
				createShape(includePlayer, timer);
			}

			if (contains(player)) {
				score += 500000.0 / getWidth() / getHeight();
			}

			paint(includePlayer);

			timer++;
			long sleep = FRAME_TIME - (System.currentTimeMillis() - start);
			if (sleep > 0) {
				try {
					Thread.sleep(sleep);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	private void createShape(boolean includePlayer, int timer) {
		int w = rand.nextInt(25) + 8;
		int h = rand.nextInt(25) + 8;
		int pos = rand.nextInt(2 * (getWidth() + w + getHeight() + h));
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

	private void paint(boolean includePlayer) {
		BufferedImage buffer = createBuffer();
		Graphics2D g = buffer.createGraphics();
		for (DodgeShape shape : shapes) {
			g.setColor(shape.color);
			g.fill(shape.shape);
		}
		g.setColor(Color.WHITE);
		g.setFont(g.getFont().deriveFont(20.0f));
		g.drawString("Score: " + (int) score, 50, 50);
		if (!includePlayer) {
			g.setFont(g.getFont().deriveFont(10.0f));
			int textWidth = g.getFontMetrics().stringWidth(COPYRIGHT_TEXT);
			int textHeight = g.getFontMetrics().getHeight();
			g.drawString(COPYRIGHT_TEXT, getWidth() / 2 - textWidth / 2, getHeight() - 10 - textHeight);
		}
		g.dispose();
		repaint(buffer);
	}

	public void stop() {
		stop.set(true);
	}

	public int getWidth() {
		return size.width;
	}

	public int getHeight() {
		return size.height;
	}

	protected abstract void calculateSize(Dimension target);

	protected abstract void updatePaused(boolean paused);

	protected abstract void repaint(BufferedImage buffer);
}
