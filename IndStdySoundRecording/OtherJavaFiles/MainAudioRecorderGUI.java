//package soundRecordGUI;
//
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.image.BufferedImage;
//
//import javax.swing.*;
//import javax.swing.filechooser.FileFilter;
//import javax.imageio.ImageIO;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.Date;
//
//@SuppressWarnings("serial")
//public class MainAudioRecorderGUI extends JFrame implements ActionListener {
////	private TestSoundDirect recorder = new TestSoundDirect();
//	private TestSound recorder = new TestSound();
//
//	private JFrame recorderFrame = new JFrame(); //main GUI frame
//	private JPanel titlePanel = new JPanel(); //title panel
//	private JPanel logoPanel = new JPanel(); //Logo panel
//	private JPanel recordingPanel = new JPanel(); //Recording panel
//	private JPanel projectTitlePanel = new JPanel(); //Project title panel
//	private JPanel buttonsPanel = new JPanel(); //Buttons panel
//	private JPanel timeDisplayPanel = new JPanel(); //Time display panel
//	private JPanel playbackTimePanel = new JPanel();//Playback time choosing panel
//	private JPanel fileListPanel = new JPanel(); //File list panel
//	private JPanel fileLocationPanel = new JPanel(); //File Location setting panel
//
//	private static final Color scuBackgroundColor = Color.decode("#93191B");
//	//	private static final Color scuForegroundColor = Color.decode("#D2C599");
//	private BufferedImage logoPicture = ImageIO.read(new File("iconANDLogo/scu-logo-seal.png"));
//	private BufferedImage logoTitlePicture = ImageIO.read(new File("iconANDLogo/scu-logo-csts.png"));
//
//	private JLabel projectTitle = new JLabel("Audio Recorder");
//	private JButton recordStopButton = new JButton();
//	private JButton playPauseButton = new JButton();
//	private JLabel recordTimeLabel = new JLabel();
//	private ImageIcon recordIcon = new ImageIcon("iconANDLogo/recorder.png");
//	private ImageIcon playIcon = new ImageIcon("iconANDLogo/play.png");
//	private ImageIcon stopIcon = new ImageIcon("iconANDLogo/stop.png");
//	private ImageIcon pauseIcon = new ImageIcon("iconANDLogo/pause.png");
//	private JLabel playbackLabel = new JLabel("Playback Time");
//	private JSpinner timeSpinner = new JSpinner( new SpinnerDateModel() );
//	private JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "hh:mm a");
//	private JButton playbackSaveButton = new JButton("Save");
//	private JLabel fileLocationLabel = new JLabel("File Location:");
//	private JTextField fileLocationTextField = new JTextField();
//	private JButton changeLocationButton = new JButton("Change Location");
//
//	private boolean isRecording = false;
//	private boolean isPlaying = false;
//	private RecordTimer timer;
//    private JFrame frame;
//    private JTextField playbackTimerTextField;
//
//	public MainAudioRecorderGUI() throws IOException { 
//		super("Audio Recorder");
//		recorderFrame.setSize(1000, 1000);
//		recorderFrame.setLayout(new BorderLayout());
//		titlePanel.setPreferredSize(new Dimension(1000, 100));
//		recorderFrame.add(titlePanel, BorderLayout.PAGE_START);
//		recordingPanel.setPreferredSize(new Dimension(1000, 500));
//		recordingPanel.setBorder(BorderFactory.createLineBorder(Color.black));
//		recorderFrame.add(recordingPanel, BorderLayout.CENTER);
//		fileListPanel.setPreferredSize(new Dimension(1000, 300));
//		fileListPanel.setBorder(BorderFactory.createLineBorder(Color.black));
//		recorderFrame.add(fileListPanel, BorderLayout.PAGE_END);
//
//		titlePanel.setBackground(scuBackgroundColor);
//		titlePanel.setLayout(new FlowLayout(FlowLayout.LEADING));
//		logoPanel.setPreferredSize(new Dimension(600,100));
//		JLabel logoPicLabel = new JLabel(new ImageIcon(logoPicture));
//		logoPanel.add(logoPicLabel);
//		JLabel logoTitlePicLabel = new JLabel(new ImageIcon(logoTitlePicture));
//		logoPanel.add(logoTitlePicLabel);
//		logoPanel.setOpaque(false);
//		titlePanel.add(logoPanel);
//
//		recordingPanel.setLayout(new BorderLayout());
//		projectTitlePanel.setPreferredSize(new Dimension(1000,50));
//		projectTitle.setFont(new Font("Serif", Font.BOLD, 30));
//		projectTitle.setForeground(scuBackgroundColor);;
//		projectTitlePanel.add(projectTitle);
//		projectTitlePanel.setOpaque(false);
//		recordingPanel.add(projectTitlePanel, BorderLayout.PAGE_START);
//		buttonsPanel.setPreferredSize(new Dimension(500,150));
//		recordStopButton.setText("Record");
//		recordStopButton.setFont(new Font("Sans", Font.BOLD, 20));
//		recordIcon.setImage(recordIcon.getImage().getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH ));
//		recordStopButton.setIcon(recordIcon);
//		playPauseButton.setText("Play");
//		playPauseButton.setFont(new Font("Sans", Font.BOLD, 20));
//		playIcon.setImage(playIcon.getImage().getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH ));
//		playPauseButton.setIcon(playIcon);
//		playPauseButton.setEnabled(false);
//		buttonsPanel.add(recordStopButton);
//		buttonsPanel.add(playPauseButton);
//		recordingPanel.add(buttonsPanel, BorderLayout.WEST);
//		timeDisplayPanel.setPreferredSize(new Dimension(400,150));
//		recordTimeLabel.setText("00:00:00");
//		recordTimeLabel.setFont(new Font("Serif", Font.BOLD, 80));
//		timeDisplayPanel.add(recordTimeLabel);
//		recordingPanel.add(timeDisplayPanel, BorderLayout.EAST);
//		playbackTimePanel.setPreferredSize(new Dimension(1000,100));
//		playbackTimePanel.add(playbackLabel);
//		playbackLabel.setFont(new Font("Sans", Font.BOLD, 20));
//		timeSpinner.setPreferredSize(new Dimension(120,30));
//		playbackTimerTextField = timeEditor.getTextField();
//		playbackTimerTextField.setFont(new Font("Serif", Font.BOLD, 20));
//		timeSpinner.setEditor(timeEditor);
//		timeSpinner.setValue(new Date());
//		timeSpinner.setEnabled(false);
//		playbackTimePanel.add(timeSpinner);
//		playbackSaveButton.setFont(new Font("Sans", Font.BOLD, 20));
//		playbackSaveButton.setEnabled(false);
//		playbackTimePanel.add(playbackSaveButton);
//		recordingPanel.add(playbackTimePanel, BorderLayout.PAGE_END);
//		recordStopButton.addActionListener(this);
//		playPauseButton.addActionListener(this);
//
//		fileListPanel.setLayout(new BorderLayout());
//		fileLocationPanel.setLayout(new FlowLayout());
//		fileLocationPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
//		fileLocationPanel.add(fileLocationLabel, FlowLayout.LEFT);
//		fileLocationTextField.setPreferredSize(new Dimension(500, 20));
//		fileLocationPanel.add(fileLocationTextField);
//		fileLocationPanel.add(changeLocationButton);
//		fileListPanel.add(fileLocationPanel, BorderLayout.PAGE_START);
//
//		recorderFrame.setVisible(true);
//		recorderFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		recorderFrame.setLocationRelativeTo(null);
//	}
//
//	@Override
//	public void actionPerformed(ActionEvent e) {
//		if (e.getSource() == recordStopButton) {
//			if (!isRecording) {
//				startRecording();
//			} else {
//				if (isPlaying == false) {
//					stopRecording();
//				} else {
//					stopPlaying();
//				}
//
//			}
//		} else if (e.getSource() == playPauseButton) {
//			if (!isPlaying) {
//				startPlaying();
//			} else {
//				if(isRecording == false) {
//					pausePlaying();
//				} else {
//					pauseRecording();
//				}
//			}
//		} else if (e.getSource() == playbackSaveButton) {
//			saveFile();
//		}
//	}
//
//	private void startRecording() {
//		Thread recordThread = new Thread(new Runnable() {
//			@Override
//			public void run() {
//				isRecording = true;
//				isPlaying = false;
//				recordStopButton.setText("Stop");
//				recordStopButton.setFont(new Font("Sans", Font.BOLD, 20));
//				stopIcon.setImage(stopIcon.getImage().getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH ));
//				recordStopButton.setIcon(stopIcon);
//				playPauseButton.setText("Pause");
//				playPauseButton.setFont(new Font("Sans", Font.BOLD, 20));
//				pauseIcon.setImage(pauseIcon.getImage().getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH ));
//				playPauseButton.setIcon(pauseIcon);
//				playPauseButton.setEnabled(true);
//				recorder.start();
//			}
//		});
//		recordThread.start();
//		timer = new RecordTimer(recordTimeLabel);
//		timer.start();
//	}
//
//	private void stopRecording() {
//		isRecording = false;
//		isPlaying = false;
//		timer.cancel();
//		recorder.finish();
//		int n = JOptionPane.showConfirmDialog(frame,
//				"Do you want to save this recording?",
//				"An Inane Question",
//				JOptionPane.YES_NO_OPTION);
//		if (n == JOptionPane.YES_OPTION) {
//			recordStopButton.setEnabled(false);
//			playPauseButton.setEnabled(false);
//			timeSpinner.setEnabled(true);
//			playbackSaveButton.setEnabled(true);
//			playbackSaveButton.addActionListener(this);
//        } else if (n == JOptionPane.NO_OPTION) {
//        	recordStopButton.setText("Record");
//    		recordStopButton.setIcon(recordIcon);
//    		playPauseButton.setText("Play");
//    		playPauseButton.setIcon(playIcon);
//    		playPauseButton.setEnabled(false);
//    		timer.reset();
//        } else {
//        	System.out.println("Do nothing");
//        	recordStopButton.setText("Record");
//    		recordStopButton.setIcon(recordIcon);
//    		playPauseButton.setText("Play");
//    		playPauseButton.setIcon(playIcon);
//    		playPauseButton.setEnabled(false);
//    		timer.reset();
//        }
//	}
//
//	private void pauseRecording() {
//		isRecording = true;
//		isPlaying = false;
//		recordStopButton.setText("Record");
//		recordStopButton.setIcon(recordIcon);
//		playPauseButton.setText("Play");
//		playPauseButton.setIcon(playIcon);
//		playPauseButton.setEnabled(false);
//	}
//
//	private void startPlaying() {
//		isRecording = false;
//		isPlaying = true;
//		timer = new RecordTimer(recordTimeLabel);
//		timer.start();
//		recordStopButton.setText("Stop");
//		recordStopButton.setIcon(stopIcon);
//		playPauseButton.setText("Pause");
//		playPauseButton.setIcon(pauseIcon);
//	}
//
//	private void pausePlaying() {
//		isRecording = true;
//		isPlaying = false;
//		recordStopButton.setText("Record");
//		recordStopButton.setIcon(recordIcon);
//		playPauseButton.setText("Play");
//		playPauseButton.setIcon(playIcon);
//	}
//
//	private void stopPlaying() {
//		isRecording = false;
//		isPlaying = false;
//		timer.reset();
//		timer.interrupt();
//		recordStopButton.setText("Record");
//		recordStopButton.setIcon(recordIcon);
//		playPauseButton.setText("Play");
//		playPauseButton.setIcon(playIcon);
//		playPauseButton.setEnabled(false);
//	}
//	
//	private void saveFile() {
//		String saveFilePath = fileLocationTextField.getText();
//		if (playbackTimerTextField.getText() == new Date().toString()) {
//			System.out.println("Got time");
//		}
//		JFileChooser fileChooser = new JFileChooser();
//		FileFilter wavFilter = new FileFilter() {
//			@Override
//			public String getDescription() {
//				return "Sound file (*.WAV)";
//			}
//
//			@Override
//			public boolean accept(File file) {
//				if (file.isDirectory()) {
//					return true;
//				} else {
//					return file.getName().toLowerCase().endsWith(".wav");
//				}
//			}
//		};
//
//		fileChooser.setFileFilter(wavFilter);
//		fileChooser.setAcceptAllFileFilterUsed(false);
//
//		int userChoice = fileChooser.showSaveDialog(this);
//		if (userChoice == JFileChooser.APPROVE_OPTION) {
//			saveFilePath = fileChooser.getSelectedFile().getAbsolutePath();
//			if (!saveFilePath.toLowerCase().endsWith(".wav")) {
//				saveFilePath += ".wav";
//			}
//
//			File wavFile = new File(saveFilePath);
//
//			try {
//				recorder.save(wavFile);
//
//				JOptionPane.showMessageDialog(MainAudioRecorderGUI.this,
//						"Saved recorded sound to:\n" + saveFilePath);
//
//			} catch (IOException ex) {
//				JOptionPane.showMessageDialog(MainAudioRecorderGUI.this, "Error",
//						"Error saving to sound file!",
//						JOptionPane.ERROR_MESSAGE);
//				ex.printStackTrace();
//			}
//		}
//	}
//
//	public static void main(String[] args) throws IOException {
//		try {
//			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//
//		SwingUtilities.invokeLater(new Runnable() {
//			@Override
//			public void run() {
//				try {
//					new MainAudioRecorderGUI().setVisible(true);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		});	
//	}
//}
