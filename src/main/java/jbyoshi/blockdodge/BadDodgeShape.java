package jbyoshi.blockdodge;

import java.awt.*;

public class BadDodgeShape extends BounceDodgeShape {

	public BadDodgeShape(BlockDodge game, double x, double y, double w, double h, Color c, float dir) {
		super(game, x, y, w, h, c, dir);
	}

	@Override
	public void move() {
		super.move();
		if (intersects(game.getPlayer())) {
			game.getPlayer().explode();
		}
	}

}
