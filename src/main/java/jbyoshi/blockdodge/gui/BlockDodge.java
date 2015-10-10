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
import java.awt.image.*;
import java.util.*;
import java.util.prefs.*;

import javax.swing.*;

import jbyoshi.blockdodge.*;

public final class BlockDodge extends JPanel {
	private static final String COPYRIGHT_TEXT = "Copyright 2015 JBYoshi        github.com/JBYoshi/BlockDodge";
	private static final long serialVersionUID = 2675582657135016482L;
	private boolean isHighScore = false;
	final JFrame frame;
	private final JComponent pauseScreen = Box.createVerticalBox();
	private volatile BufferedImage buffer;
	private final BlockDodgeGame game = new BlockDodgeGame() {

		@Override
		protected void updatePaused(boolean paused) {
			setInput(paused ? inputPauseMenu : inputInGame);
		}

		@Override
		protected void update() {
			BufferedImage buffer = new BufferedImage(Math.max(1, getWidth()), Math.max(1, getHeight()),
					BufferedImage.TYPE_INT_RGB);
			Graphics2D g = buffer.createGraphics();

			g.setColor(Color.BLACK);
			g.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());

			for (DodgeShape shape : getShapes()) {
				g.setColor(shape.getColor());
				g.fill(shape.getShape());
			}

			g.setColor(Color.WHITE);
			g.setFont(g.getFont().deriveFont(20.0f));
			try {
				int highScore = HighScores.getHighScore();
				String highScoreText = "High Score: " + highScore;
				g.drawString(highScoreText, getWidth() - 50 - g.getFontMetrics().stringWidth(highScoreText), 50);
				if (getScore() >= highScore + 1) {
					isHighScore = true;
				}
				if (isHighScore) {
					g.setColor(Color.YELLOW);
				}
			} catch (BackingStoreException e) {
				e.printStackTrace();
			}
			g.drawString("Score: " + getScore(), 50, 50);
			g.dispose();

			BlockDodge.this.buffer = buffer;
			BlockDodge.this.repaint();
			BlockDodge.this.requestFocusInWindow();
		}

		@Override
		protected Dimension calculateSize() {
			return getSize();
		}
	};
	final PlayerDodgeShape player;
	private final InputMainMenu inputMainMenu;
	private final InputInGame inputInGame;
	private final InputPauseMenu inputPauseMenu;
	private Input input;

	public BlockDodge(JFrame frame, Component mainMenu) {
		this.frame = frame;

		inputMainMenu = new InputMainMenu(this, mainMenu);
		inputInGame = new InputInGame(this);
		inputPauseMenu = new InputPauseMenu(this, pauseScreen);
		player = new PlayerDodgeShape(game, inputInGame);
		setInput(inputMainMenu);

		setLayout(new BorderLayout());
		pauseScreen.setVisible(false);
		add(pauseScreen);
		pauseScreen.add(Box.createVerticalGlue());
		pauseScreen.add(label("Paused"));
		pauseScreen.add(Box.createVerticalStrut(32));
		pauseScreen.add(label("Press Escape, Space or Enter to continue."));
		pauseScreen.add(label("Press Delete to exit."));
		pauseScreen.add(Box.createVerticalGlue());
	}

	public BlockDodgeGame getGame() {
		return game;
	}

	public void reset() {
		isHighScore = false;
	}

	@Override
	public void paintComponent(Graphics g) {
		if (buffer != null) {
			g.drawImage(buffer, 0, 0, null);
		}
	}

	void setInput(Input input) {
		if (this.input != null) {
			this.input.deactivate();
		}
		input.activate();
		this.input = input;
	}

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

		JFrame frame = new JFrame("Block Dodge");
		frame.setIconImages(Arrays.asList(loadIcon(16), loadIcon(32), loadIcon(64), loadIcon(128), loadIcon(256)));
		frame.enableInputMethods(false);

		final BlockDodge panel = new BlockDodge(frame, infoContainer);

		frame.setContentPane(panel);
		frame.setGlassPane(infoContainer);
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setVisible(true);

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
		return Toolkit.getDefaultToolkit().getImage(BlockDodge.class.getResource("icon-" + size + ".png"));
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
