package com.zzpxyx.subplayer.core;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.zzpxyx.subplayer.config.Config;
import com.zzpxyx.subplayer.event.Event;
import com.zzpxyx.subplayer.event.Update;
import com.zzpxyx.subplayer.parser.AdaptiveParser;

public class View implements Observer {
	private static final String OPEN_EMOJI = "O";
	private static final String PREVIOUS_EMOJI = "|<<";
	private static final String BACKWARD_EMOJI = "<<";
	private static final String PLAY_EMOJI = ">";
	private static final String PAUSE_EMOJI = "||";
	private static final String STOP_EMOJI = "><";
	private static final String FORWARD_EMOJI = ">>";
	private static final String NEXT_EMOJI = ">>|";
	private static final String EXIT_EMOJI = "X";
	private static final int SEEKBAR_MAX = 10000;
	private static final Dimension BUTTON_DIMENSION = new Dimension(60, 24);
	private static final Color NO_COLOR = new Color(0, 0, 0, 0);
	private static final Font BUTTON_FONT = new Font(Font.MONOSPACED, Font.BOLD, 12);

	private static enum ActionKey {
		Open, PlayOrPause, Stop, Backward, Forward, Previous, Next, DescreaseSpeed, IncreaseSpeed, Exit, ShowHideButtons
	}

	private static enum RenderMethod {
		Basic, Advanced
	}

	private int mouseCurrentX;
	private int mouseCurrentY;
	private int playSpeed = 100; // Percent.
	private long totalPlayTime = 0;
	private boolean isPlaying = false;
	private boolean isButtonVisible = true;
	private List<String> text = new LinkedList<String>();
	private List<Event> eventList;
	private List<JButton> buttonList = new LinkedList<JButton>();
	private BufferStrategy bufferStrategy;
	private RenderMethod renderMethod;
	private Color displayColor = Color.BLACK;
	private Font displayFont;
	private JFrame frame = new JFrame("SubPlayer");
	private JFileChooser fileChooser = new JFileChooser();
	private JComboBox<String> encodingComboBox = new JComboBox<>(
			Charset.availableCharsets().keySet().toArray(new String[0]));
	private JTextArea contentTextArea = new JTextArea();
	private JPanel controlPanel = new JPanel(new GridBagLayout());
	private JButton openButton = new JButton(OPEN_EMOJI);
	private JButton previousButton = new JButton(PREVIOUS_EMOJI);
	private JButton backwardButton = new JButton(BACKWARD_EMOJI);
	private JButton playOrPauseButton = new JButton(PLAY_EMOJI);
	private JButton stopButton = new JButton(STOP_EMOJI);
	private JButton forwardButton = new JButton(FORWARD_EMOJI);
	private JButton nextButton = new JButton(NEXT_EMOJI);
	private JButton exitButton = new JButton(EXIT_EMOJI);
	private JPanel previewPanel = new JPanel(new BorderLayout());
	private JPanel displayPanel = new JPanel() {
		private static final long serialVersionUID = 1L;

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (g instanceof Graphics2D) {
				int componentWidth = getWidth();
				int componentHeight = getHeight();
				int textWidth;
				int textHeight;
				int originX;
				int originY = 0;
				Graphics2D graphics2d = (Graphics2D) g;
				graphics2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
						RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				graphics2d.setBackground(displayColor);
				graphics2d.clearRect(0, 0, componentWidth, componentHeight);
				graphics2d.setFont(displayFont);
				FontMetrics fontMetrics = graphics2d.getFontMetrics();
				synchronized (View.this) {
					for (String line : text) {
						Rectangle2D rectangle2D = fontMetrics.getStringBounds(line, graphics2d);
						textWidth = (int) rectangle2D.getWidth();
						textHeight = (int) rectangle2D.getHeight();
						originX = (componentWidth - textWidth) / 2; // Center the text.
						graphics2d.setColor(Color.BLACK);
						graphics2d.fillRect(originX, originY, textWidth, textHeight);
						graphics2d.setColor(Color.WHITE);
						graphics2d.drawString(line, originX, originY + fontMetrics.getAscent());
						originY += textHeight;
					}
				}
			}
		};
	};
	private JProgressBar seekBar = new JProgressBar();
	private JLabel currentTimeLabel = new JLabel("0:00:00");
	private JLabel totalTimeLabel = new JLabel("0:00:00");
	private JSpinner playSpeedSpinner = new JSpinner(new SpinnerNumberModel(100, 50, 200, 2));

	public View(Properties config) {
		displayFont = new Font(Font.SANS_SERIF, Font.BOLD, Integer.parseInt(config.getProperty(Config.FONT_SIZE)));

		if (System.getProperty("os.name").toLowerCase().startsWith("linux")) {
			renderMethod = RenderMethod.Advanced;
		} else {
			renderMethod = RenderMethod.Basic;
		}

		encodingComboBox.setSelectedItem(Charset.defaultCharset().name());
		encodingComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updatePreview();
			}

		});

		previewPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0),
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)));
		previewPanel.add(encodingComboBox, BorderLayout.NORTH);
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

		displayPanel.setFocusable(false);
		displayPanel.addMouseListener(new MouseAdapter() {
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
		displayPanel.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				frame.setLocation(e.getXOnScreen() - mouseCurrentX + frame.getX(),
						e.getYOnScreen() - mouseCurrentY + frame.getY());
				mouseCurrentX = e.getXOnScreen();
				mouseCurrentY = e.getYOnScreen();
			}
		});
		displayPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("H"),
				ActionKey.ShowHideButtons);
		displayPanel.getActionMap().put(ActionKey.ShowHideButtons, new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				showHideButtons();
			}
		});

		buttonList.add(openButton);
		buttonList.add(exitButton);
		buttonList.add(playOrPauseButton);
		buttonList.add(stopButton);
		buttonList.add(backwardButton);
		buttonList.add(forwardButton);
		buttonList.add(previousButton);
		buttonList.add(nextButton);
		for (JButton button : buttonList) {
			button.setPreferredSize(BUTTON_DIMENSION);
			button.setFont(BUTTON_FONT);
			button.setFocusable(false);
		}

		NumberEditor playSpeedSpinnerEditor = (NumberEditor) playSpeedSpinner.getEditor();
		playSpeedSpinnerEditor.getTextField().setEditable(false);
		playSpeedSpinnerEditor.getFormat().setPositiveSuffix("%");

		seekBar.setMaximum(SEEKBAR_MAX);

		controlPanel.setFocusable(false);
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.WEST;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.insets = new Insets(10, 10, 10, 0);
		constraints.gridx = 0;
		controlPanel.add(openButton, constraints);
		constraints.gridx = 2;
		controlPanel.add(playOrPauseButton, constraints);
		constraints.gridx = 4;
		controlPanel.add(backwardButton, constraints);
		constraints.gridx = 6;
		controlPanel.add(previousButton, constraints);
		constraints.gridx = 8;
		controlPanel.add(playSpeedSpinner, constraints);
		constraints.gridx = 9;
		controlPanel.add(currentTimeLabel, constraints);
		constraints.gridx = 10;
		constraints.weightx = 1;
		controlPanel.add(seekBar, constraints);
		constraints.insets = new Insets(10, 0, 10, 0);
		constraints.weightx = 0;
		constraints.gridx = 1;
		controlPanel.add(exitButton, constraints);
		constraints.gridx = 3;
		controlPanel.add(stopButton, constraints);
		constraints.gridx = 5;
		controlPanel.add(forwardButton, constraints);
		constraints.gridx = 7;
		controlPanel.add(nextButton, constraints);
		constraints.insets = new Insets(10, 10, 10, 10);
		constraints.gridx = 11;
		controlPanel.add(totalTimeLabel, constraints);

		Container contentPane = frame.getContentPane();
		contentPane.add(displayPanel, BorderLayout.CENTER);
		contentPane.add(controlPanel, BorderLayout.SOUTH);

		frame.getRootPane().putClientProperty("Window.shadow", false); // Remove window shadow, especially for macOS.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setAlwaysOnTop(true);
		frame.setPreferredSize(new Dimension(Integer.parseInt(config.getProperty(Config.WINDOW_WIDTH)),
				Integer.parseInt(config.getProperty(Config.WINDOW_HEIGHT))));
		frame.setLocation(Integer.parseInt(config.getProperty(Config.WINDOW_X_POSITION)),
				Integer.parseInt(config.getProperty(Config.WINDOW_Y_POSITION)));
		frame.setUndecorated(true);
		frame.setBackground(NO_COLOR);
		frame.pack();
		frame.setVisible(true);
		frame.createBufferStrategy(2);
		bufferStrategy = frame.getBufferStrategy();
	}

	public void addController(Controller controller) {
		// Open.
		Action openAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
					controller.setEventList(eventList);
					totalPlayTime = eventList.get(eventList.size() - 1).time;
					totalTimeLabel.setText(formatDuration(totalPlayTime));
					changePlayState(false);
				}
			}
		};
		openButton.addActionListener(openAction);
		displayPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("O"), ActionKey.Open);
		displayPanel.getActionMap().put(ActionKey.Open, openAction);

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
		displayPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),
				ActionKey.PlayOrPause);
		displayPanel.getActionMap().put(ActionKey.PlayOrPause, playOrPauseAction);

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
		displayPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("S"), ActionKey.Stop);
		displayPanel.getActionMap().put(ActionKey.Stop, stopAction);

		// Backward.
		Action backwardAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.backward();
			}
		};
		backwardButton.addActionListener(backwardAction);
		displayPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("B"),
				ActionKey.Backward);
		displayPanel.getActionMap().put(ActionKey.Backward, backwardAction);

		// Forward.
		Action forwardAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.forward();
			}
		};
		forwardButton.addActionListener(forwardAction);
		displayPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F"), ActionKey.Forward);
		displayPanel.getActionMap().put(ActionKey.Forward, forwardAction);

		// Previous.
		Action previousAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.previous();
			}
		};
		previousButton.addActionListener(previousAction);
		displayPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("P"),
				ActionKey.Previous);
		displayPanel.getActionMap().put(ActionKey.Previous, previousAction);

		// Next.
		Action nextAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.next();
			}
		};
		nextButton.addActionListener(nextAction);
		displayPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("N"), ActionKey.Next);
		displayPanel.getActionMap().put(ActionKey.Next, nextAction);

		// Set speed.
		playSpeedSpinner.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				int newSpeed = (int) playSpeedSpinner.getValue();
				if (newSpeed != playSpeed) {
					controller.setSpeed(newSpeed);
					playSpeed = newSpeed;
				}
			}
		});

		// Decrease speed.
		Action decreaseSpeedAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (playSpeed > 50) {
					playSpeedSpinner.setValue(playSpeed - 2);
				}
			}
		};
		displayPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("D"),
				ActionKey.DescreaseSpeed);
		displayPanel.getActionMap().put(ActionKey.DescreaseSpeed, decreaseSpeedAction);

		// Increase speed.
		Action increaseSpeedAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (playSpeed < 200) {
					playSpeedSpinner.setValue(playSpeed + 2);
				}
			}
		};
		displayPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("I"),
				ActionKey.IncreaseSpeed);
		displayPanel.getActionMap().put(ActionKey.IncreaseSpeed, increaseSpeedAction);

		// Exit.
		Action exitAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		};
		exitButton.addActionListener(exitAction);
		displayPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"),
				ActionKey.Exit);
		displayPanel.getActionMap().put(ActionKey.Exit, exitAction);

		// Seek bar.
		seekBar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				seekBar.setValue(e.getX() * SEEKBAR_MAX / seekBar.getWidth());
				controller.jumpToTime(e.getX() * totalPlayTime / seekBar.getWidth());
			}
		});
	}

	private void changePlayState(boolean newPlayState) {
		this.isPlaying = newPlayState;
		playOrPauseButton.setText(newPlayState ? PAUSE_EMOJI : PLAY_EMOJI);
	}

	private void showHideButtons() {
		isButtonVisible = !isButtonVisible;
		frame.setVisible(false);
		displayColor = isButtonVisible ? Color.BLACK : NO_COLOR;
		controlPanel.setVisible(isButtonVisible);
		frame.setVisible(true);
	}

	private void updatePreview() {
		File file = fileChooser.getSelectedFile();
		if (file != null) {
			try {
				eventList = AdaptiveParser.getEventList(file.getAbsolutePath(),
						encodingComboBox.getSelectedItem().toString());
				String sample = eventList.stream().filter(t -> t.type == Event.Type.Start).limit(10).map(t -> t.text)
						.collect(Collectors.joining(System.lineSeparator()));
				contentTextArea.setText(sample);
			} catch (IOException e) {
				contentTextArea.setText("Cannot open the selected file with the specified encoding.");
			}
		}
	}

	private String formatDuration(long millis) {
		millis /= 1000;
		return String.format("%d:%02d:%02d", millis / 3600, (millis % 3600) / 60, millis % 60);
	}

	@Override
	public void update(Observable model, Object arg) {
		if (arg instanceof Update) {
			Update update = (Update) arg;
			synchronized (View.this) {
				text = update.text;
			}
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					switch (renderMethod) {
					case Basic:
						frame.repaint();
						break;
					case Advanced:
						do {
							do {
								Graphics graphics = bufferStrategy.getDrawGraphics();
								frame.update(graphics);
								graphics.dispose();
							} while (bufferStrategy.contentsRestored());
							bufferStrategy.show();
						} while (bufferStrategy.contentsLost());
						break;
					}
					if (totalPlayTime == 0) {
						seekBar.setValue(0);
						currentTimeLabel.setText("0:00:00");
					} else {
						seekBar.setValue((int) (update.time * SEEKBAR_MAX / totalPlayTime));
						currentTimeLabel.setText(formatDuration(update.time));
					}
				}
			});
		}
	}
}
