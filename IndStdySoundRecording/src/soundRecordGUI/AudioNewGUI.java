package soundRecordGUI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;
import java.util.Date;

@SuppressWarnings("serial")
public class AudioNewGUI extends JFrame implements ActionListener {
	private TestSound recorder = new TestSound();
	private RecordTimer timer;

	private JFrame recorderFrame = new JFrame(); //main GUI frame
	private JPanel titlePanel = new JPanel(); //title panel
	private JPanel logoPanel = new JPanel(); //Logo panel
	private JPanel recordingPanel = new JPanel(); //Recording panel
	private JPanel projectTitlePanel = new JPanel(); //Project title panel
	private JPanel buttonsPanel = new JPanel(); //Buttons panel
	private JPanel timeDisplayPanel = new JPanel(); //Time display panel
	private JPanel fileLocationPanel = new JPanel(); //File Location setting panel
	private JPanel filesListingPanel = new JPanel(); //Files listing panel
	private JPanel fileListPanel = new JPanel(); //List of files panel
	private JPanel playbackTimePanel = new JPanel();//Playback time choosing panel

	private static final Color scuBackgroundColor = Color.decode("#93191B");
	//	private static final Color scuForegroundColor = Color.decode("#D2C599");
	private BufferedImage logoPicture = ImageIO.read(new File("iconANDLogo/scu-logo-seal.png"));
	private BufferedImage logoTitlePicture = ImageIO.read(new File("iconANDLogo/scu-logo-csts.png"));

	private JLabel projectTitle = new JLabel("Audio Recorder");
	private JButton recordStopButton = new JButton();
	private JButton playPauseButton = new JButton();
	private JLabel recordTimeLabel = new JLabel();
	private ImageIcon recordIcon = new ImageIcon("iconANDLogo/recorder.png");
	private ImageIcon playIcon = new ImageIcon("iconANDLogo/play.png");
	private ImageIcon stopIcon = new ImageIcon("iconANDLogo/stop.png");
	private ImageIcon pauseIcon = new ImageIcon("iconANDLogo/pause.png");
	private JLabel fileLocationLabel = new JLabel("File Location:");
	private JTextField fileLocationTextField = new JTextField();
	private JButton changeLocationButton = new JButton("Change Location");
	private DefaultListModel<String> model;
	private JList<String> fileList;

	private JLabel playbackLabel = new JLabel("Playback Time");
	private JSpinner timeSpinner = new JSpinner( new SpinnerDateModel() );
	private JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "hh:mm a");
	private JLabel timeErrorLabel = new JLabel();
	private String saveTime = "";

	private boolean isRecording = false;
	private boolean isPlaying = false;
	private JFrame optionPaneFrame;
	private JTextField playbackTimerTextField;
	private String saveFilePath;
	private String playFileName;
	private Thread playbackThread;

	public AudioNewGUI() throws IOException { 
		super("Audio Recorder");
		recorderFrame.setSize(1000, 1000);
		recorderFrame.setLayout(new BorderLayout());
		titlePanel.setPreferredSize(new Dimension(1000, 100));
		recorderFrame.add(titlePanel, BorderLayout.PAGE_START);
		recordingPanel.setPreferredSize(new Dimension(1000, 250));
		recordingPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		recorderFrame.add(recordingPanel, BorderLayout.CENTER);
		filesListingPanel.setPreferredSize(new Dimension(1000, 400));
		filesListingPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		recorderFrame.add(filesListingPanel, BorderLayout.PAGE_END);

		titlePanel.setBackground(scuBackgroundColor);
		titlePanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		logoPanel.setPreferredSize(new Dimension(600,100));
		JLabel logoPicLabel = new JLabel(new ImageIcon(logoPicture));
		logoPanel.add(logoPicLabel);
		JLabel logoTitlePicLabel = new JLabel(new ImageIcon(logoTitlePicture));
		logoPanel.add(logoTitlePicLabel);
		logoPanel.setOpaque(false);
		titlePanel.add(logoPanel);

		recordingPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		projectTitlePanel.setPreferredSize(new Dimension(1000,50));
		projectTitle.setFont(new Font("Serif", Font.BOLD, 30));
		projectTitle.setForeground(scuBackgroundColor);;
		projectTitlePanel.add(projectTitle);
		projectTitlePanel.setOpaque(false);
		recordingPanel.add(projectTitlePanel);
		buttonsPanel.setPreferredSize(new Dimension(500,150));
		recordStopButton.setText("Record");
		recordStopButton.setFont(new Font("Sans", Font.BOLD, 20));
		recordIcon.setImage(recordIcon.getImage().getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH ));
		recordStopButton.setIcon(recordIcon);
		playPauseButton.setText("Play");
		playPauseButton.setFont(new Font("Sans", Font.BOLD, 20));
		playIcon.setImage(playIcon.getImage().getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH ));
		playPauseButton.setIcon(playIcon);
		playPauseButton.setEnabled(false);
		buttonsPanel.add(recordStopButton);
		buttonsPanel.add(playPauseButton);
		recordingPanel.add(buttonsPanel);
		timeDisplayPanel.setPreferredSize(new Dimension(400,150));
		recordTimeLabel.setText("00:00:00");
		recordTimeLabel.setFont(new Font("Serif", Font.BOLD, 80));
		timeDisplayPanel.add(recordTimeLabel);
		recordingPanel.add(timeDisplayPanel);
		recordStopButton.addActionListener(this);
		playPauseButton.addActionListener(this);

		filesListingPanel.setLayout(new BorderLayout());
		fileLocationPanel.setLayout(new FlowLayout());
		fileLocationPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		fileLocationPanel.add(fileLocationLabel, FlowLayout.LEFT);
		fileLocationTextField.setPreferredSize(new Dimension(500, 30));
		fileLocationTextField.setText(this.getClass().getClassLoader().getResource("").getPath());
		fileLocationPanel.add(fileLocationTextField);
		fileLocationPanel.add(changeLocationButton);
		filesListingPanel.add(fileLocationPanel, BorderLayout.PAGE_START);
		fileListPanel.setPreferredSize(new Dimension(900, 350));
		fileListPanel.setLayout(new BorderLayout());
		filesListingPanel.add(fileListPanel, BorderLayout.CENTER);
		this.displayFileList();
		changeLocationButton.addActionListener(this);

		recorderFrame.setVisible(true);
		recorderFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		recorderFrame.setLocationRelativeTo(null);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == recordStopButton) { //if Record Button or Stop Button is pressed
			if (!isRecording) {
				startRecording();
			} else {
				if (isPlaying == false) {
					stopRecording();
				} else {
					stopPlaying();
				}

			}
		} else if (e.getSource() == playPauseButton) { //if Play Button or Pause Button is pressed
			if (!isPlaying) {
				startPlaying();
			} else {
				if(isRecording == false) {
					pausePlaying();
				} else {
					pauseRecording();
				}
			}
		} else if (e.getSource() == changeLocationButton) { //if Change Location Button is pressed 
			fileListPanel.removeAll();
			JFileChooser chooser = new JFileChooser(); 
			chooser.setCurrentDirectory(new java.io.File("."));
			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			chooser.setAcceptAllFileFilterUsed(false);   
			if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { 
				System.out.println("getCurrentDirectory(): " 
						+  chooser.getCurrentDirectory());
				fileLocationTextField.setText(chooser.getCurrentDirectory().getPath());
				displayFileList();
				fileListPanel.revalidate();
				fileListPanel.updateUI();
				recorderFrame.pack();
			}
			else {
				System.out.println("No Selection ");
			}
		}
	}

	private void startRecording() {
		Thread recordThread = new Thread(new Runnable() {
			@Override
			public void run() {
				isRecording = true;
				isPlaying = false;
				recordStopButton.setText("Stop");
				recordStopButton.setFont(new Font("Sans", Font.BOLD, 20));
				stopIcon.setImage(stopIcon.getImage().getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH ));
				recordStopButton.setIcon(stopIcon);
				playPauseButton.setText("Pause");
				playPauseButton.setFont(new Font("Sans", Font.BOLD, 20));
				pauseIcon.setImage(pauseIcon.getImage().getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH ));
				playPauseButton.setIcon(pauseIcon);
				playPauseButton.setEnabled(true);
				recorder.start();
			}
		});
		recordThread.start();
		timer = new RecordTimer(recordTimeLabel);
		timer.start();
	}

	private void stopRecording() {
		isRecording = false;
		isPlaying = false;
		recordStopButton.setText("Record");
		recordStopButton.setIcon(recordIcon);
		playPauseButton.setText("Play");
		playPauseButton.setIcon(playIcon);
		playPauseButton.setEnabled(false);
		timer.cancel();
		recorder.finish();
		int n = JOptionPane.showConfirmDialog(optionPaneFrame,
				"Do you want to save this recording?",
				"Save File",
				JOptionPane.YES_NO_OPTION);
		if (n == JOptionPane.YES_OPTION) {
			System.out.println("Saving the recording");
			playbackTimeSelection();
		} else if (n == JOptionPane.NO_OPTION) {
			System.out.println("Cancel saving");
			timer.reset();
		} else {
			System.out.println("Do nothing");
			timer.reset();
		}
	}

	private void playbackTimeSelection() {
		Object[] options = {"Save",
		"Cancel"};
		int n1 = JOptionPane.showOptionDialog(null,
				getOptionPanel(),
				"Playback time",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.PLAIN_MESSAGE,
				null, options, options[0]);

		if (n1 == JOptionPane.YES_OPTION && saveTime != "") {
			System.out.println("Playback time choose");
			saveFile();
		} else if (n1 == JOptionPane.YES_OPTION && saveTime == "") {
			System.out.println("Playback time selection error");
			playbackTimeSelection();
		} else if (n1 == JOptionPane.NO_OPTION) {
			System.out.println("Cancel saving");
			timer.reset();
		} else {
			System.out.println("Do nothing");
			timer.reset();
		}
	}

	private JPanel getOptionPanel() {
		playbackTimePanel.setPreferredSize(new Dimension(100,80));
		playbackTimePanel.add(playbackLabel);
		playbackLabel.setFont(new Font("Sans", Font.BOLD, 26));
		timeSpinner.setPreferredSize(new Dimension(150,40));
		playbackTimerTextField = timeEditor.getTextField();
		playbackTimerTextField.setFont(new Font("Serif", Font.BOLD, 26));
		timeSpinner.setEditor(timeEditor);
		timeSpinner.setValue(new Date());
		playbackTimePanel.add(timeSpinner);
		playbackTimePanel.add(timeErrorLabel);
		timeErrorLabel.setVisible(false);
		timeSpinner.addChangeListener(new ChangeListener() {      
			@Override
			public void stateChanged(ChangeEvent e) {
				Date currentTime = new Date();
				@SuppressWarnings("deprecation")
				int curTime = currentTime.getHours()*60 + currentTime.getMinutes();				
				@SuppressWarnings("deprecation")
				int spnTime = ((Date)timeSpinner.getValue()).getHours()*60 + ((Date)timeSpinner.getValue()).getMinutes();
				if(spnTime <= curTime) {
					System.out.println("Bad Time");
					saveTime = "";
					timeErrorLabel.setText("Enter a valid time after current time");
					timeErrorLabel.setFont(new Font("Sans", Font.BOLD, 10));
					timeErrorLabel.setForeground(scuBackgroundColor);
					timeErrorLabel.setVisible(true);
					playbackTimePanel.repaint();
				} else {
					System.out.println("Good time");
					saveTime = String.format("%04d", spnTime);
					timeErrorLabel.setVisible(false);
					System.out.println(saveTime);
				}
			}
		});
		return playbackTimePanel;
	}

	private void pauseRecording() {
		isRecording = true;
		isPlaying = false;
		recordStopButton.setText("Record");
		recordStopButton.setIcon(recordIcon);
		playPauseButton.setText("Play");
		playPauseButton.setIcon(playIcon);
		playPauseButton.setEnabled(false);
	}

	private void startPlaying() {
		isRecording = false;
		isPlaying = true;
		timer = new RecordTimer(recordTimeLabel);
		timer.start();
		playbackThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {


					recordStopButton.setText("Stop");
					recordStopButton.setIcon(stopIcon);
					playPauseButton.setText("Pause");
					playPauseButton.setIcon(pauseIcon);

					recorder.play(playFileName);
					timer.reset();

				} catch (UnsupportedAudioFileException ex) {
					ex.printStackTrace();
				} catch (LineUnavailableException ex) {
					ex.printStackTrace();
				} catch (IOException ex) {
					ex.printStackTrace();
				}

			}
		});

		playbackThread.start();
	}

	private void pausePlaying() {
		isRecording = true;
		isPlaying = false;
		recordStopButton.setText("Record");
		recordStopButton.setIcon(recordIcon);
		playPauseButton.setText("Play");
		playPauseButton.setIcon(playIcon);
	}

	private void stopPlaying() {
		isRecording = false;
		isPlaying = false;
		timer.reset();
		timer.interrupt();
		recordStopButton.setText("Record");
		recordStopButton.setIcon(recordIcon);
		playPauseButton.setText("Play");
		playPauseButton.setIcon(playIcon);
		playPauseButton.setEnabled(false);
		recorder.stop();
		playbackThread.interrupt();
	}

	private void saveFile() {
		JFileChooser fileChooser = new JFileChooser();
		FileFilter wavFilter = new FileFilter() {
			@Override
			public String getDescription() {
				return "Sound file (*.WAV)";
			}

			@Override
			public boolean accept(File file) {
				if (file.isDirectory()) {
					return true;
				} else {
					return file.getName().toLowerCase().endsWith(".wav");
				}
			}
		};

		fileChooser.setFileFilter(wavFilter);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setSelectedFile(new File(saveTime + ".wav"));
		disableTextField(fileChooser.getComponents());

		int userChoice = fileChooser.showSaveDialog(this);
		if (userChoice == JFileChooser.APPROVE_OPTION) {
			saveFilePath = fileChooser.getSelectedFile().getAbsolutePath();
			if (!saveFilePath.toLowerCase().endsWith(".wav")) {
				saveFilePath += ".wav";
			}

			File wavFile = new File(saveFilePath);

			try {
				recorder.save(wavFile);

				JOptionPane.showMessageDialog(AudioNewGUI.this,
						"Saved recorded sound to:\n" + saveFilePath);

			} catch (IOException ex) {
				JOptionPane.showMessageDialog(AudioNewGUI.this, "Error",
						"Error saving to sound file!",
						JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
		}
	}

	private void disableTextField(Component[] comp)
	{
		for(int x = 0; x < comp.length; x++)
		{
			if(comp[x] instanceof JPanel) disableTextField(((JPanel)comp[x]).getComponents());
			else if(comp[x] instanceof JTextField)
			{
				((JTextField)comp[x]).setEditable(false);
				return;
			}
		}
	}

	private void displayFileList() {
		fileList = new JList<>(model = new DefaultListModel<String>());
		File path = new File(fileLocationTextField.getText());
		File[] listOfFiles = path.listFiles();
		if (listOfFiles != null) {
			for(File f : listOfFiles) {
				if(f.getName().toLowerCase().endsWith(".wav"))
					model.addElement(f.getName());
			}
		}
		fileListPanel.add(new JScrollPane(fileList), BorderLayout.PAGE_START);
		fileList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					playFileName = (new StringBuilder().append(fileLocationTextField.getText()).append("/").append(fileList.getSelectedValue().toString())).toString();
					playPauseButton.setEnabled(true);
					recordStopButton.setEnabled(true);
					recordStopButton.setText("Record");
					recordStopButton.setIcon(recordIcon);
					playPauseButton.setText("Play");
					playPauseButton.setIcon(playIcon);
				}
			}
		});
	}

	public static void main(String[] args) throws IOException {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					new AudioNewGUI().setVisible(true);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});	
	}
}
