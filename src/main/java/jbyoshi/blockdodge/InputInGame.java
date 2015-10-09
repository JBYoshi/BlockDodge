package jbyoshi.blockdodge;

import java.awt.event.*;

import com.flowpowered.math.vector.*;

public final class InputInGame implements PlayerController, KeyListener {
	private final BlockDodgeGame game;
	private volatile boolean up, down, left, right;

	public InputInGame(BlockDodgeGame game) {
		this.game = game;
	}

	@Override
	public Vector2d getMovement() {
		return new Vector2d(up == down ? 0 : up ? -1 : 1, left == right ? 0 : left ? -1 : 1).normalize();
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

}
