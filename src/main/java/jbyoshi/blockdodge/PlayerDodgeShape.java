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

import com.flowpowered.math.vector.*;

public final class PlayerDodgeShape extends DodgeShape {
	private static final Color COLOR = Color.WHITE;
	private static final int SIZE = 32;
	private final PlayerController controller;

	PlayerDodgeShape(BlockDodgeGame game, PlayerController controller) {
		super(game, 0, 0, SIZE, SIZE, COLOR);
		this.controller = controller;
	}

	@Override
	public void move() {
		Vector2d movement = controller.getMovement();
		setX(getX() + movement.getX());
		setY(getY() + movement.getY());
	}

	void reset() {
		setX(game.getWidth() / 2 - getWidth() / 2);
		setY(game.getHeight() / 2 - getHeight() / 2);
	}

	@Override
	void onRemoved() {
		game.stop();
	}

}
