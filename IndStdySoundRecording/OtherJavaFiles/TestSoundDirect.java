package soundRecordGUI;

import java.io.*;
import java.util.Scanner;

import javax.sound.sampled.*;

public class TestSoundDirect {

	//path of wav file
	private File wavFile = new File("/Users/adithiathreya/Desktop/testing1Mar3.wav");
	

	// format of audio file
	AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;

	// the line from which audio data is captured
	TargetDataLine line;

	/**
	 * Defines an audio format
	 */
	AudioFormat getAudioFormat() {
		float sampleRate = 44100;
		int sampleSizeInBits = 8;
		int channels = 1;
		boolean signed = false;
		boolean bigEndian = false;
		return new AudioFormat(sampleRate, sampleSizeInBits,
				channels, signed, bigEndian);
	}

	/**
	 * Captures the sound and record into a WAV file
	 * @throws IOException 
	 */
	public void start() throws IOException{
		try {
			AudioFormat format = getAudioFormat();
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
			AudioInputStream ais = new AudioInputStream(line);

			System.out.println("Start recording...");
			AudioSystem.write(ais, fileType, wavFile);

		} catch (LineUnavailableException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Closes the target data line to finish capturing and recording
	 */
	public void finish() {
		line.stop();
		line.close();
		System.out.println("Finished");
	}
	
	public void save(File wavFile) throws IOException {
		
	}
	
	public static void main(String[] args) throws IOException {
        TestSoundDirect recorder = new TestSoundDirect();

        System.out.println("Enter 1 to start recording");
        @SuppressWarnings("resource")
		Scanner user_input = new Scanner( System.in );
        int ui = user_input.nextInt();
        while (ui != 2) {
        	recorder.start();
        }
        recorder.finish();
    }

}