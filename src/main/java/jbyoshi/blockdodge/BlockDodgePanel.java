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
import java.awt.image.*;

import javax.swing.*;

public final class BlockDodgePanel extends JPanel {
	private static final long serialVersionUID = 6904399199721821562L;
	private final JComponent pauseScreen = Box.createVerticalBox();
	private volatile BufferedImage buffer;
	private static final String COPYRIGHT_TEXT = "Copyright 2015 JBYoshi        github.com/JBYoshi/BlockDodge";
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
			g.drawString("Score: " + getScore(), 50, 50);

			if (!includePlayer) {
				g.setFont(g.getFont().deriveFont(10.0f));
				int textWidth = g.getFontMetrics().stringWidth(COPYRIGHT_TEXT);
				int textHeight = g.getFontMetrics().getHeight();
				g.drawString(COPYRIGHT_TEXT, getWidth() / 2 - textWidth / 2, getHeight() - 10 - textHeight);
			}
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
