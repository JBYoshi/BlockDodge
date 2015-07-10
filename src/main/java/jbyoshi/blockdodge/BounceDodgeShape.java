package jbyoshi.blockdodge;

import java.awt.*;

public class BounceDodgeShape extends DodgeShape {
	private double xMove, yMove;

	public BounceDodgeShape(BlockDodge game, double x, double y, double w, double h, Color c, float dir) {
		super(game, x, y, w, h, c);
		this.xMove = Math.sin(dir);
		this.yMove = -Math.cos(dir);
	}

	@Override
	public void move() {
		x += xMove;
		y += yMove;
		if (xMove > 0) {
			// Moving right
			if (x + width >= game.getWidth()) {
				// Hit right wall
				x = game.getWidth() - x - width + game.getWidth() - width;
				xMove = -xMove;
			}
		} else if (xMove < 0) {
			// Moving left
			if (x < 0) {
				// Hit left wall
				x = -x;
				xMove = -xMove;
			}
		}

		if (yMove > 0) {
			// Moving down
			if (y + height >= game.getHeight()) {
				// Hit bottom wall
				y = game.getHeight() - y - height + game.getHeight() - height;
				yMove = -yMove;
			}
		} else if (yMove < 0) {
			// Moving up
			if (y < 0) {
				// Hit top wall
				y = -y;
				yMove = -yMove;
			}
		}
	}
}
