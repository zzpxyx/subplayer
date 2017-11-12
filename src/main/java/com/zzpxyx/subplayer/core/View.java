package com.zzpxyx.subplayer.core;

import java.awt.Color;
import java.awt.Dimension;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.stream.Collectors;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class View implements Observer {
	private JTextPane textPane = new JTextPane();
	private String text;

	public View() {
		JFrame frame = new JFrame("SubPlayer");
		JPanel panel = new JPanel();

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setSize(1200, 500);
		// frame.setUndecorated(true);
		// frame.setOpacity(0.25f);
		frame.add(panel);

		panel.add(textPane);
		textPane.setEditable(false);
		textPane.setPreferredSize(new Dimension(1000, 200));
		textPane.setBackground(Color.BLACK);

		SimpleAttributeSet textStyle = new SimpleAttributeSet();
		StyleConstants.setAlignment(textStyle, StyleConstants.ALIGN_CENTER);
		StyleConstants.setForeground(textStyle, Color.WHITE);
		StyleConstants.setFontSize(textStyle, 32);
		StyleConstants.setBold(textStyle, true);
		StyledDocument textDoc = textPane.getStyledDocument();
		textDoc.setParagraphAttributes(0, 0, textStyle, true);

		frame.setVisible(true);
	}

	@Override
	public void update(Observable model, Object arg) {
		if (arg instanceof List<?>) {
			List<?> visibleSubtitleList = (List<?>) arg;
			text = visibleSubtitleList.stream().filter(o -> o instanceof Subtitle).map(s -> ((Subtitle) s).text)
					.collect(Collectors.joining(System.lineSeparator()));
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					textPane.setText(text);
				}
			});
		}
	}
}
