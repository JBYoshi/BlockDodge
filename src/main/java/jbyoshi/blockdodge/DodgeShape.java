package jbyoshi.blockdodge;

import java.awt.*;
import java.awt.geom.*;

public final class DodgeShape {
	private final int x, y, size, rot;
	private final Shape basedOn;
	private Path2D transformed;

	public DodgeShape(int x, int y, int size, int rot, Shape basedOn) {
		this.x = x;
		this.y = y;
		this.size = size;
		this.rot = rot;
		this.basedOn = basedOn;
		updateTransformed();
	}

	private void updateTransformed() {
		AffineTransform transform = new AffineTransform();
		transform.scale(size, size);
		transform.rotate(rot);
		transform.translate(x, y);
		transformed = new Path2D.Double(basedOn, transform);
	}

	public boolean collides(Rectangle2D player) {
		return transformed.intersects(player);
	}
}
