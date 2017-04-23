package Drink_Mixer;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.io.*;

public class Processor_Driver{
	private static Processor p;
	private static int port;
	private static float[][] drinkMatrix;
	private final static int UNIT_POUR_TIME = 1000;
	private final static int totalIngredients = 17;
	private final static int totalDrinks = 11;
	
	public static void main(String[] args) throws InterruptedException, IOException{	
		startup();
		p.connect('p');
		System.out.println("System is ready to use.");
		// Takes user input and processes the user's commands	
		/***********************************************************************
		 ******************* Server Stuff **************************************
		 ***********************************************************************/
		String inputLine, outputLine;
		port = 1024;
		Socket clientSocket = null;		
		while(true){
			try{ 
				ServerSocket serverSocket = new ServerSocket(port);
				clientSocket = serverSocket.accept();
				PrintWriter out =
						new PrintWriter(clientSocket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(
						new InputStreamReader(clientSocket.getInputStream()));
				
				while ((inputLine = in.readLine()) != null) {
					System.out.println(inputLine);
			        processUser(inputLine);
			        break;
			    }
				
			} catch (Exception e){			
			} finally{
				if (clientSocket != null)
					clientSocket.close();
			}
		}
		
        
        /************************************************************************
         ************************************************************************/
	}
	
	private static void processUser(String inputLine) {
		String[] input = inputLine.split(" ");
		Integer drink = Integer.parseInt(input[0]) - 1;
		for (int ingred = 0; ingred < totalIngredients; ingred++){
			float units = drinkMatrix[drink-1][ingred];
			if (units > 0){
				// Change ingred%5+1 once we have enough sensors
				sleepTime(Integer.toString((ingred%6)+1).charAt(0), units);
			}
		}
		
	}
	
	
	/**
	 * Amount of time * the number of times it needs poured.
	 * @param sensor
	 * @param unit
	 */
	private static void sleepTime(char sensor, float unit){
		for (int i = 0; i < unit; i++){
			connect(sensor);
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
			}
			connect(sensor);
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
			}
		}
		/*connect(sensor);
		try {
			Thread.sleep(UNIT_POUR_TIME*(long) unit);
		} catch (InterruptedException e) {
		} finally {
			connect(sensor);
		}*/
	}

	/**
	 * Set up the drink matrix and processor.
	 */
	public static void startup(){
		LinkedList<Float> units = parseDrinkList();
		drinkMatrix = getDrinkMatrix(units);
		
		try {
			p = new Processor();
		} catch (InterruptedException e) {
			System.out.println(e);
		}
	}
	
	
	/**
	 * Send data to the Arduino
	 * @param data
	 */
	public static void connect(char data){
		p.connect(data);
	}
	
	
	/**
	 * 
	 * @return Linked List of each ingredient.
	 */
	private static LinkedList<Float> parseDrinkList(){
		LinkedList<Float> units = new LinkedList<Float>();
        BufferedReader br = null;
        String unit, line = "";
        
        // Regular Expression containing anything but 0-9
        String cvsSplitBy = ",";

        try {
        	// Opens file
            br = new BufferedReader(new FileReader("DrinkList.csv"));
            
            //Reads lines
            while ((line = br.readLine()) != null) {
            	
                // use comma, new line, and space as separator
            	String[] unitGroup = line.split(cvsSplitBy);
            	for (int i = 0; i < unitGroup.length; i++){
            		unit = unitGroup[i];
                	units.add((Float) Float.parseFloat(unit));
            	}
            	
            }

        } catch (FileNotFoundException e) {
        	System.out.println("There was a problem reading the file");
        } catch (IOException e) {
            System.out.println("There was a problem reading a number.");
        } finally {
            if (br != null) {
            	
            	// Closes the file
                try {
                    br.close();
                } catch (IOException e) {
                    System.out.println("There was an error closing the file.");
                }
            }
        }
        
        return units;
	}
	
	/**
	 * 
	 * @param units
	 * @return matrix of units for each ingredient
	 */
	private static float[][] getDrinkMatrix(LinkedList<Float> units){
		float[][] drinkMatrix = new float[totalDrinks][totalIngredients];
		
		for (int name = 0; name < totalDrinks; name++){
			for (int ingred = 0; ingred < totalIngredients; ingred++){
				drinkMatrix[name][ingred] = units.removeFirst();
			}
		}
		
		return drinkMatrix;
	}
}
