package jbyoshi.blockdodge.gui;

import java.awt.*;
import java.awt.event.*;

import jbyoshi.blockdodge.*;

public final class InputPauseMenu implements Input, KeyListener {
	private final BlockDodge panel;
	private final BlockDodgeGame game;
	private final Component pauseMenu;

	public InputPauseMenu(BlockDodge panel, Component pauseMenu) {
		this.panel = panel;
		this.game = panel.getGame();
		this.pauseMenu = pauseMenu;
	}
	@Override
	public void activate() {
		panel.addKeyListener(this);
		pauseMenu.setVisible(true);
	}

	@Override
	public void deactivate() {
		panel.removeKeyListener(this);
		pauseMenu.setVisible(false);
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_SPACE:
		case KeyEvent.VK_ENTER:
		case KeyEvent.VK_ESCAPE:
			game.setPaused(false);
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

}
