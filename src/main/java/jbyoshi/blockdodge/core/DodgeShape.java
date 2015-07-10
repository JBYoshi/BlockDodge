package jbyoshi.blockdodge.core;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

import jbyoshi.blockdodge.*;

public abstract class DodgeShape {
	protected final BlockDodge game;
	protected final Random rand = new Random();
	final Color color;
	private static final float DROP_SCALE = 0.25f;
	private static final int DROP_COUNT = 10;
	private int dropCount = 0;
	final Rectangle2D shape;

	public DodgeShape(BlockDodge game, double x, double y, double w, double h, Color c) {
		this.game = game;
		this.shape = new Rectangle2D.Double(x, y, w, h);
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
			super(DodgeShape.this.game, DodgeShape.this.getX(), DodgeShape.this.getY(),
					DodgeShape.this.getWidth() * DROP_SCALE, DodgeShape.this.getHeight() * DROP_SCALE,
					DodgeShape.this.color, dir);
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

	public double getX() {
		return shape.getX();
	}

	public double getY() {
		return shape.getY();
	}

	public double getWidth() {
		return shape.getWidth();
	}

	public double getHeight() {
		return shape.getHeight();
	}

	protected void setX(double x) {
		shape.setRect(x, getY(), getWidth(), getHeight());
	}

	protected void setY(double y) {
		shape.setRect(getX(), y, getWidth(), getHeight());
	}

	public boolean collides(DodgeShape other) {
		return game.contains(this) && game.contains(other) && shape.intersects(other.shape);
	}
}
