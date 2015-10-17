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

final class InputMainMenu extends Input {
	private final Component info;

	public InputMainMenu(BlockDodgePanel panel, Component info) {
		super(panel);
		this.info = info;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_ENTER:
		case KeyEvent.VK_SPACE:
			game.addTask(game::stop);
			break;
		case KeyEvent.VK_F11:
			JFrame frame = panel.frame;
			GraphicsDevice device = frame.getGraphicsConfiguration().getDevice();
			if (device.isFullScreenSupported() && device.getFullScreenWindow() != frame) {
				frame.dispose();
				frame.setUndecorated(true);
				device.setFullScreenWindow(frame);
			} else {
				frame.dispose();
				device.setFullScreenWindow(null);
				frame.setUndecorated(false);
				frame.setVisible(true);
			}
			frame.revalidate();
			break;
		case KeyEvent.VK_DELETE:
			// The End
			System.exit(0);
			break;
		}
	}

	@Override
	void activate() {
		super.activate();
		info.setVisible(true);
	}

	@Override
	void deactivate() {
		super.deactivate();
		info.setVisible(false);
	}

}
