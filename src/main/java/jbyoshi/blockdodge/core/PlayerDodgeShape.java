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
		x = game.getWidth() / 2 - width / 2;
		y = game.getHeight() / 2 - height / 2;
	}

}
