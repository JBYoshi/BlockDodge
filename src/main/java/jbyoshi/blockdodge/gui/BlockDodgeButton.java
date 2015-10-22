package jbyoshi.blockdodge.gui;

import static javax.swing.BorderFactory.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;

public final class BlockDodgeButton extends JButton {
	private static final long serialVersionUID = -8284452011216018490L;

	public BlockDodgeButton(String text, Color color, ActionListener l) {
		super(text);
		addActionListener(l);
		setForeground(color);
		setUI(new BasicButtonUI());

		Border me = new Border() {
			private final Border pressed = createMatteBorder(3, 3, 1, 1, color);
			private final Border released = createLineBorder(color, 2);

			@Override
			public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
				getActive(c).paintBorder(c, g, x, y, width, height);
			}

			@Override
			public Insets getBorderInsets(Component c) {
				return getActive(c).getBorderInsets(c);
			}

			@Override
			public boolean isBorderOpaque() {
				return true;
			}

			private Border getActive(Component c) {
				if (c instanceof AbstractButton && ((AbstractButton) c).getModel().isPressed()) {
					return pressed;
				} else {
					return released;
				}
			}
		};
		setBorder(createCompoundBorder(me, createEmptyBorder(2, 2, 2, 2)));
		setOpaque(false);
		setHorizontalAlignment(SwingConstants.CENTER);
	}
}
