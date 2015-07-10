package jbyoshi.blockdodge.core;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

import jbyoshi.blockdodge.*;

public abstract class DodgeShape extends Rectangle2D.Double {
	protected final BlockDodge game;
	protected final Random rand = new Random();
	final Color color;
	private static final float DROP_SCALE = 0.25f;
	private static final int DROP_COUNT = 10;
	private int dropCount = 0;

	public DodgeShape(BlockDodge game, double x, double y, double w, double h, Color c) {
		super(x, y, w, h);
		this.game = game;
		this.color = c;
	}

	protected abstract void move();

	public final void explode() {
		explode0();
	}

	void explode0() {
		for (float i = 0; i < DROP_COUNT; i++) {
			game.add(new Drop((float) (rand.nextFloat() * 2 * Math.PI)));
		}
		game.remove(this);
	}

	protected void onCollided(DodgeShape other) {
	}

	void onRemoved() {
	}

	protected final class Drop extends BounceDodgeShape {
		private int time = 100;

		protected Drop(float dir) {
			super(DodgeShape.this.game, DodgeShape.this.x, DodgeShape.this.y, DodgeShape.this.width * DROP_SCALE,
					DodgeShape.this.height * DROP_SCALE, DodgeShape.this.color, dir);
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
		void explode0() {
			game.remove(this);
		}

		@Override
		protected void onCollided(DodgeShape other) {
			if (other instanceof Drop && ((Drop) other).outer() == DodgeShape.this) {
				return;
			}
			game.remove(this);
		}

		private DodgeShape outer() {
			return DodgeShape.this;
		}

		@Override
		void onRemoved() {
			if (DodgeShape.this.dropCount-- == 0) {
				DodgeShape.this.onRemoved();
			}
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
