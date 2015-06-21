package soundRecordGUI;

import java.io.*;
import java.math.BigInteger;
import javax.sound.sampled.*;
import java.util.Scanner;


public class AudioMixer implements Serializable {
  public static final int HEADER_SIZE = 44;
  private static final int LITTLE_ENDIAN = 0;
  private static final int BIG_ENDIAN = 1;
  private File audioFile;

  // prints out the header of an uncompressed WAVE
  public void printHeader(File s) {		
    FileInputStream fileIn = null;
    BufferedInputStream bufferedIn = null;
    try {
      // create a file reader
      fileIn = new FileInputStream(s);
      bufferedIn = new BufferedInputStream(fileIn);
      int data = 0;
      int bytesRead = 0;

     // read until header is read fully and end-of-file is not reached
      while ( bytesRead < HEADER_SIZE && (data = bufferedIn.read()) != -1 ) {
        String binaryData = Integer.toString(data, 2);
        System.out.println(String.format("Byte at index %d: %08d\n", bytesRead, Integer.valueOf(binaryData)));
        ++bytesRead;
      }
    } catch (FileNotFoundException e) {
      System.err.println("File could not be found" +e.getMessage());
    } catch (IOException e) {
      System.err.println("IOException" +e.getMessage());
    } finally {
      try {
        bufferedIn.close();	
      } catch (IOException e) {
        System.err.println("IOException" +e.getMessage());
      }
    }
  }	

  // returns the integer value of a byte array 
  private int getDecimalValueOfByteArray(byte[] array, int order) {
    if (order == BIG_ENDIAN) {
      BigInteger number = new BigInteger(array);
      return number.intValue();
    } else { 
      // order is LITTLE_ENDIAN
      int length = array.length;

      // create a new array called array1
      byte[] array1 = new byte[length];
			
      // reverse the contents of array and put in array1	
      int up = 0, down = length - 1;
      while (up <= down) {
      array1[up] = array[down];
      array1[down] = array[up];
      up++;
      down--;
    }
    BigInteger number = new BigInteger(array1);
    return number.intValue();
  }
}

  private void printSummary(File s) {
    FileInputStream fileIn = null;
    BufferedInputStream bufferedIn = null;
    byte[] array2 = new byte[2];
    byte[] array4 = new byte[4];
		
    try {
      // create a file reader
      fileIn = new FileInputStream(s);
      bufferedIn = new BufferedInputStream(fileIn);

      // skip the first 20 bytes
      bufferedIn.skip(20);
			
      // read audio format
      bufferedIn.read(array2);
      System.out.println("Audio Format = " +getDecimalValueOfByteArray(array2, LITTLE_ENDIAN));
			
      // read number of channels
      bufferedIn.read(array2);
      System.out.println("Number of Channels = " +getDecimalValueOfByteArray(array2, LITTLE_ENDIAN));
			
      // read sample rate
      bufferedIn.read(array4);
      System.out.println("Sample Rate = " +getDecimalValueOfByteArray(array4, LITTLE_ENDIAN));
			
      // read byte rate
      bufferedIn.read(array4);
      System.out.println("Byte Rate = " +getDecimalValueOfByteArray(array4, LITTLE_ENDIAN));
			
      // read Bytes in each sample for all channel
      bufferedIn.read(array2);
      System.out.println("Bytes per sample = " +getDecimalValueOfByteArray(array2, LITTLE_ENDIAN));
			
      // read bits per sample
      bufferedIn.read(array2);
      System.out.println("Bits per sample for all channels = " +getDecimalValueOfByteArray(array2, LITTLE_ENDIAN));
			
      // skip data chunk id, don't print it out
      bufferedIn.skip(4);
			
      // read audio data size
      bufferedIn.read(array4);
      System.out.println("Total bytes of audio data  = " +getDecimalValueOfByteArray(array4, LITTLE_ENDIAN));			
    } catch (FileNotFoundException e) {
      System.err.println("File could not be found" +e.getMessage());
    } catch (IOException e) {
      System.err.println("IOException" +e.getMessage());
    } finally {
      try {
        bufferedIn.close();	
      } catch (IOException e) {
        System.err.println("IOException" +e.getMessage());
      }
    }
  }

  public void merge(File source1, File source2, File destination) {
    FileInputStream fileIn1 = null, fileIn2 = null;
    BufferedInputStream bufferedIn1 = null, bufferedIn2 = null;
    FileOutputStream fileOut = null;
    BufferedOutputStream bufferedOut = null;
    int size1, size2;		
    int data1 = 0, data2 = 0, data3; 

    try {
      // create two file readers and a file writer
      fileIn1 = new FileInputStream(source1);
      bufferedIn1 = new BufferedInputStream(fileIn1);
      fileIn2 = new FileInputStream(source2);
      bufferedIn2 = new BufferedInputStream(fileIn2);
      fileOut = new FileOutputStream(destination);
      bufferedOut = new BufferedOutputStream(fileOut);
			
      int bytesRead = 0, HEADER_SIZE = 44;
      byte[] buffer = new byte[HEADER_SIZE];
			
      // get audio data size of source files
      size1 = getDataChunkSize(source1);
      size2 = getDataChunkSize(source2);
      int minSize = Math.min(size1, size2);
			
      // copy header of larger source file into destination
      if (size1 >= size2) {
        bufferedIn1.read(buffer);
        bufferedOut.write(buffer);
        bufferedIn2.skip(HEADER_SIZE);
      } else {
        bufferedIn2.read(buffer);
        bufferedOut.write(buffer);
        bufferedIn1.skip(HEADER_SIZE);
      }
			
      // merge the audio samples in two files
      while (bytesRead < minSize) {
        data1 = bufferedIn1.read();
        //System.out.print(data1);
        //System.out.print(",");
        data2 = bufferedIn2.read();
        data3 = data1 + data2 - 128;
        if (data3 > 255)
          data3 = 255;
        else if(data3 < 0)
          data3 = 0;
				
        // write the merged data to destination file
        bufferedOut.write(data3);
        bytesRead++;
      }

    // copy any remaining bytes in source1 to destination file
    while (bytesRead < size1) {
      data1 = bufferedIn1.read(); 
      bufferedOut.write(data1);	
      bytesRead++;
    }	

    //copy any remaining bytes in source2 to destination file
    while (bytesRead < size2) {
      data2 = bufferedIn2.read(); 
      bufferedOut.write(data2);	
      bytesRead++;
    }				
  } catch (FileNotFoundException e) {
    System.err.println("File could not be found" +e.getMessage());
  } catch (IOException e) {
    System.err.println("IOException" +e.getMessage());
  } finally {
    try {
      bufferedIn1.close();
      bufferedIn2.close();
      // flush bufferedOut
      bufferedOut.flush();
      bufferedOut.close();
    } catch (IOException e) {
      System.err.println("IOException" +e.getMessage());
    }
  }
}

  public int getDataChunkSize(File filename) {
    int size = 0;
    FileInputStream fileIn = null;
    BufferedInputStream bufferedIn = null;
    try {
      // create two file readers
      fileIn = new FileInputStream(filename);
      bufferedIn = new BufferedInputStream(fileIn);

      // skip 40 bytes in header of source2
      bufferedIn.skip(HEADER_SIZE - 4);
		
      // get audio data size of source1
      byte[] chunkSizeArray = new byte[4];
      bufferedIn.read(chunkSizeArray);
      size = getDecimalValueOfByteArray(chunkSizeArray, LITTLE_ENDIAN);	
    } catch (FileNotFoundException e) {
      System.err.println("File could not be found" +e.getMessage());
    } catch (IOException e) {
      System.err.println("IOException" +e.getMessage());
    } finally {
      try {
        bufferedIn.close();
      } catch (IOException e) {
        System.err.println("IOException" +e.getMessage());
      }
    }
  
    return size;
  }

  public void reverse(File source, File destination) {
    RandomAccessFile fileIn = null;
    RandomAccessFile fileOut = null;
		
    int BUFFER_SIZE = 10000;
    byte[] header = new byte[HEADER_SIZE];
    byte[] buffer = new byte[BUFFER_SIZE];
		
    try {
      // create a file reader
      fileIn = new RandomAccessFile(source, "r");

      // create a file writer
      fileOut = new RandomAccessFile(destination, "rw");

      // copy the header of source to destination file
      int numBytes = fileIn.read(header); 
      fileOut.write(header, 0, numBytes);
	
      // read & write audio samples in blocks of size BUFFER_SIZE 
      //starting from the end of the source1
      long seekDistance = fileIn.length();
      int bytesToRead = BUFFER_SIZE;
      long totalBytesRead = 0;
      do {	 
        seekDistance -= BUFFER_SIZE;

        if (seekDistance >= 0) 
         bytesToRead = BUFFER_SIZE;
        else if (seekDistance < 0) {
          // read remaining bytes
          seekDistance = 0;					 
          bytesToRead = (int) (fileIn.length() - totalBytesRead);
        }

        fileIn.seek(seekDistance);
				
        // read a block of audio samples into buffer
        int numBytesRead = fileIn.read(buffer, 0, bytesToRead);
        totalBytesRead += numBytesRead;
				
        // reverse contents of buffer
        int up = 0;
        int down = numBytesRead-1;
        byte temp;
        while (up < down) {
          temp = buffer[up];
          buffer[up] = buffer[down];
          buffer[down] = temp;
          up++;
          down--;
        }

        // write buffer to destination
        fileOut.write(buffer, 0, numBytesRead);
      } while (totalBytesRead < fileIn.length());
      fileOut.setLength(fileIn.length());			
    } catch (FileNotFoundException e) {
      System.err.println("File could not be found" +e.getMessage());
    } catch (IOException e) {
      System.err.println("IOException" +e.getMessage());
    } finally {
      try {
        fileIn.close();
        fileOut.close();
      } catch (IOException e) {
        System.err.println("IOException" +e.getMessage());
      }		
    }	
  }

  public void setAudioFile(File f) {
    audioFile = f;
  }
    
  
  public void play() throws Exception {
    AudioInputStream stream =   AudioSystem.getAudioInputStream(audioFile);
    Clip clip = (Clip) AudioSystem.getClip();

     // line listener causes program to exit after play is completed.
    clip.addLineListener(new LineListener() {
       public void update(LineEvent evt) {   		
          if (evt.getType() == LineEvent.Type.STOP) {
            System.exit(0);
          }
       }
    });

    // open the audio stream and start playing the clip
    clip.open(stream);
    clip.start();

    // program waits here while the music is played
    Thread.sleep(1800*1000);
  }


  public static void main(String[] args) {
    AudioMixer mixer = new AudioMixer();
    File musicFile = new File("/Users/adithiathreya/Desktop/testing1Mar3.wav");
    mixer.printSummary(musicFile);
    //mixer.printSummary(musicFile);
    File musicFile1 = new File("/Users/adithiathreya/Desktop/testing1Mar3Audacity.wav");
    mixer.printSummary(musicFile1);
    File musicFile2 = new File("/Users/adithiathreya/Downloads/blues.wav");
    mixer.printSummary(musicFile2);
//    File mergedMusicFile = new File("audio/merged.wav");
//    mixer.merge(musicFile, musicFile1, mergedMusicFile);
    //File reversedMusicFile = new File("audio/revgroovy.wav");
    //mixer.reverse(musicFile1, reversedMusicFile);
 


   /* try {
      AudioMixer player = new AudioMixer();
      System.out.print("Enter name of file to play:");
      Scanner s = new Scanner(System.in);
      String input = s.next();
      player.setAudioFile(new File(input));
      FileOutputStream fileOut = new FileOutputStream("text/player.dat");
      ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
      objectOut.writeObject(player);
      objectOut.close();	
      System.out.println("Written AudioMixer object called player to file player.dat");

      System.out.println("Create new AudioMixer object called player1");
      AudioMixer player1 = new AudioMixer();
      System.out.println("Initialize player1 fields from file player.dat");
      FileInputStream fileIn = new FileInputStream("text/player.dat");
      ObjectInputStream objectIn = new ObjectInputStream(fileIn);
      player1 = (AudioMixer) objectIn.readObject();
      objectIn.close();	
      player1.play(); // play the audio  
    } catch (Exception e) {
      e.printStackTrace();
    } */
  }
}