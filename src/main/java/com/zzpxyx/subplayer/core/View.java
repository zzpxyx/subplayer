package com.zzpxyx.subplayer.core;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class View implements Observer {
	private static final String OPEN_EMOJI = String.valueOf(Character.toChars(0x23CF));
	private static final String PREVIOUS_EMOJI = String.valueOf(Character.toChars(0x23EE));
	private static final String BACKWARD_EMOJI = String.valueOf(Character.toChars(0x23EA));
	private static final String PLAY_EMOJI = String.valueOf(Character.toChars(0x25B6));
	private static final String PAUSE_EMOJI = String.valueOf(Character.toChars(0x23F8));
	private static final String STOP_EMOJI = String.valueOf(Character.toChars(0x23F9));
	private static final String FORWARD_EMOJI = String.valueOf(Character.toChars(0x23E9));
	private static final String NEXT_EMOJI = String.valueOf(Character.toChars(0x23ED));

	private boolean isPlaying = false;
	private boolean isButtonVisible = true;
	private JFrame frame = new JFrame("SubPlayer");
	private JTextPane textPane = new JTextPane();
	private JFileChooser fileChooser = new JFileChooser();
	private JButton openButton = new JButton(OPEN_EMOJI);
	private JButton previousButton = new JButton(PREVIOUS_EMOJI);
	private JButton backwardButton = new JButton(BACKWARD_EMOJI);
	private JButton playOrPauseButton = new JButton(PLAY_EMOJI);
	private JButton stopButton = new JButton(STOP_EMOJI);
	private JButton forwardButton = new JButton(FORWARD_EMOJI);
	private JButton nextButton = new JButton(NEXT_EMOJI);
	private String text;

	public View() {
		textPane.setEditable(false);
		textPane.setFocusable(false);
		textPane.setPreferredSize(new Dimension(1500, 120));
		textPane.setBackground(Color.BLACK);
		textPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					showHideButtons();
				}
			}
		});
		textPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("H"), "ShowHideButtons");
		textPane.getActionMap().put("ShowHideButtons", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				showHideButtons();
			}
		});

		SimpleAttributeSet textStyle = new SimpleAttributeSet();
		StyleConstants.setAlignment(textStyle, StyleConstants.ALIGN_CENTER);
		StyleConstants.setForeground(textStyle, Color.WHITE);
		StyleConstants.setFontFamily(textStyle, "sans-serif");
		StyleConstants.setFontSize(textStyle, 40);
		StyleConstants.setBold(textStyle, true);
		StyledDocument textDoc = textPane.getStyledDocument();
		textDoc.setParagraphAttributes(0, 0, textStyle, true);

		openButton.setFocusable(false);
		previousButton.setFocusable(false);
		backwardButton.setFocusable(false);
		playOrPauseButton.setFocusable(false);
		stopButton.setFocusable(false);
		forwardButton.setFocusable(false);
		nextButton.setFocusable(false);

		Container pane = frame.getContentPane();
		pane.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		pane.add(textPane, constraints);
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.insets = new Insets(10, 10, 10, 0);
		pane.add(openButton, constraints);
		constraints.gridx = 1;
		pane.add(previousButton, constraints);
		constraints.gridx = 2;
		pane.add(backwardButton, constraints);
		constraints.gridx = 3;
		pane.add(playOrPauseButton, constraints);
		constraints.gridx = 4;
		pane.add(stopButton, constraints);
		constraints.gridx = 5;
		pane.add(forwardButton, constraints);
		constraints.gridx = 6;
		pane.add(nextButton, constraints);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setAlwaysOnTop(true);
		frame.setLocation(200, 900);
		frame.setUndecorated(true);
		frame.pack();
		frame.setVisible(true);
	}

	public void addController(Controller controller) {
		// Open.
		Action openAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				fileChooser.setFileFilter(new FileNameExtensionFilter("Subtitle files", "srt"));
				if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
					String fileName = fileChooser.getSelectedFile().getAbsolutePath();
					controller.setSubtitleFile(fileName);
					changePlayState(false);
				}
			}
		};
		openButton.addActionListener(openAction);
		textPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("O"), "Open");
		textPane.getActionMap().put("Open", openAction);

		// Previous.
		Action previousAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.previous();
			}
		};
		previousButton.addActionListener(previousAction);
		textPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("P"), "Previous");
		textPane.getActionMap().put("Previous", previousAction);

		// Backward.
		Action backwardAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.backward();
			}
		};
		backwardButton.addActionListener(backwardAction);
		textPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("B"), "Backward");
		textPane.getActionMap().put("Backward", backwardAction);

		// Play or pause.
		Action playOrPauseAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.playOrPause();
				changePlayState(!isPlaying);
			}
		};
		playOrPauseButton.addActionListener(playOrPauseAction);
		textPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "PlayOrPause");
		textPane.getActionMap().put("PlayOrPause", playOrPauseAction);

		// Stop.
		Action stopAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.stop();
				changePlayState(false);
			}
		};
		stopButton.addActionListener(stopAction);
		textPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("S"), "Stop");
		textPane.getActionMap().put("Stop", stopAction);

		// Forward.
		Action forwardAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.forward();
			}
		};
		forwardButton.addActionListener(forwardAction);
		textPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F"), "Forward");
		textPane.getActionMap().put("Forward", forwardAction);

		// Next.
		Action nextAction = new AbstractAction() {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.next();
			}
		};
		nextButton.addActionListener(nextAction);
		textPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("N"), "Next");
		textPane.getActionMap().put("Next", nextAction);
	}

	private void changePlayState(boolean newPlayState) {
		this.isPlaying = newPlayState;
		playOrPauseButton.setText(newPlayState ? PAUSE_EMOJI : PLAY_EMOJI);
	}

	private void showHideButtons() {
		isButtonVisible = !isButtonVisible;
		openButton.setVisible(isButtonVisible);
		previousButton.setVisible(isButtonVisible);
		backwardButton.setVisible(isButtonVisible);
		playOrPauseButton.setVisible(isButtonVisible);
		stopButton.setVisible(isButtonVisible);
		forwardButton.setVisible(isButtonVisible);
		nextButton.setVisible(isButtonVisible);
		frame.pack();
	}

	@Override
	public void update(Observable model, Object arg) {
		if (arg instanceof List<?>) {
			List<?> visibleSubtitleList = (List<?>) arg;
			text = visibleSubtitleList.stream().map(o -> o.toString())
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
