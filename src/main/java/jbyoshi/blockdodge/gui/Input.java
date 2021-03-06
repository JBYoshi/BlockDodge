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

import jbyoshi.blockdodge.*;

abstract class Input implements KeyListener {
	final BlockDodgePanel panel;
	final BlockDodgeGame game;
	private final Component display;

	Input(BlockDodgePanel panel) {
		this.panel = panel;
		this.game = panel.getGame();
		this.display = createDisplay();
	}

	void activate() {
		panel.frame.addKeyListener(this);
		panel.add(display);
		panel.frame.revalidate();
		panel.frame.repaint();
	}

	void deactivate() {
		panel.frame.removeKeyListener(this);
		panel.remove(display);
		panel.frame.revalidate();
		panel.frame.repaint();
	}

	abstract Component createDisplay();

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
}
