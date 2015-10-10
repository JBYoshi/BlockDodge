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

import com.flowpowered.math.vector.*;

import jbyoshi.blockdodge.*;

final class InputInGame extends Input implements PlayerController, FocusListener {
	InputInGame(BlockDodgePanel panel) {
		super(panel);
	}

	@Override
	public synchronized Vector2d getMovement() {
		boolean left = panel.keys.isPressed(KeyEvent.VK_LEFT);
		boolean right = panel.keys.isPressed(KeyEvent.VK_RIGHT);
		boolean up = panel.keys.isPressed(KeyEvent.VK_UP);
		boolean down = panel.keys.isPressed(KeyEvent.VK_DOWN);

		Vector2d movement = new Vector2d(left == right ? 0 : left ? -1 : 1, up == down ? 0 : up ? -1 : 1);
		// "Cannot normalize the zero vector"
		if (!movement.equals(Vector2d.ZERO)) {
			movement = movement.normalize();
		}
		return movement;
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
