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
import java.awt.event.*;
import java.util.concurrent.atomic.*;
import java.util.prefs.*;

import javax.swing.*;

public final class BlockDodge {
	private static final String COPYRIGHT_TEXT = "Copyright 2015 JBYoshi        github.com/JBYoshi/BlockDodge";

	public static void main(String[] args) {
		JFrame frame = new JFrame("Block Dodge");
		frame.enableInputMethods(false);
		final BlockDodgePanel panel = new BlockDodgePanel();
		frame.setContentPane(panel);
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setVisible(true);

		Box info = Box.createVerticalBox();
		info.add(Box.createVerticalGlue());
		info.add(label("Block Dodge", 32));
		info.add(Box.createVerticalStrut(32));
		info.add(label("You are the white box in the center of the screen."));
		info.add(label("Use the arrow keys to move."));
		info.add(label("Avoid the colored blocks flying at you."));
		info.add(label("Press Space or Enter to start."));
		info.add(label("You can press Escape to pause and Delete to quit."));
		info.add(Box.createVerticalGlue());
		info.add(label(COPYRIGHT_TEXT, 10));
		info.add(Box.createVerticalStrut(50));

		Box infoContainer = Box.createHorizontalBox();
		infoContainer.add(Box.createHorizontalGlue());
		infoContainer.add(info);
		infoContainer.add(Box.createHorizontalGlue());
		frame.setGlassPane(infoContainer);
		infoContainer.setVisible(false);

		final AtomicBoolean isPlaying = new AtomicBoolean(false);
		panel.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (isPlaying.get()) {
					return;
				}
				switch (e.getKeyCode()) {
				case KeyEvent.VK_ENTER:
				case KeyEvent.VK_SPACE:
					panel.getGame().stop();
					break;
				case KeyEvent.VK_F11:
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
				}
			}
		});
		while (true) {
			// Start screen
			isPlaying.set(false);
			infoContainer.setVisible(true);
			frame.revalidate();
			panel.getGame().go(false);

			// Actual game
			infoContainer.setVisible(false);
			frame.revalidate();
			isPlaying.set(true);
			panel.reset();

			panel.getGame().go(true);

			// High scores
			try {
				int score = panel.getGame().getScore();
				if (score > HighScores.getHighScore() && JOptionPane.showConfirmDialog(frame,
						new Object[] { "New high score!", score, "Save?" }, frame.getTitle(), JOptionPane.YES_NO_OPTION,
						JOptionPane.PLAIN_MESSAGE) == JOptionPane.YES_OPTION) {
					HighScores.updateHighScore(score);
				} else {
					// Not a high score.
					panel.reset();
				}
			} catch (BackingStoreException e) {
				e.printStackTrace();
			}
		}
	}

	private static JLabel label(String text) {
		JLabel label = new JLabel(text);
		label.setForeground(Color.WHITE);
		label.setAlignmentX(0.5f);
		return label;
	}

	private static JLabel label(String text, float size) {
		JLabel label = label(text);
		label.setFont(label.getFont().deriveFont(size));
		return label;
	}
}
