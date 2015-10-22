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

import javax.swing.*;

final class InputPauseMenu extends Input {
	InputPauseMenu(BlockDodgePanel panel) {
		super(panel);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_SPACE:
		case KeyEvent.VK_ENTER:
		case KeyEvent.VK_ESCAPE:
			unpause();
			break;
		case KeyEvent.VK_DELETE:
			quit();
			break;
		}
	}

	private void unpause() {
		game.setPaused(false);
	}

	private void quit() {
		game.addTask(() -> panel.player.explode());
		unpause();
	}

	@Override
	Component createDisplay() {
		Box pauseScreen = Box.createVerticalBox();
		pauseScreen.add(Box.createVerticalGlue());
		pauseScreen.add(UI.label("Paused"));
		pauseScreen.add(Box.createVerticalStrut(32));
		pauseScreen.add(UI.button("Continue (Escape/Space/Enter)", Color.GREEN, e -> unpause()));
		pauseScreen.add(UI.button("Exit (Delete)", Color.RED, e -> quit()));
		pauseScreen.add(Box.createVerticalGlue());

		return pauseScreen;
	}

}
