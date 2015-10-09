package jbyoshi.blockdodge.gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import jbyoshi.blockdodge.*;
import jbyoshi.blockdodge.gui.*;

public final class InputMainMenu implements Input, KeyListener {
	private final BlockDodge panel;
	private final Component info;
	private final BlockDodgeGame game;

	public InputMainMenu(BlockDodge panel, Component info) {
		this.panel = panel;
		this.game = panel.getGame();
		this.info = info;
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_ENTER:
		case KeyEvent.VK_SPACE:
			game.addTask(game::stop);
			break;
		case KeyEvent.VK_F11:
			JFrame frame = panel.frame;
			GraphicsDevice device = frame.getGraphicsConfiguration().getDevice();
			if (device.isFullScreenSupported() && device.getFullScreenWindow() != frame) {
				frame.dispose();
				frame.setUndecorated(true);
				device.setFullScreenWindow(frame);
			} else {
				frame.dispose();
				device.setFullScreenWindow(null);
				frame.setUndecorated(false);
				frame.setVisible(true);
			}
			frame.revalidate();
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void activate() {
		panel.addKeyListener(this);
		info.setVisible(true);
	}

	@Override
	public void deactivate() {
		panel.removeKeyListener(this);
		info.setVisible(false);
	}

}
