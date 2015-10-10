package jbyoshi.blockdodge.util;

import java.awt.event.*;
import java.util.*;

public final class KeyTracker implements KeyListener {
	private final Set<Integer> presses = new HashSet<>();

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		presses.add(e.getKeyCode());
	}

	@Override
	public void keyReleased(KeyEvent e) {
		presses.remove(e.getKeyCode());
	}

	public boolean isPressed(int keyCode) {
		return presses.contains(keyCode);
	}

}
