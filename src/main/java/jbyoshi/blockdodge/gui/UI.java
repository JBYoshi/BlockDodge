package jbyoshi.blockdodge.gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

final class UI {

	static JLabel label(String text, float size) {
		JLabel label = label(text);
		label.setFont(label.getFont().deriveFont(size));
		return label;
	}

	static BlockDodgeButton button(String text, Color c, ActionListener l) {
		BlockDodgeButton button = new BlockDodgeButton(text, c, l);
		button.setAlignmentX(0.5f);
		return button;
	}

	static JLabel label(String text) {
		JLabel label = new JLabel(text);
		label.setForeground(Color.WHITE);
		label.setAlignmentX(0.5f);
		return label;
	}

}
