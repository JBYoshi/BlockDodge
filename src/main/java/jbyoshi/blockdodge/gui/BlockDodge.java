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
	static final String COPYRIGHT_TEXT = "Copyright 2015 JBYoshi        github.com/JBYoshi/BlockDodge";

	static BlockDodgePanel panel;

	public static void main(String[] args) {
		JFrame frame = new JFrame("Block Dodge " + Updater.getCurrentVersion());
		panel = new BlockDodgePanel(frame);
		frame.setIconImages(Arrays.asList(loadIcon(16), loadIcon(32), loadIcon(64), loadIcon(128), loadIcon(256)));
		frame.enableInputMethods(false);
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
			panel.getGame().go(null);

			// Actual game
			panel.setInput(panel.inputInGame);
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

}
