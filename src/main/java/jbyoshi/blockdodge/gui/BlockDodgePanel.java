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
import java.util.prefs.*;

import javax.swing.*;

import jbyoshi.blockdodge.*;
import jbyoshi.blockdodge.util.*;

public final class BlockDodgePanel extends JPanel {
	private static final long serialVersionUID = 2675582657135016482L;
	private boolean isHighScore = false;
	final JFrame frame;
	private volatile BufferedImage buffer;
	private final BlockDodgeGame game = new BlockDodgeGame() {

		@Override
		protected void updatePaused(boolean paused) {
			SwingUtilities.invokeLater(() -> {
				setInput(paused ? inputPauseMenu : inputInGame);
			});
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

			BlockDodgePanel.this.buffer = buffer;
			BlockDodgePanel.this.repaint();
			frame.requestFocusInWindow();
		}

		@Override
		protected Dimension calculateSize() {
			return getSize();
		}
	};
	final PlayerDodgeShape player;
	final InputMainMenu inputMainMenu;
	final InputInGame inputInGame;
	final InputPauseMenu inputPauseMenu;
	private Input input;
	final KeyTracker keys = new KeyTracker();

	public BlockDodgePanel(JFrame frame) {
		super(new BorderLayout());
		this.frame = frame;

		inputMainMenu = new InputMainMenu(this);
		inputInGame = new InputInGame(this);
		inputPauseMenu = new InputPauseMenu(this);
		player = new PlayerDodgeShape(game, inputInGame);
		setInput(inputMainMenu);
		addKeyListener(keys);
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
}
