
package MLB;

//import com.corundumstudio.socketio.SocketIOClient;

import jssc.SerialPort;

//import org.json.*;

import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class App 
{
	final static Printer printer = new Printer();
	final static SerialPort serialPort = new SerialPort("COM3");
	final static SQLDataBase db = new SQLDataBase();
	final static Webkit wk = new Webkit(); //GUN'S CLASS NAAR WEBKIT
	final static JsonGet Jget = new JsonGet(printer);
	
	static String rekeningnummer = "MLBI0200000001";
	static String withdrawAmount ="";
	static int balance = 0;
	static boolean accountExist = false;
	static boolean receipt = false;
	static String pinLength = "";
	static int failCount;
	static String withdrawDigit = "";
	static int[] withdrawArray = {0,0,0};
	static int withdrawDigitCount=0;
	
	/**
		* @param args the command line arguments
	*/
	public static void main(String[] args) 
	{
		//String rekeningnummer="MLBI0200000001"; //Deze zijn om mee te testen
		//String withdrawAmount="10";
		
		//***********JsonGet methodes***********//
		//Jget.checkAccount(rekeningnummer);
		//Jget.getBalance(rekeningnummer);
		//Jget.withdraw(rekeningnummer,withdrawAmount);
		//Jget.pinFail(rekeningnummer);
		//Jget.pinSucces(rekeningnummer);
		//Jget.test();
		//printer.print();
		
		
		//*********Attempt at JSON parsing*********//
		
		//String str = "{ \"name\": \"Alice\", \"age\": 20 }";
		//JSONObject obj = new JSONObject(str);
		//String n = obj.getString("name");
		//int a = obj.getInt("age");
		//System.out.println(n + " " + a);
		
		
		//*************Serial to Java********************//
		
		try 
		{
			for(int i=0;i<2;i++)
			{
				serialPort.openPort();//Open serial port
				serialPort.setParams(2000000, 8, 1, 2);//Set params.
				serialPort.closePort();
			}
			serialPort.openPort();//Open serial port
			serialPort.setParams(2000000, 8, 1, 2);//Set params.
			
			//******Serial to Java read*****//
			System.out.println("Serial Ready");
			serialPort.addEventListener(new SerialPortEventListener() 
			{
				@Override
				public void serialEvent(SerialPortEvent event) 
				{
					if (event.isRXCHAR() && event.getEventValue() > 0) //If data is available
					{
						int bytesCount = event.getEventValue();
						try 
						{
							//String read1 = new String(serialPort.readString(1));
							String read = new String(serialPort.readString(bytesCount));
							System.out.println("READ: "+read);
							try
							{
								String sub = read.substring(0,2);
								String sub2 = read.substring(2);
								
								int a = Integer.parseInt(sub);
								if(a==1)
								{
									System.out.println("waiting for reknr...");
									String read1 = new String(serialPort.readString(14));
									System.out.println("read1: "+read1);
									switchCase(a,read1);
									
								}
								else if(a==7)
								{
									switchCase(a,sub2);
								}
								else if(a==9)
								{
									withdrawArray[withdrawDigitCount] = Integer.parseInt(sub2);
									withdrawDigitCount++;
									switchCase(a,sub2);
								}
								else
								{
									switchCase2(a);
								}
								
							}
							catch(Exception e)
							{
								//System.out.println("Error in eventlistener"); 
							}
							
							//	System.out.println(caseArduino);
							//	System.out.println(restBytes);
							
							
						} 
						catch (SerialPortException e) 
						{
							e.printStackTrace();
						}
					}
				}
			});
			
		}
		catch (SerialPortException ex)
		{
			System.out.println(ex);
			// serialPort.closePort();//Close serial port
		} 
		
	}//einde main methode
	public static void switchCase2(int caseFromArduino)
	{
		String result= "";
		
		switch(caseFromArduino)
		{
			case 21: //pin verify Succes
			result = "pin gelukt!";
			failCount = Jget.pinFail(rekeningnummer);
			if(failCount>3)//als de arduino niet verder mag
			{
				try
				{
					System.out.println("locked");
					serialPort.writeInt(49); // dit schrijft een 1
					
				}
				catch(Exception e)
				{
					System.out.println("Writing to serialPort: Failed");
				}
				//HIER NAAR NIEK STUREN DAT ACCOUNT IS BLOCKED	
			}
			else//als hij wel verder mag
			{
				try
				{
					serialPort.writeInt(50); // dit schrijft een 2
					Jget.pinSucces(rekeningnummer);
					// HIER MOET JE pinVerify NAAR WEBKIT STUREN
					wk.sendPinStatus(true);
				}
				catch(Exception e)
				{
					System.out.println("Writing to serialPort: Failed");
				}
			}
			
			break;
			
			case 20: //pin verify Fail
			result = "pin gefaalt!"; 
			failCount = Jget.pinFail(rekeningnummer);
			if(failCount>2)//als de arduino niet verder mag
			{
				try
				{
					serialPort.writeInt(49); // dit schrijft een 1
					System.out.println("locked");
				}
				catch(Exception e)
				{
					System.out.println("Writing to serialPort: Failed");
				}
				//HIER NAAR NIEK STUREN DAT ACCOUNT IS BLOCKED
				wk.sendAccStatus(false);
			}
			else//als hij wel verder mag.
			{
				try
				{
					serialPort.writeInt(50); // dit schrijft een 2
					
				}
				catch(Exception e)
				{
					System.out.println("Writing to serialPort: Failed");
				}
			}
			//HIER MOET JE failcount NAAR NIEK
			wk.sendFailCount(failCount);
			break;
			
			case 3: //Get balance
			balance = Jget.getBalance(rekeningnummer);
			result = Integer.toString(balance);
			
			//HIER MOET JE balance NAAR WEBKIT STUREN
			wk.sendBalance(balance);
			break;
			
			case 4: //Withdraw some amount
			result = "withdraw";
			resetWithdraw();
			//HIER WITHDRAW REQUEST, WE GAAN WITHDRAW SCREEN IN
			wk.sendWithdrawRequest();
			break;
			
			case 5: //bon printen
			result = "receipt: yes";
			receipt = true;
			wk.sendReceiptStatus(receipt);
			printer.print();
			
			//HIER MOET JE DE BOOLEAN VAN receipt NAAR WEBKIT STUREN
			receipt = false;
			break;
			
			case 6: //cancel
			result = "cancel";
			//HIER MOET EEN CANCEL REQUEST NAAR WEBKIT
			wk.sendCancelRequest();
			break;
			case 2: //clear pin input
			result = "clear input";
			resetWithdraw();
			//HIER MOET CLEAR INPUT REQUEST
			wk.sendClearPinInput();
			break;
			
			case 10: //back request
			result = "Back to Home screen";
			//HIER MOET BACK REQUEST
			wk.sendBackRequest();
			break;
			
			case 14:
			for(int r=0;r<withdrawDigitCount;r++)
			{
				withdrawAmount = withdrawAmount + withdrawArray[r];
			}
			System.out.println(rekeningnummer+"\n"+withdrawAmount);
			Jget.withdraw(rekeningnummer, withdrawAmount);
			
			////////////////////////////HIER ARRAY STUREN//////////////////////////////////////
			wk.sendMoneyOptions(biljet(Integer.parseInt(withdrawAmount))); // DIT IS EEN ARRAY VAN BILJETTEN
			result = "withdraw: " + withdrawAmount;
			
			//HIER MOET JE withdrawAmount NAAR WEBKIT STUREN
			wk.sendWithdrawAmount(withdrawAmount);
			break;
		}
		System.out.println("case "+caseFromArduino);
		System.out.println(result+"\n"); //check reply
	}
	
	public static void switchCase(int caseFromArduino, String restBytes)
	{	
		String result= "";	
		switch(caseFromArduino)
		{
			case 1: //pinpas word hier gescant
			rekeningnummer = restBytes;
			accountExist = Jget.checkAccount(rekeningnummer);
			try
			{
				if(accountExist == true)
				{
					serialPort.writeInt(50); // dit schrijft een 2
					//&& Jget.pinFail(rekeningnummer)<3
				}
				else
				{
					serialPort.writeInt(49); // dit schrijft een 1
				}
			}
			catch(Exception e)
			{
				System.out.println("Writing to serialPort: Failed");
			}
			System.out.println(accountExist);
			
			result = "rekeningnummer: "+rekeningnummer; //print rekeningnummer van Arduino	
			
			//HIER MOET JE accountExist NAAR WEBKIT STUREN
			wk.sendAccExist(accountExist);
			break;
			
			case 7: //pinLength naar niek
			pinLength = restBytes;
			result = "pin length"+pinLength;
			//HIER MOET STRING LENGTE VAN PIN NAAR WEBKIT
			wk.sendPinLength(pinLength);
			break;
			
			case 9: //withdraw digit naar niek
			withdrawDigit = restBytes;
			result = "withdrawDigit: "+withdrawDigit;
			//HIER MOET withdrawDigit naar niek
			wk.sendWithdrawDigit(withdrawDigit);
			break;
			
		}//Einde switch
		
		System.out.println("case "+caseFromArduino);
		System.out.println(result+"\n"); //check reply
	}
	
	public static int[] biljet(int withdrawAmount)
	{
		int oldWithdraw = withdrawAmount;
		int withdraw = withdrawAmount;
		
		int bills[] = {100,50,20,10,5};
		int outputs[] = {0,0,0,0,0};
		
		for(int i =0; i<bills.length; i++)
		{
			outputs[i] = withdraw/bills[i];
			withdraw = withdraw-(outputs[i]*bills[i]);
			System.out.println("Aantal "+bills[i]+" : " + outputs[i]);				
		}
		
		if(withdraw>0)
		{
			/////////////////HIER message STUREN////////////////////////
			//String message = "Withdraw Afgerond naar : "+(oldWithdraw - withdraw)+ " euro";
			System.out.println("Withdraw: "+oldWithdraw+"\nWithdraw Afgerond naar : "+(oldWithdraw - withdraw)+ " euro");
		}
		return outputs;
	}
	
	public static void resetWithdraw()
	{
		for(int r=0;r<3;r++)
		{
			withdrawArray[r] = 0;
		}
		withdrawAmount = "";
		withdrawDigitCount=0;
	}
	
}

