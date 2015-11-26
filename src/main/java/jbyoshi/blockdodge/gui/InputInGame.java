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
import java.awt.geom.*;
import java.awt.image.*;

import javax.swing.*;

import jbyoshi.blockdodge.*;

final class InputInGame extends Input implements PlayerController, FocusListener {
	private static final double SQRT_HALF = Math.sqrt(0.5);
	private final Cursor visibleCursor, invisibleCursor;
	private final Robot robot;
	private boolean shouldMoveMouse;
	private Container display;
	private BlockDodgeButton pauseButton;

	InputInGame(BlockDodgePanel panel) {
		super(panel);
		Robot robot = null;
		try {
			robot = new Robot(panel.frame.getGraphicsConfiguration().getDevice());
		} catch (AWTException e) {
			e.printStackTrace();
		}
		this.robot = robot;

		BufferedImage image = new BufferedImage(PlayerDodgeShape.SIZE, PlayerDodgeShape.SIZE,
				BufferedImage.TYPE_INT_ARGB);
		this.invisibleCursor = Toolkit.getDefaultToolkit().createCustomCursor(image,
				new Point(PlayerDodgeShape.SIZE / 2, PlayerDodgeShape.SIZE / 2), "Player");
		Graphics2D g = image.createGraphics();
		g.setColor(PlayerDodgeShape.COLOR);
		final int s = PlayerDodgeShape.SIZE;
		g.fillRect(0, 0, s, 1);
		g.fillRect(0, 0, 1, s);
		g.fillRect(s - 1, 0, s, s);
		g.fillRect(0, s - 1, s, s);
		g.dispose();
		this.visibleCursor = Toolkit.getDefaultToolkit().createCustomCursor(image,
				new Point(PlayerDodgeShape.SIZE / 2, PlayerDodgeShape.SIZE / 2), "Player");
	}

	@Override
	public synchronized Point2D move(PlayerDodgeShape player) {
		boolean left = panel.keys.isPressed(KeyEvent.VK_LEFT);
		boolean right = panel.keys.isPressed(KeyEvent.VK_RIGHT);
		boolean up = panel.keys.isPressed(KeyEvent.VK_UP);
		boolean down = panel.keys.isPressed(KeyEvent.VK_DOWN);

		if (left || right || up || down) {
			panel.setCursor(invisibleCursor);
			double move = left != right && up != down ? SQRT_HALF : 1;
			double x = player.getX();
			if (left && !right) {
				x -= move;
			} else if (right && !left) {
				x += move;
			}
			double y = player.getY();
			if (up && !down) {
				y -= move;
			} else if (down && !up) {
				y += move;
			}

			moveMouse(player);
			return new Point2D.Double(x, y);
		}

		if (shouldMoveMouse) {
			moveMouse(player);
			return new Point2D.Double(player.getX(), player.getY());
		}

		panel.setCursor(visibleCursor);

		Point2D startLoc = new Point2D.Double(player.getX(), player.getY());
		Point2D endLoc = panel.getMousePosition();
		if (endLoc == null) {
			return new Point2D.Double(player.getX(), player.getY());
		}
		endLoc = new Point2D.Double(endLoc.getX() - player.getWidth() / 2, endLoc.getY() - player.getHeight() / 2);
		Point2D difference = new Point2D.Double(endLoc.getX() - startLoc.getX(), endLoc.getY() - startLoc.getY());
		if (endLoc.distance(startLoc) <= 1.0) {
			return endLoc;
		}

		double length = difference.distance(0, 0);
		return new Point2D.Double(startLoc.getX() + difference.getX() / length,
				startLoc.getY() + difference.getY() / length);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_ESCAPE:
			game.setPaused(true);
			break;
		case KeyEvent.VK_DELETE:
			game.addTask(() -> panel.player.explode());
			break;
		}
	}

	@Override
	void activate() {
		super.activate();
		panel.frame.addFocusListener(this);
		panel.setCursor(visibleCursor);
		display.add(pauseButton);
		display.revalidate();

		shouldMoveMouse = true;
	}

	@Override
	void deactivate() {
		super.deactivate();
		panel.frame.removeFocusListener(this);
		panel.setCursor(panel.getParent().getCursor());
	}

	@Override
	public void focusGained(FocusEvent e) {
	}

	@Override
	public void focusLost(FocusEvent e) {
		game.setPaused(true);
	}

	private void moveMouse(PlayerDodgeShape player) {
		shouldMoveMouse = false;
		if (robot != null) {
			Point2D loc = panel.getLocationOnScreen();
			robot.mouseMove((int) Math.round(player.getX() + loc.getX() + player.getWidth() / 2),
					(int) Math.round(player.getY() + loc.getY() + player.getHeight() / 2));
		}
	}

	@Override
	Component createDisplay() {
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setOpaque(false);
		pauseButton = UI.button("Pause (Escape)", Color.WHITE, e -> game.setPaused(true));
		pauseButton.setLocation(50, 50 + UI.label("Size test").getPreferredSize().height * 3 / 2);
		pauseButton.setSize(pauseButton.getPreferredSize());
		panel.add(pauseButton);
		display = panel;
		return panel;
	}

	@Override
	public void playerDied(PlayerDodgeShape player) {
		panel.setCursor(invisibleCursor);
		display.remove(pauseButton);
		display.revalidate();
	}

}
