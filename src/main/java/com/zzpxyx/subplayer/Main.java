package com.zzpxyx.subplayer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.zzpxyx.subplayer.core.Model;
import com.zzpxyx.subplayer.core.Subtitle;
import com.zzpxyx.subplayer.parser.SrtParser;

public class Main {
	public static void main(String[] args) {
		JFrame frame = new JFrame("SubPlayer");
		JPanel panel = new JPanel();
		JTextPane textPane = new JTextPane();

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

		// List<Subtitle> subtitleList=SrtParser.getSubtitles("/tmp/a.srt");
		// textPane.setText(subtitleList.get(0).text);
		ArrayList<Subtitle> subtitleList = new ArrayList<Subtitle>();
		subtitleList.add(new Subtitle(0, 1000, "hello\nhello hello"));
		subtitleList.add(new Subtitle(2000, 3000, "world\nworld world"));
		new Model(subtitleList).Play();

		// textPane.setText("world\nworld world");
	}
}
