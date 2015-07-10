package jbyoshi.blockdodge;

import java.awt.*;

import jbyoshi.blockdodge.core.*;

public class BadDodgeShape extends BounceDodgeShape {

	public BadDodgeShape(BlockDodge game, double x, double y, double w, double h, Color c, float dir) {
		super(game, x, y, w, h, c, dir);
	}

	@Override
	protected void onCollided(DodgeShape other) {
		if (other == game.getPlayer()) {
			other.explode();
		}
		explode();
	}

}
