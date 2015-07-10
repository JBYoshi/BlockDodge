package jbyoshi.blockdodge;

import java.awt.*;
import java.awt.geom.*;

public abstract class DodgeShape extends Rectangle2D.Double {
	protected final BlockDodge game;
	private final Color c;

	public DodgeShape(BlockDodge game, double x, double y, double w, double h, Color c) {
		super(x, y, w, h);
		this.game = game;
		this.c = c;
	}

	public abstract void move();

	public Color getColor() {
		return c;
	}
}
