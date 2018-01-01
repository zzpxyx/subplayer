package com.zzpxyx.subplayer.core;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.zzpxyx.subplayer.event.Event;
import com.zzpxyx.subplayer.parser.AdaptvieParser;

public class View implements Observer {
	private static final String OPEN_EMOJI = String.valueOf(Character.toChars(0x1F5C1));
	private static final String PREVIOUS_EMOJI = String.valueOf(Character.toChars(0x23EE));
	private static final String BACKWARD_EMOJI = String.valueOf(Character.toChars(0x23EA));
	private static final String PLAY_EMOJI = String.valueOf(Character.toChars(0x25B6));
	private static final String PAUSE_EMOJI = String.valueOf(Character.toChars(0x23F8));
	private static final String STOP_EMOJI = String.valueOf(Character.toChars(0x23F9));
	private static final String FORWARD_EMOJI = String.valueOf(Character.toChars(0x23E9));
	private static final String NEXT_EMOJI = String.valueOf(Character.toChars(0x23ED));
	private static final String INCREASE_SPEED_EMOJI = String.valueOf(Character.toChars(0x2795));
	private static final String DECREASE_SPEED_EMOJI = String.valueOf(Character.toChars(0x2796));
	private static final String EXIT_EMOJI = String.valueOf(Character.toChars(0x274C));

	private int mouseCurrentX;
	private int mouseCurrentY;
	private boolean isPlaying = false;
	private boolean isButtonVisible = true;
	private String text;
	private List<Event> eventList;
	private JFrame frame = new JFrame("SubPlayer");
	private JTextPane textPane = new JTextPane();
	private JTextArea contentTextArea = new JTextArea();
	private JButton openButton = new JButton(OPEN_EMOJI);
	private JButton previousButton = new JButton(PREVIOUS_EMOJI);
	private JButton backwardButton = new JButton(BACKWARD_EMOJI);
	private JButton playOrPauseButton = new JButton(PLAY_EMOJI);
	private JButton stopButton = new JButton(STOP_EMOJI);
	private JButton forwardButton = new JButton(FORWARD_EMOJI);
	private JButton nextButton = new JButton(NEXT_EMOJI);
	private JButton increaseSpeedButton = new JButton(INCREASE_SPEED_EMOJI);
	private JButton decreaseSpeedButton = new JButton(DECREASE_SPEED_EMOJI);
	private JButton exitButton = new JButton(EXIT_EMOJI);
	private JFileChooser fileChooser = new JFileChooser();
	private JComboBox<String> encodingComboBox = new JComboBox<>(
			Charset.availableCharsets().keySet().toArray(new String[0]));
	private JPanel previewPanel = new JPanel(new BorderLayout());

	public View() {
		encodingComboBox.setSelectedItem(Charset.defaultCharset().name());
		encodingComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updatePreview();
			}

		});

		previewPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0),
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)));
		previewPanel.add(encodingComboBox, BorderLayout.PAGE_START);
		previewPanel.add(contentTextArea, BorderLayout.CENTER);

		fileChooser.setAccessory(previewPanel);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setFileFilter(
				new FileNameExtensionFilter("Subtitle files (*.srt, *.ssa, *.ass)", "srt", "ssa", "ass"));
		fileChooser.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
					updatePreview();
				}
			}
		});

		textPane.setEditable(false);
		textPane.setFocusable(false);
		textPane.setPreferredSize(new Dimension(1500, 120));
		textPane.setBackground(Color.BLACK);
		textPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				mouseCurrentX = e.getXOnScreen();
				mouseCurrentY = e.getYOnScreen();
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					showHideButtons();
				}
			}
		});
		textPane.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				frame.setLocation(e.getXOnScreen() - mouseCurrentX + frame.getX(),
						e.getYOnScreen() - mouseCurrentY + frame.getY());
				mouseCurrentX = e.getXOnScreen();
				mouseCurrentY = e.getYOnScreen();
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
		constraints.gridx = 7;
		pane.add(decreaseSpeedButton, constraints);
		constraints.gridx = 8;
		pane.add(increaseSpeedButton, constraints);
		constraints.gridx = 9;
		pane.add(exitButton, constraints);

		for (Component component : frame.getContentPane().getComponents()) {
			if (component instanceof JButton) {
				component.setFocusable(false);
			}
		}

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
				if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
					controller.setEventList(eventList);
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
				controller.playOrPause(!isPlaying);
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

		// Increase speed.
		Action increaseSpeedAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.increaseSpeed();
			}
		};
		increaseSpeedButton.addActionListener(increaseSpeedAction);
		textPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("I"), "IncreaseSpeed");
		textPane.getActionMap().put("IncreaseSpeed", increaseSpeedAction);

		// Decrease speed.
		Action decreaseSpeedAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.decreaseSpeed();
			}
		};
		decreaseSpeedButton.addActionListener(decreaseSpeedAction);
		textPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("D"), "DecreaseSpeed");
		textPane.getActionMap().put("DecreaseSpeed", decreaseSpeedAction);

		// Exit.
		Action exitAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		};
		exitButton.addActionListener(exitAction);
		textPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "Exit");
		textPane.getActionMap().put("Exit", exitAction);
	}

	private void changePlayState(boolean newPlayState) {
		this.isPlaying = newPlayState;
		playOrPauseButton.setText(newPlayState ? PAUSE_EMOJI : PLAY_EMOJI);
	}

	private void showHideButtons() {
		isButtonVisible = !isButtonVisible;
		for (Component component : frame.getContentPane().getComponents()) {
			if (component instanceof JButton) {
				component.setVisible(isButtonVisible);
			}
		}
		frame.pack();
	}

	private void updatePreview() {
		File file = fileChooser.getSelectedFile();
		if (file != null) {
			try {
				eventList = AdaptvieParser.getEventList(file.getAbsolutePath(),
						encodingComboBox.getSelectedItem().toString());
				String sample = eventList.stream().filter(t -> t.type == Event.Type.Start).limit(10).map(t -> t.text)
						.collect(Collectors.joining(System.lineSeparator()));
				contentTextArea.setText(sample);
			} catch (IOException e) {
				contentTextArea.setText("Cannot open the selected file with the specified encoding.");
			}

		}
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
