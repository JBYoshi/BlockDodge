package jbyoshi.blockdodge;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

public abstract class DodgeShape extends Rectangle2D.Double {
	protected final BlockDodge game;
	protected final Random rand = new Random();
	private final Color c;
	private static final float DROP_SCALE = 0.25f;
	private static final int DROP_COUNT = 10;
	private int dropCount = 0;

	public DodgeShape(BlockDodge game, double x, double y, double w, double h, Color c) {
		super(x, y, w, h);
		this.game = game;
		this.c = c;
	}

	public abstract void move();

	public Color getColor() {
		return c;
	}

	public void explode() {
		game.remove(this);
		for (float i = 0; i < DROP_COUNT; i++) {
			game.add(new Drop((float) (rand.nextFloat() * 2 * Math.PI)));
		}
	}

	protected final class Drop extends BounceDodgeShape {
		private int time = 100;

		protected Drop(float dir) {
			super(DodgeShape.this.game, DodgeShape.this.x, DodgeShape.this.y, DodgeShape.this.width * DROP_SCALE,
					DodgeShape.this.height * DROP_SCALE, DodgeShape.this.c, dir);
			DodgeShape.this.dropCount++;
		}

		@Override
		public void move() {
			super.move();
			if (time-- == 0) {
				game.remove(this);
			}
		}

		@Override
		public void explode() {
			game.remove(this);
		}

		void onRemoved() {
			DodgeShape.this.dropCount--;
		}
	}

	public int getDropCount() {
		return dropCount;
	}

	@Override
	public boolean contains(double x, double y) {
		return game.contains(this) && super.contains(x, y);
	}

	@Override
	public boolean contains(double x, double y, double w, double h) {
		return game.contains(this) && super.contains(x, y, w, h);
	}

	@Override
	public boolean contains(Rectangle2D rect) {
		if (rect instanceof DodgeShape) {
			DodgeShape ds = (DodgeShape) rect;
			if (!game.contains(ds)) {
				return false;
			}
		}
		return game.contains(this) && super.contains(rect);
	}

	@Override
	public boolean intersects(double x, double y, double w, double h) {
		return game.contains(this) && super.intersects(x, y, w, h);
	}

	@Override
	public boolean intersects(Rectangle2D rect) {
		if (rect instanceof DodgeShape) {
			DodgeShape ds = (DodgeShape) rect;
			if (!game.contains(ds)) {
				return false;
			}
		}
		return game.contains(this) && super.intersects(rect);
	}

	@Override
	public boolean intersectsLine(double x1, double y1, double x2, double y2) {
		return game.contains(this) && super.intersectsLine(x1, y1, x2, y2);
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}

	@Override
	public boolean equals(Object other) {
		return other == this;
	}
}
