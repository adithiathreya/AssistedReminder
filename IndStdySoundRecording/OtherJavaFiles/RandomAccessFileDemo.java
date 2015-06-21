package soundRecordGUI;

import java.io.*;
import java.lang.reflect.Array;
import java.math.BigInteger;

public class RandomAccessFileDemo {
	 public static final int HEADER_SIZE = 44;
	  private static final int LITTLE_ENDIAN = 0;
	  private static final int BIG_ENDIAN = 1;
  public static void main(String[] args) {
    try {
    	@SuppressWarnings("resource")
		RandomAccessFile byteFile1 = new RandomAccessFile(new File("/Users/adithiathreya/Desktop/testing1Mar3.wav"), "rw");
    	for (int i=0; i<1444; ++i) {
			byteFile1.seek(i);
			System.out.printf("\nindex " +i+ ": " + String.format("%8s", Integer.toBinaryString(((Integer)byteFile1.read())).replace(' ', '0')));
				
		}
//    	@SuppressWarnings("resource")
//		RandomAccessFile byteFile2 = new RandomAccessFile(new File("/Users/adithiathreya/Desktop/testaudiodirect.wav"), "rw");
//		for (int i=144; i<1444; ++i) {
//			byteFile2.seek(i);
//			System.out.printf("\nindex " +i+ ": " + String.format("%8s", Integer.toBinaryString(((Integer)byteFile2.read())).replace(' ', '0')));
//			
//		}
//		@SuppressWarnings("resource")
//		RandomAccessFile byteFile3 = new RandomAccessFile(new File("/Applications/Audacity/testaudiodirect.wav"), "rw");
//		for (int i=44; i<144; ++i) {
//			byteFile3.seek(i);
//			System.out.printf("\nindex " +i+ ": " + String.format("%8s", Integer.toBinaryString(((Integer)byteFile3.read())).replace(' ', '0')));
//			
//		}
//		@SuppressWarnings("resource")
//		RandomAccessFile byteFile4 = new RandomAccessFile(new File("/Applications/Audacity/tryaudio.wav"), "rw");
//		for (int i=144; i<1444; ++i) {
//			byteFile4.seek(i);
//			System.out.printf("\nindex " +i+ ": " + String.format("%8s", Integer.toBinaryString(((Integer)byteFile4.read())).replace(' ', '0')));
//			
//		}
		@SuppressWarnings("resource")
		RandomAccessFile byteFile5 = new RandomAccessFile(new File("/Users/adithiathreya/Downloads/blues.wav"), "rw");
		byte[] a1 = new byte[4];
		for (int i=0; i<44; ++i) {
			byteFile5.seek(i);
			System.out.printf("\nindex " +i+ ": " + (Integer)byteFile5.read());
			if (i>=4 && i<8) {
				a1[i-4] = (byte) byteFile5.read();
			}
			
		}
		getDecimalValueOfByteArray(a1, BIG_ENDIAN);
    }
    catch (FileNotFoundException e) {
      System.err.println("File could not be found" +e.getMessage());
    }
    catch (IOException e) {
      System.err.println("IOException" +e.getMessage());
    } 
  }
  private static int getDecimalValueOfByteArray(byte[] array, int order) {
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
}