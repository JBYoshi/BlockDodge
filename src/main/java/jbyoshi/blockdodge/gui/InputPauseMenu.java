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

import java.awt.*;
import java.awt.event.*;

final class InputPauseMenu extends Input {
	private final Component pauseMenu;

	public InputPauseMenu(BlockDodge panel, Component pauseMenu) {
		super(panel);
		this.pauseMenu = pauseMenu;
	}

	@Override
	void activate() {
		super.activate();
		pauseMenu.setVisible(true);
	}

	@Override
	void deactivate() {
		super.deactivate();
		pauseMenu.setVisible(false);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_SPACE:
		case KeyEvent.VK_ENTER:
		case KeyEvent.VK_ESCAPE:
			game.setPaused(false);
			break;
		case KeyEvent.VK_DELETE:
			game.addTask(() -> panel.player.explode());
			game.setPaused(false);
			break;
		}
	}

}
