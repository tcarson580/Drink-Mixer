package Drink_Mixer;

import java.io.*;
import java.util.TooManyListenersException;
import java.util.concurrent.TimeUnit;
import gnu.io.*;

public class Arduino implements SerialPortEventListener
{
 
   // Used to in the process of converting the read in characters-
   // first in to a string and then into a number.
   String rawStr="";
   
   // Declare serial port variable
   SerialPort mySerialPort;

   // Declare input steam
   InputStream in;
   OutputStream out;
 
   boolean stop = false;


   /**
    * Open's the communication port with the Arduino
    *
    * @param portName
    * @param baudRate
 * @throws PortInUseException 
    */
   protected void start(String portName,int baudRate) throws NoSuchPortException, PortInUseException
   {	
	   stop = false; 
      
	   // Finds and opens the port
	   CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(portName);
	   mySerialPort = (SerialPort)portId.open("my_java_serial" + portName, 2000);

	   // Configure the port
       try {
    	   mySerialPort.setSerialPortParams(baudRate,
    	   mySerialPort.DATABITS_8,
           mySerialPort.STOPBITS_1,
           mySerialPort.PARITY_NONE);
       } catch (UnsupportedCommOperationException e) {
    	   System.out.println("Probably an unsupported Speed");
       }

       // Establish stream for reading from the port
       try {
    	   in = mySerialPort.getInputStream();
           out = mySerialPort.getOutputStream();
            
       } catch (IOException e) { 
    	   System.out.println("couldn't get streams");
       }

       // we could read from "in" in a separate thread, but the API gives us events
       try {
    	   mySerialPort.addEventListener(this);
    	   mySerialPort.notifyOnDataAvailable(true);
       } catch (TooManyListenersException e) {
    	   System.out.println("couldn't add listener");
       }
     
   }

   
   /**
    * Closes the Serial Port
    */
   protected void closeSerialPort() 
   {
      try {
         in.close();
         stop = true; 
         mySerialPort.close();

      }  catch (Exception e) {
    	 System.out.println("Closing port error: " + e);
      }
   }

   
   /**
    * Sends a character to the Arduino code
    * 
    * @param ch
    */
   protected void sendData(char ch)
   {
      try { 
    	  out.write(ch);
      } catch (Exception e) {
    	  System.out.println("Send data error: " + e);
      }
   }



   public void serialEvent(SerialPortEvent event) 
   { 

      //Reads in data while data is available
      while (event.getEventType()== SerialPortEvent.DATA_AVAILABLE && stop==false) 
      {
         try {
        	 char ch;
             //Read in the available character
         	 while(!(in.available() > 0))
         		continue;
          
         	ch = (char)in.read();

            //If the read character is a letter this means that we have found an identifier.
            if (Character.isLetter(ch)==true && rawStr!="") {
               //Convert the string containing the characters accumulated since the last identifier into a double.
               System.out.println("Output = " + ch);              
               

               //Reset rawStr ready for the next reading
               rawStr = ("");
            } else {
               // Add incoming characters to a string.
               // Only add characters to the string if they are digits. 
               // When the arduino starts up the first characters it sends through are S-t-a-r-t- 
               // and so to avoid adding these characters we only add characters if they are digits.

               if (Character.isDigit(ch)) {
                  rawStr = ( rawStr + Character.toString(ch));
                  //System.out.println("raw String = " + rawStr);
               } else {
                  //Get the decimal point
                  if (ch=='.') { 
                     rawStr = ( rawStr + Character.toString(ch));
                  } else {
                     System.out.print(ch);
                  }
               } 
            }
         } catch (IOException e) {
        	 System.out.println("Serial Event error: " + e);
         }
      }
   }
   
   /*
   public static void main(String[] args) throws InterruptedException{
	   Arduino myArduino = new Arduino();
   	
	   myArduino.start("COM8", 9600);
	   //Sleep to allow time for setup
	   TimeUnit.SECONDS.sleep(2);
	   myArduino.sendData('p');
	   myArduino.sendData('1');
	   myArduino.sendData('2');
	   myArduino.sendData('3');
	   myArduino.sendData('4');
	   myArduino.sendData('5');
	   TimeUnit.SECONDS.sleep(1);
	   myArduino.sendData('5');
	   TimeUnit.SECONDS.sleep(1);
	   myArduino.sendData('4');
	   TimeUnit.SECONDS.sleep(1);
	   myArduino.sendData('3');
	   TimeUnit.SECONDS.sleep(1);
	   myArduino.sendData('2');
	   TimeUnit.SECONDS.sleep(1);
	   myArduino.sendData('1');
	   TimeUnit.SECONDS.sleep(1);
	   
   }*/

}