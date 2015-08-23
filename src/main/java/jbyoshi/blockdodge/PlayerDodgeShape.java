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

public final class PlayerDodgeShape extends DodgeShape implements KeyListener, FocusListener {
	private static final Color COLOR = Color.WHITE;
	private static final int SIZE = 32;
	private volatile boolean up, down, left, right, quit, pause;
	private static final double SQRT_HALF = Math.sqrt(0.5);

	public PlayerDodgeShape(BlockDodge game) {
		super(game, 0, 0, SIZE, SIZE, COLOR);
	}

	@Override
	public void move() {
		while (pause) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
		if (quit) {
			explode();
			return;
		}
		double move = left != right && up != down ? SQRT_HALF : 1;
		if (left && getX() >= move) {
			setX(getX() - move);
		}
		if (right && getX() < game.getWidth() - getWidth() - move) {
			setX(getX() + move);
		}
		if (up && getY() >= move) {
			setY(getY() - move);
		}
		if (down && getY() < game.getHeight() - getHeight() - move) {
			setY(getY() + move);
		}
	}

	void reset() {
		pause = false;
		quit = false;
		setX(game.getWidth() / 2 - getWidth() / 2);
		setY(game.getHeight() / 2 - getHeight() / 2);
	}

	@Override
	void onRemoved() {
		game.stop();
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			up = true;
			break;
		case KeyEvent.VK_DOWN:
			down = true;
			break;
		case KeyEvent.VK_LEFT:
			left = true;
			break;
		case KeyEvent.VK_RIGHT:
			right = true;
			break;
		case KeyEvent.VK_SPACE:
		case KeyEvent.VK_ENTER:
			if (pause) {
				synchronized (this) {
					quit = true;
					setPaused(false);
				}
			}
			break;
		case KeyEvent.VK_ESCAPE:
			setPaused(!pause);
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			up = false;
			break;
		case KeyEvent.VK_DOWN:
			down = false;
			break;
		case KeyEvent.VK_LEFT:
			left = false;
			break;
		case KeyEvent.VK_RIGHT:
			right = false;
			break;
		}
	}

	private synchronized void setPaused(boolean paused) {
		pause = paused;
		game.pauseScreen.setVisible(paused);
	}

	@Override
	public void focusGained(FocusEvent e) {
	}

	@Override
	public void focusLost(FocusEvent e) {
		setPaused(true);
	}

}
