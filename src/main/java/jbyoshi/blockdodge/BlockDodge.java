package jbyoshi.blockdodge;

import java.awt.*;
import java.awt.geom.*;

import javax.swing.*;

public final class BlockDodge extends JPanel {
	private final Rectangle2D player;
	private final int width, height;

	public BlockDodge(int width, int height) {
		this.width = width;
		this.height = height;
		final int playerSize = 32;
		this.player = new Rectangle2D.Float(width / 2 - playerSize / 2, height / 2 - playerSize / 2, playerSize,
				playerSize);
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
