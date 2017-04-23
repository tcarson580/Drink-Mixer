package Drink_Mixer;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;

/**
 * 
 * @author Travis
 */
public class Processor {	
	private char cmd;
	private Arduino arduino;
	private String comPort;
	
	protected Processor() throws InterruptedException{
		arduino = new Arduino();
		LinkedList<String> comms = findPort();
		while(comms.size() > 0){
			try{
				arduino.start(comms.removeFirst(), 9600);
			} catch (NoSuchPortException | PortInUseException e){
				
			} 
		}
		setup();
	}
	
	protected Processor(String com) throws InterruptedException{
		arduino = new Arduino();
		try {
			arduino.start(com, 9600);
		} catch (PortInUseException | NoSuchPortException e) {
			
		} 
		setup();
	}
	
	protected Processor(int baudRate) throws InterruptedException{
		arduino = new Arduino();
		LinkedList<String> comms = findPort();
		while(comms.size() > 0){
			try{
				arduino.start(comms.removeFirst(), baudRate);
			} catch (NoSuchPortException | NullPointerException | PortInUseException e){
				
			} 
		}
		setup();
	}
	
	protected Processor(String com, int baudRate) throws InterruptedException{
		arduino = new Arduino();
		try {
			arduino.start(com, baudRate);
		} catch (NoSuchPortException | PortInUseException e) {

		}
		setup();
	}
	
	
	private void setup() throws InterruptedException{
		cmd = 'n';
		TimeUnit.SECONDS.sleep(2);
		
	}
	
	
	/**
	 * Returns a list of active Comm ports for the computer.
	 * @return
	 */
	private LinkedList<String> findPort(){
		LinkedList<String> comms = new LinkedList<String>();
		Enumeration port_list = CommPortIdentifier.getPortIdentifiers();

		
		while (port_list.hasMoreElements()){
			CommPortIdentifier port_id = (CommPortIdentifier)port_list.nextElement();
	
			if (port_id.getPortType() == CommPortIdentifier.PORT_SERIAL){
				comms.add(port_id.getName());
			}
		}
		
		return comms;
	}
	
	protected Arduino getArduino(){
		return arduino;
	}
	
	/**
	 * Sends a 1 digit data character to the arduino for processing.
	 * 
	 * @param data
	 */
	protected void connect(char data){
		this.cmd = data;	
		arduino.sendData(cmd);
	}
	
	protected char getCommand(){
		return cmd;
	}
}
