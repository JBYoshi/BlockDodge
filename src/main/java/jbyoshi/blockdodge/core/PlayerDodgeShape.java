package jbyoshi.blockdodge.core;

import java.awt.*;

public final class PlayerDodgeShape extends DodgeShape {
	private static final Color COLOR = Color.WHITE;
	private static final int SIZE = 32;

	public PlayerDodgeShape(BlockDodge game) {
		super(game, 0, 0, SIZE, SIZE, COLOR);
	}

	@Override
	public void move() {
		// TODO Auto-generated method stub

	}

	void reset() {
		setX(game.getWidth() / 2 - getWidth() / 2);
		setY(game.getHeight() / 2 - getHeight() / 2);
	}

}
