package Drink_Mixer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class User_Driver {
	public static void main(String[] args){
		String hostName = "172.16.71.81";
		int portNumber = 1024;
		Socket kkSocket = null;
		PrintWriter out = null;

		try {
		    kkSocket = new Socket(hostName, portNumber);
		    System.out.println("connected");
		    
		    out = new PrintWriter(kkSocket.getOutputStream(), true);
		    BufferedReader in = new BufferedReader(
		        new InputStreamReader(kkSocket.getInputStream()));
		    
		    out.println("2");
		    System.out.println("Fred was sent");
		} catch (Exception e){
			System.out.println("error with the client");
		} finally {
			if (out != null)
				out.flush();
			if (kkSocket != null)
				try {
					kkSocket.close();
				} catch (IOException e) {
				}
		}
	}
}
