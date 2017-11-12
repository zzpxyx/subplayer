package com.zzpxyx.subplayer.core;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class View implements Observer {
	private JTextPane textPane = new JTextPane();

	public View() {
		JFrame frame = new JFrame("SubPlayer");
		JPanel panel = new JPanel();

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setSize(500, 500);
		// frame.setUndecorated(true);
		// frame.setOpacity(0.25f);
		frame.add(panel);

		panel.add(textPane);

		SimpleAttributeSet textStyle = new SimpleAttributeSet();
		StyleConstants.setAlignment(textStyle, StyleConstants.ALIGN_CENTER);
		StyledDocument textDoc = textPane.getStyledDocument();
		textDoc.setParagraphAttributes(0, 0, textStyle, true);

		frame.setVisible(true);
	}

	@Override
	public void update(Observable model, Object arg) {
		if (arg instanceof List<?>) {
			List<?> visibleSubtitleList = (List<?>) arg;
			String text = "";
			for (Object obj : visibleSubtitleList) {
				if (obj instanceof Subtitle) {
					text += ((Subtitle) obj).text + "\n";
				}
			}
			textPane.setText(text);
		}
	}
}
