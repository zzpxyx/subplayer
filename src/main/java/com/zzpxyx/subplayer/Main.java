package subplayer;

import java.awt.Color;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import subplayer.parser.SrtParser;

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
		frame.setVisible(true);

		// List<Subtitle> subtitleList=SrtParser.getSubtitles("/tmp/a.srt");
		// textPane.setText(subtitleList.get(0).text);
		textPane.setText("hello\nhello hello");

		SimpleAttributeSet textStyle = new SimpleAttributeSet();
		StyleConstants.setAlignment(textStyle, StyleConstants.ALIGN_CENTER);
		StyledDocument textDoc = textPane.getStyledDocument();
		textDoc.setParagraphAttributes(0, textDoc.getLength(), textStyle, true);
		textPane.setText("world\nworld world");
	}
}
