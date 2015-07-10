package jbyoshi.blockdodge;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

import javax.swing.*;

public final class BlockDodge extends JPanel {
	private final int width, height;
	private final Random rand = new Random();
	private static final Color[] COLORS = new Color[] { Color.BLUE, Color.CYAN, Color.GREEN, Color.MAGENTA,
			Color.ORANGE, Color.PINK, Color.RED, Color.YELLOW };
	private static final int FRAME_TIME = 1000 / 40;
	private static final int PLAYER_SIZE = 32;
	private static final Color PLAYER_COLOR = Color.WHITE;

	public BlockDodge(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void go() {
		Rectangle2D player = new Rectangle2D.Float(width / 2 - PLAYER_SIZE / 2, height / 2 - PLAYER_SIZE / 2,
				PLAYER_SIZE, PLAYER_SIZE);
		Set<DodgeShape> shapes = new HashSet<DodgeShape>();
		int timer = 0;
		int deadTimer = -1;

		while (true) {
			long start = System.currentTimeMillis();

			for (DodgeShape shape : new HashSet<DodgeShape>(shapes)) {
				shape.move();
				if (deadTimer < 0 && shape.intersects(player)) {
					deadTimer = 100;
					// TODO explode
				}
			}

			if (timer % 100 == 0) {
				int w = rand.nextInt(25) + 8;
				int h = rand.nextInt(25) + 8;
				int x = 0; // TODO
				int y = 0; // TODO
				float dir = (float) (rand.nextFloat() * Math.PI);
				Color c = COLORS[rand.nextInt(COLORS.length)];
				shapes.add(new BadDodgeShape(this, x, y, w, h, c, dir));
			}

			timer++;
			if (deadTimer > 0) {
				deadTimer--;
			} else if (deadTimer == 0) {
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
		// TODO
	}
}
