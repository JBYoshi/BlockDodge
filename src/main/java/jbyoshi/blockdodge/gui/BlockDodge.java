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
import java.util.*;
import java.util.concurrent.*;
import java.util.prefs.*;

import javax.swing.*;

import jbyoshi.blockdodge.updater.*;

public final class BlockDodge {
	private static final String COPYRIGHT_TEXT = "Copyright 2015 JBYoshi        github.com/JBYoshi/BlockDodge";

	public static void main(String[] args) {
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

		Box pauseScreen = Box.createVerticalBox();
		pauseScreen.setVisible(false);
		pauseScreen.add(Box.createVerticalGlue());
		pauseScreen.add(label("Paused"));
		pauseScreen.add(Box.createVerticalStrut(32));
		pauseScreen.add(label("Press Escape, Space or Enter to continue."));
		pauseScreen.add(label("Press Delete to exit."));
		pauseScreen.add(Box.createVerticalGlue());

		JFrame frame = new JFrame("Block Dodge " + Updater.getCurrentVersion());
		frame.setIconImages(Arrays.asList(loadIcon(16), loadIcon(32), loadIcon(64), loadIcon(128), loadIcon(256)));
		frame.enableInputMethods(false);

		final BlockDodgePanel panel = new BlockDodgePanel(frame, infoContainer, pauseScreen);
		frame.setGlassPane(infoContainer);
		panel.setLayout(new BorderLayout());
		panel.add(pauseScreen);

		frame.setContentPane(panel);
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setVisible(true);

		if (Updater.isEnabled()) {
			new Thread(() -> {
				Optional<Version> update = Optional.empty();
				while (true) {
					try {
						update = Updater.findUpdate();
					} catch (Exception e) {
						JOptionPane.showMessageDialog(frame, e, "Updater error", JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
					}
					if (update.isPresent()) {
						String name = update.get().getName();
						if (JOptionPane.showConfirmDialog(frame,
								new Object[] { "Update available!", name, "Download?" }, "BlockDodge",
								JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE) == JOptionPane.YES_OPTION) {
							try {
								update.get().open();
							} catch (Exception e) {
								JOptionPane
								.showMessageDialog(frame,
										new Object[] { "Could not open update in your browser.",
												new JTextField(update.get().getURL()) },
										"Update", JOptionPane.ERROR_MESSAGE);
							}
						}
					} else {
						System.out.println("No updates available");
					}
					try {
						Thread.sleep(TimeUnit.MINUTES.toMillis(10));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			} , "Updater").start();
		}

		while (true) {
			// Start screen
			panel.setInput(panel.inputMainMenu);
			infoContainer.setVisible(true);
			frame.revalidate();
			panel.getGame().go(null);

			// Actual game
			panel.setInput(panel.inputInGame);
			infoContainer.setVisible(false);
			frame.revalidate();
			panel.reset();

			panel.getGame().go(panel.player);

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

	private static Image loadIcon(int size) {
		return Toolkit.getDefaultToolkit().getImage(BlockDodgePanel.class.getResource("icon-" + size + ".png"));
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
