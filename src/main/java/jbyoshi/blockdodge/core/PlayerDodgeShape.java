package jbyoshi.blockdodge.core;

import java.awt.*;
import java.awt.event.*;

public final class PlayerDodgeShape extends DodgeShape implements KeyListener {
	private static final Color COLOR = Color.WHITE;
	private static final int SIZE = 32;
	private boolean up, down, left, right;

	public PlayerDodgeShape(BlockDodge game) {
		super(game, 0, 0, SIZE, SIZE, COLOR);
	}

	@Override
	public void move() {
		if (left) {
			setX(getX() - 1);
		}
		if (right) {
			setX(getX() + 1);
		}
		if (up) {
			setY(getY() - 1);
		}
		if (down) {
			setY(getY() + 1);
		}
	}

	void reset() {
		setX(game.getWidth() / 2 - getWidth() / 2);
		setY(game.getHeight() / 2 - getHeight() / 2);
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

}
