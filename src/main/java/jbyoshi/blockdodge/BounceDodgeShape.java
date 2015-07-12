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

import jbyoshi.blockdodge.core.*;

public class BounceDodgeShape extends DodgeShape {
	private double xMove, yMove;

	public BounceDodgeShape(BlockDodge game, double x, double y, double w, double h, Color c, float dir, double speed) {
		super(game, x, y, w, h, c);
		this.xMove = Math.sin(dir) * speed;
		this.yMove = -Math.cos(dir) * speed;
	}

	@Override
	protected void move() {
		double x = getX() + xMove;
		double y = getY() + yMove;
		if (xMove > 0) {
			// Moving right
			if (x + getWidth() >= game.getWidth()) {
				// Hit right wall
				x = game.getWidth() - x - getWidth() + game.getWidth() - getWidth();
				xMove = -xMove;
			}
		} else if (xMove < 0) {
			// Moving left
			if (x < 0) {
				// Hit left wall
				x = -x;
				xMove = -xMove;
			}
		}

		if (yMove > 0) {
			// Moving down
			if (y + getHeight() >= game.getHeight()) {
				// Hit bottom wall
				y = game.getHeight() - y - getHeight() + game.getHeight() - getHeight();
				yMove = -yMove;
			}
		} else if (yMove < 0) {
			// Moving up
			if (y < 0) {
				// Hit top wall
				y = -y;
				yMove = -yMove;
			}
		}

		setX(x);
		setY(y);
	}
}
