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

import java.awt.Image;
import java.awt.Toolkit;
import java.util.Arrays;
import java.util.Optional;
import java.util.prefs.BackingStoreException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import jbyoshi.blockdodge.updater.Updater;
import jbyoshi.blockdodge.updater.Version;

public final class BlockDodge {
	static final String COPYRIGHT_TEXT = "Copyright 2015 JBYoshi        github.com/JBYoshi/BlockDodge";

	static BlockDodgePanel panel;

	public static void main(String[] args) {
		// macOS initialization code - this has to be run before any AWT classes are loaded
		System.setProperty("apple.awt.application.name", "Block Dodge");
		try {
			Class<?> macApplication = Class.forName("com.apple.eawt.Application");
			Object applicationInstance = macApplication.getMethod("getApplication").invoke(null);
			macApplication.getMethod("setDockIconImage", Image.class).invoke(applicationInstance, loadIcon(256));
		} catch (ClassNotFoundException ignore) {
		} catch (ReflectiveOperationException e) {
			throw new AssertionError(e);
		}

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
				Optional<Version> update;
				try {
					update = Updater.findUpdate();
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
													   new Object[]{
														   "Could not open update in your browser.",
														   new JTextField(update.get().getURL())
													   },
													   "Update", JOptionPane.ERROR_MESSAGE
									);
							}
						}
					} else {
						System.out.println("No updates available");
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(frame, e, "Updater error", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
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
