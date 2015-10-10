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

public final class PlayerDodgeShape extends DodgeShape {
	private static final Color COLOR = Color.WHITE;
	private static final int SIZE = 32;
	private final PlayerController controller;

	public PlayerDodgeShape(BlockDodgeGame game, PlayerController controller) {
		super(game, 0, 0, SIZE, SIZE, COLOR);
		this.controller = controller;
	}

	@Override
	public void move() {
		Point2D movement = controller.move(this);
		setX(clamp(getX() + movement.getX(), 0, game.getWidth() - getWidth()));
		setY(clamp(getY() + movement.getY(), 0, game.getHeight() - getHeight()));
	}

	private static double clamp(double val, double min, double max) {
		if (val < min) {
			val = min;
		} else if (val > max) {
			val = max;
		}
		return val;
	}

	void reset() {
		setX(game.getWidth() / 2 - getWidth() / 2);
		setY(game.getHeight() / 2 - getHeight() / 2);
	}

	@Override
	protected void onRemoved() {
		game.stop();
	}

}
