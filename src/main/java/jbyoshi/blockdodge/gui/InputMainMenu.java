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
	public InputMainMenu(BlockDodgePanel panel) {
		super(panel);
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
	Component createDisplay() {
		Box info = Box.createVerticalBox();
		info.add(Box.createVerticalGlue());
		info.add(UI.label("Block Dodge", 32));
		info.add(Box.createVerticalStrut(32));
		info.add(UI.label("You are the white box in the center of the screen."));
		info.add(UI.label("Use your mouse or the arrow keys to move."));
		info.add(UI.label("Avoid the colored blocks flying at you."));
		info.add(Box.createVerticalStrut(32));
		info.add(UI.button("Start (Enter)", Color.GREEN, e -> game.stop()));
		info.add(Box.createVerticalGlue());
		info.add(UI.label(BlockDodge.COPYRIGHT_TEXT, 10));
		info.add(Box.createVerticalStrut(50));
		return info;
	}

}
