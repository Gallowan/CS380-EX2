/**
 * CS 380.01 - Computer Networks
 * Professor: NDavarpanah
 *
 * Exercise 2
 * Ex2Client
 *
 * Justin Galloway
 */

import java.io.*;
import java.nio.*;
import java.util.*;
import java.net.*;

import java.util.zip.CRC32;

// Connects to a socket and receives 100 bytes at .5 bytes every reception.
// Reconstructs the message, generates the CRC code and sends it back.
public class Ex2Client {
    public static void main(String[] args) {
        try {
            // Same host as previous exercise
            Socket s = new Socket("18.221.102.182",38102);
            System.out.println("\nConnected to Server.");
            System.out.println("Recieved bytes:\n");
            long crc = genCRC( getBytes(s) );
            sendCRC(s, crc);

            // Run check and return "Response good." for true
            String validity = (checkCRC(s)) ? "Response good." : "Invalid response.";
            System.out.println(validity);

            // Close the socket to end.
            s.close();
            System.out.println("Disconneced from server.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Receives the sent bytes and prints them properly.
    public static byte[] getBytes(Socket s) {
        try {
            InputStream is = s.getInputStream();
            int count = 0;
            byte[] msg = new byte[100];

            for(int k = 0; k < 100; ++k) {
                if(count == 10) {
                    // Reset count at 10
                    System.out.println();
                    count = 0;
                }

                // Rearrange bytes into proper message
                int firstHalf = is.read();
                int newFirstHalf = 16 * firstHalf;
                int secondHalf = is.read();
                firstHalf += secondHalf;
                msg[k] = (byte) firstHalf;

                // Print the bytes received as hexadecimal
                String part1 = Integer.toHexString(firstHalf).substring(0,1).toUpperCase();
                String part2 = Integer.toHexString(secondHalf).substring(0,1).toUpperCase();

                System.out.print(part1 + part2);
                ++count;
            }
            return msg;
        } catch (Exception e) {}
        return null;
    }

    // Reads server response to test generated CRC code
    // 1 is true, else false
    public static boolean checkCRC(Socket s) {
        try {
            InputStream is = s.getInputStream();
            int check = is.read();
            return (check == 1) ? true : false;
        } catch (Exception e) {}
        return false;
    }

    // Cast CRC is an integer, send as an array of bytes
    // Odd solution, but.
    public static void sendCRC(Socket s, long crc) {
        try {
            OutputStream os = s.getOutputStream();
            // CRC is 4 bytes, so...
            ByteBuffer bb = ByteBuffer.allocate(4);
            bb.putInt((int)crc);

            byte[] asArray = bb.array();
            os.write(asArray);
        } catch (Exception e) {}
    }

    // Generates the CRC32 object
    public static long genCRC(byte[] b) {
        long crc;
        CRC32 crcGen = new CRC32();

        crcGen.reset();
        crcGen.update(b, 0, 100);
        crc = crcGen.getValue();

        //Print it out!
        System.out.println("\nGenerated CRC32: " + Long.toHexString(crc).toUpperCase() + ".\n");
        return crc;
    }
}
