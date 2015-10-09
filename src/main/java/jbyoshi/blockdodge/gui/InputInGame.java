package jbyoshi.blockdodge.gui;

import java.awt.event.*;

import com.flowpowered.math.vector.*;

import jbyoshi.blockdodge.*;

public final class InputInGame implements Input, PlayerController, KeyListener {
	private final BlockDodge panel;
	private final BlockDodgeGame game;
	private volatile boolean up, down, left, right;

	public InputInGame(BlockDodge panel) {
		this.panel = panel;
		this.game = panel.getGame();
	}

	@Override
	public synchronized Vector2d getMovement() {
		Vector2d movement = new Vector2d(left == right ? 0 : left ? -1 : 1, up == down ? 0 : up ? -1 : 1);
		// "Cannot normalize the zero vector"
		if (movement.equals(Vector2d.ZERO)) {
			movement = movement.normalize();
		}
		return movement;
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public synchronized void keyPressed(KeyEvent e) {
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
		case KeyEvent.VK_ESCAPE:
			game.addTask(() -> game.setPaused(!game.isPaused()));
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

	@Override
	public void activate() {
		panel.addKeyListener(this);
	}

	@Override
	public void deactivate() {
		panel.removeKeyListener(this);
	}

}
