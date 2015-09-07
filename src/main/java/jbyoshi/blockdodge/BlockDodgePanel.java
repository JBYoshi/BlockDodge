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
package jbyoshi.blockdodge;

import java.awt.*;
import java.awt.image.*;
import java.util.prefs.*;

import javax.swing.*;

public final class BlockDodgePanel extends JPanel {
	private static final long serialVersionUID = 6904399199721821562L;
	private boolean isHighScore = false;
	private final JComponent pauseScreen = Box.createVerticalBox();
	private volatile BufferedImage buffer;
	private final BlockDodgeGame game = new BlockDodgeGame() {

		@Override
		protected void updatePaused(boolean paused) {
			pauseScreen.setVisible(paused);
		}

		@Override
		protected void paint(boolean includePlayer) {
			BufferedImage buffer = new BufferedImage(Math.max(1, getWidth()), Math.max(1, getHeight()),
					BufferedImage.TYPE_INT_RGB);
			Graphics2D g = buffer.createGraphics();

			g.setColor(Color.BLACK);
			g.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());

			for (DodgeShape shape : getShapes()) {
				g.setColor(shape.color);
				g.fill(shape.shape);
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
			BlockDodgePanel.this.requestFocusInWindow();
		}

		@Override
		protected Dimension calculateSize() {
			return getSize();
		}
	};

	public BlockDodgePanel() {
		addKeyListener(game.getPlayer());
		addFocusListener(game.getPlayer());

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

	private static JLabel label(String text) {
		JLabel label = new JLabel(text);
		label.setForeground(Color.WHITE);
		label.setAlignmentX(0.5f);
		return label;
	}
}
