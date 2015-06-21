package soundRecordGUI;

import java.io.*;

import javax.sound.sampled.*;

public class TestSound implements LineListener {

	//buffer array size to store the audio file before saving
	private static final int BUFFER_SIZE = 4096;
	private ByteArrayOutputStream recordBytes;
	
	private boolean isRunning;

	//format for recording audio
	AudioFormat format;

	// format of audio file
	AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;

	// the line from which audio data is captured
	TargetDataLine line;
	
	boolean playCompleted;

	//this flag indicates whether the playback is stopped or not.
	boolean isStopped;

	//Defines an audio format
	AudioFormat getAudioFormat() {
		float sampleRate = 12000;
		int sampleSizeInBits = 8;
		int channels = 1;
		boolean signed = false;
		boolean bigEndian = false;
		return new AudioFormat(sampleRate, sampleSizeInBits,
				channels, signed, bigEndian);
	}

	/**
	 * Captures the sound and record into a WAV file
	 */
	public void start(){
		try {
			format = getAudioFormat();
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

			// checks if system supports the data line
			if (!AudioSystem.isLineSupported(info)) {
				System.out.println("Line not supported");
				System.exit(0);
			}
			line = (TargetDataLine) AudioSystem.getLine(info);
			line.open(format);
			line.start();   // start capturing

			System.out.println("Start capturing...");

			System.out.println("Start recording...");

			byte[] buffer = new byte[BUFFER_SIZE];
			int bytesRead = 0;

			recordBytes = new ByteArrayOutputStream();
			isRunning = true;

			while (isRunning) {
				bytesRead = line.read(buffer, 0, buffer.length);
				recordBytes.write(buffer, 0, bytesRead);
			}

		} catch (LineUnavailableException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Closes the target data line to finish capturing and recording
	 */
	public void finish() {
		isRunning = false;
		line.stop();
		line.close();
		System.out.println("Finished");
	}
	
	/**
	 * Save recorded sound data into a .wav file format.
	 */
	public void save(File wavFile) throws IOException {
		byte[] audioData = recordBytes.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
		AudioInputStream audioInputStream = new AudioInputStream(bais, format, audioData.length/ format.getFrameSize());		
		AudioSystem.write(audioInputStream, fileType, wavFile);
		audioInputStream.close();
		recordBytes.close();
	}
	
	/**
	 * Play a given audio file.
	 */ 
	public void play(String audioFilePath) throws UnsupportedAudioFileException,
	IOException, LineUnavailableException {
		File audioFile = new File(audioFilePath);

		AudioInputStream audioStream = AudioSystem
				.getAudioInputStream(audioFile);

		AudioFormat format = audioStream.getFormat();

		DataLine.Info info = new DataLine.Info(Clip.class, format);

		Clip audioClip = (Clip) AudioSystem.getLine(info);

		audioClip.addLineListener(this);

		audioClip.open(audioStream);

		audioClip.start();

		playCompleted = false;

		while (!playCompleted) {
			// wait for the playback completes
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
				if (isStopped) {
					audioClip.stop();
					break;
				}
			}
		}

		audioClip.close();

	}

	/**
	 * Stop playing back.
	 */
	public void stop() {
		isStopped = true;
	}

	/**
	 * Listens to the audio line events to know when the playback completes.
	 */
	@Override
	public void update(LineEvent event) {
		LineEvent.Type type = event.getType();
		if (type == LineEvent.Type.STOP) {
			playCompleted = true;
		}
	}

}