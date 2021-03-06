/*
 * Copyright (c) 2015 JBYoshi
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
import java.awt.geom.*;
import java.util.*;

public abstract class DodgeShape {
	protected final BlockDodgeGame game;
	protected final Random rand = new Random();
	private final Color color;
	private static final float DROP_SCALE = 0.25f;
	private int dropCount = 0;
	final Rectangle2D.Double shape;

	public DodgeShape(BlockDodgeGame game, double x, double y, double w, double h, Color c) {
		this.game = game;
		this.shape = new Rectangle2D.Double(x, y, w, h);
		this.color = c;
	}

	protected abstract void move();

	public void explode() {
		int width = (int) shape.getWidth();
		int height = (int) shape.getHeight();
		if (width <= 0 || height <= 0) {
			// No room to explode. Just cancel.
			game.remove(this);
			return;
		}

		boolean[][] used = new boolean[width][height];
		int maxArea = (int) (width * height * DROP_SCALE);
		while (true) {
			int dropX = -1, dropY = -1;
			findPos: for (int x = 0; x < used.length; x++) {
				for (int y = 0; y < used[x].length; y++) {
					if (!used[x][y]) {
						dropX = x;
						dropY = y;
						break findPos;
					}
				}
			}
			if (dropX == -1 || dropY == -1) {
				break;
			}
			int dropX2 = dropX, dropY2 = dropY;
			while ((dropX2 - dropX) * (dropY2 - dropY) < maxArea) {
				if (rand.nextBoolean()) {
					// Expand the width
					if (used.length == dropX2 + 1) {
						break;
					}
					for (int y = dropY; y <= dropY2; y++) {
						if (used[dropX2][y]) {
							break;
						}
					}
					dropX2++;
				} else {
					// Expand the height
					if (used[0].length == dropY2 + 1) {
						break;
					}
					for (int x = dropX; x <= dropX2; x++) {
						if (used[x][dropY2]) {
							break;
						}
					}
					dropY2++;
				}
			}
			for (int x = dropX; x <= dropX2; x++) {
				for (int y = dropY; y <= dropY2; y++) {
					used[x][y] = true;
				}
			}
			float dir = (float) (rand.nextFloat() * 2 * Math.PI);
			game.add(new Drop(dropX, dropY, dropX2, dropY2, dir, 1));
		}
		game.remove(this);
	}

	protected void onCollided(DodgeShape other) {
		explode();
	}

	protected void onDeath() {
	}

	protected void onFullyRemoved() {
	}

	protected final class Drop extends BounceDodgeShape {
		private int time = 0;

		protected Drop(int x1, int y1, int x2, int y2, float dir, double speed) {
			super(DodgeShape.this.game, DodgeShape.this.getX() + x1, DodgeShape.this.getY() + y1, x2 - x1 + 1,
					y2 - y1 + 1, DodgeShape.this.getColor(), dir, speed);
			DodgeShape.this.dropCount++;
		}

		@Override
		public void move() {
			super.move();
			if (++time % 5 == 0) {
				if (getWidth() == 0 && getHeight() == 0) {
					game.remove(this);
					return;
				}
				int change = rand.nextInt((int) (getWidth() + getHeight()));
				if (change < getWidth()) {
					if (rand.nextBoolean()) {
						setX(getX() + 1);
					}
					setWidth(this, getWidth() - 1);
				} else {
					if (rand.nextBoolean()) {
						setY(getY() + 1);
					}
					setHeight(this, getHeight() - 1);
				}
			}
		}

		@Override
		protected void onCollided(DodgeShape other) {
			if (other instanceof Drop && ((Drop) other).outer() == outer()) {
				return;
			}
			super.onCollided(other);
		}

		private DodgeShape outer() {
			return DodgeShape.this;
		}

		@Override
		public void explode() {
			game.remove(this);
		}

		@Override
		protected void onFullyRemoved() {
			if (--DodgeShape.this.dropCount == 0) {
				DodgeShape.this.onFullyRemoved();
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

	public final Rectangle2D getShapeCopy() {
		return new Rectangle2D.Double(getX(), getY(), getWidth(), getHeight());
	}

	public Color getColor() {
		return color;
	}

	protected void setX(double x) {
		shape.x = x;
	}

	protected void setY(double y) {
		shape.y = y;
	}

	public boolean collides(DodgeShape other) {
		return game.contains(this) && game.contains(other) && shape.intersects(other.shape);
	}

	// To get around compiler insanity. Used by Drop.

	static void setWidth(DodgeShape shape, double width) {
		shape.shape.width = width;
	}

	static void setHeight(DodgeShape shape, double height) {
		shape.shape.height = height;
	}
}
