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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import jbyoshi.blockdodge.BlockDodgeGame;
import jbyoshi.blockdodge.DodgeShape;
import jbyoshi.blockdodge.PlayerDodgeShape;
import jbyoshi.blockdodge.util.HighScoreTracker;
import jbyoshi.blockdodge.util.KeyTracker;

public final class BlockDodgePanel extends JPanel {
	private static final long serialVersionUID = 2675582657135016482L;
	final JFrame frame;
	private volatile List<Consumer<Graphics2D>> buffer;
	private final BlockDodgeGame game = new BlockDodgeGame() {

		@Override
		protected void updatePaused(boolean paused) {
			SwingUtilities.invokeLater(() -> {
				setInput(paused ? inputPauseMenu : inputInGame);
			});
		}

		@Override
		protected void update() {
			List<Consumer<Graphics2D>> buffer = new LinkedList<>();

			buffer.add(g -> {
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, getWidth(), getHeight());
			});

			for (DodgeShape shape : getShapes()) {
				Color cachedColor = shape.getColor();
				Shape cachedShape = shape.getShapeCopy();
				buffer.add(g -> {
					g.setColor(cachedColor);
					g.fill(cachedShape);
				});
			}

			boolean isHighScore = false;
			int score = getScore();
			try {
				int highScore = HighScoreTracker.INSTANCE.getHighScore();
				if (score > highScore) {
					isHighScore = true;
				}
				buffer.add(g -> {
					g.setColor(Color.WHITE);
					g.setFont(g.getFont().deriveFont(20.0f));
					String highScoreText = "High Score: " + highScore;
					g.drawString(highScoreText, BlockDodgePanel.this.getWidth() - 50 - g.getFontMetrics().stringWidth(highScoreText), 50);
				});
			} catch (IOException e) {
				e.printStackTrace();
			}

			boolean isHighScore0 = isHighScore;
			buffer.add(g -> {
				g.setColor(Color.WHITE);
				g.setFont(g.getFont().deriveFont(20.0f));
				if (isHighScore0) {
					g.setColor(Color.YELLOW);
				}
				g.drawString("Score: " + score, 50, 50);
			});

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
		frame.addKeyListener(keys);
	}

	public BlockDodgeGame getGame() {
		return game;
	}

	public void reset() {
	}

	@Override
	public void paintComponent(Graphics g) {
		if (buffer != null) {
			buffer.forEach(x -> x.accept((Graphics2D) g));
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
