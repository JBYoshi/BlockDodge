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
package jbyoshi.blockdodge.gui;

import java.awt.event.*;
import java.awt.geom.*;

import jbyoshi.blockdodge.*;

final class InputInGame extends Input implements PlayerController, FocusListener {
	private static final double SQRT_HALF = Math.sqrt(0.5);

	InputInGame(BlockDodgePanel panel) {
		super(panel);
	}

	@Override
	public synchronized Point2D move(PlayerDodgeShape player) {
		boolean left = panel.keys.isPressed(KeyEvent.VK_LEFT);
		boolean right = panel.keys.isPressed(KeyEvent.VK_RIGHT);
		boolean up = panel.keys.isPressed(KeyEvent.VK_UP);
		boolean down = panel.keys.isPressed(KeyEvent.VK_DOWN);

		if (left || right || up || down) {
			double move = left != right && up != down ? SQRT_HALF : 1;
			double x = player.getX();
			if (left && !right) {
				x -= move;
			} else if (right && !left) {
				x += move;
			}
			double y = player.getY();
			if (up && !down) {
				y -= move;
			} else if (down && !up) {
				y += move;
			}
			return new Point2D.Double(x, y);
		}

		Point2D startLoc = new Point2D.Double(player.getX(), player.getY());
		Point2D endLoc = panel.getMousePosition();
		endLoc = new Point2D.Double(endLoc.getX() - player.getWidth() / 2, endLoc.getY() - player.getHeight() / 2);
		Point2D difference = new Point2D.Double(endLoc.getX() - startLoc.getX(), endLoc.getY() - startLoc.getY());
		if (endLoc.distance(startLoc) <= 1.0) {
			return endLoc;
		}

		double length = difference.distance(0, 0);
		return new Point2D.Double(startLoc.getX() + difference.getX() / length,
				startLoc.getY() + difference.getY() / length);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_ESCAPE:
			game.setPaused(true);
			break;
		case KeyEvent.VK_DELETE:
			game.addTask(() -> panel.player.explode());
			break;
		}
	}

	@Override
	void activate() {
		super.activate();
		panel.addFocusListener(this);
	}

	@Override
	void deactivate() {
		super.deactivate();
		panel.removeFocusListener(this);
	}

	@Override
	public void focusGained(FocusEvent e) {
	}

	@Override
	public void focusLost(FocusEvent e) {
		game.addTask(() -> game.setPaused(true));
	}

}
