package MLB;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import lejos.pc.comm.*;

public class App 
{
	final static Printer printer = new Printer();
	static SerialPort serialPort = new SerialPort("COM3");
	final static SQLDataBase db = new SQLDataBase();
	final static Webkit wk = new Webkit();
	final static JsonGet Jget = new JsonGet(printer,wk);
	//Die hieronder gooit een exception.
	//static Dispenser dispenser = new Dispenser();
	
	static String rekeningnummer = null;
	static String withdrawAmount ="";
	static int balance = 0;
	static boolean accountExist = false;
	static boolean receipt = false;
	static String pinLength = "";
	static int failCount = 0;
	static String withdrawDigit = "";
	static int[] withdrawArray = {0,0,0};
	static int withdrawDigitCount=0;
	static String afgerond="";
	
	/**
		* @param args the command line arguments
	*/
	public static void main(String[] args) 
	{
		if(args.length > 0)
		{
			serialPort = new SerialPort(args[0]);
		}
		else 
		{
			serialPort = new SerialPort("COM3");
		}
		//*************Serial to Java********************//
		
		try 
		{
			serialPort.openPort();
			serialPort.setParams(2000000, 8, 1, 2);
			
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
							String read = new String(serialPort.readString(bytesCount));
							System.out.println("READ: "+read);
							try
							{
								String sub = read.substring(0,2);
								String sub2 = read.substring(2);
								
								int a = Integer.parseInt(sub);
								if(a==1)
								{
									System.out.println("Pinpas gescand!");
									wk.scanPas();
								}
								
								if(a==20)
								{
									System.out.println("waiting for reknr+pin...");
									System.out.println("read1: "+sub2);
									switchCase(a,sub2);
									
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
								
							}
							
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
		} 
		
	}
	public static void switchCase2(int caseFromArduino)
	{
		String result= "";
		
		switch(caseFromArduino)
		{
			
			case 3: //Get balance
			balance = Jget.getBalance(rekeningnummer);
			result = Integer.toString(balance);
			
			wk.sendBalance(balance);
			break;
			
			case 4: //Naar withdraw page
			result = "Entering withdraw page";
			resetWithdraw();
			wk.sendWithdrawRequest();
			break;
			
			case 5: //bon printen
			result = "receipt: yes";
			receipt = true;
			wk.sendReceiptStatus(receipt);
			printer.print();
			
			receipt = false;
			break;
			
			case 6: //cancel
			result = "cancel";
			wk.sendCancelRequest();
			break;
			case 2: //clear pin input
			result = "clear input";
			resetWithdraw();
			wk.sendClearPinInput();
			break;
			
			case 10: //back request
			result = "Back to Home screen";
			wk.sendBackRequest();
			break;
			
			case 14:
			for(int r=0;r<withdrawDigitCount;r++)
			{
				withdrawAmount = withdrawAmount + withdrawArray[r];
			}
			System.out.println(rekeningnummer+"\n"+withdrawAmount);
			
			int biljetten[] = biljet(Integer.parseInt(withdrawAmount)); //in biljet word withdraw afgerond
			wk.sendWithdrawAmount(afgerond); //afgerond bedrag word gestuurd
			
			int confirm=0;
			
			try //kijken of tevreden met afgerond bedrag
			{
				System.out.println("wachten op response");
				String read1 = new String(serialPort.readString(1));//1=Nee, 2=Ja
				System.out.println("readConfirmation: "+read1);
				confirm = Integer.parseInt(read1);
				
			}
			catch(Exception e)
			{
				System.out.println("Error in waiting for confirmation..");
			}
			
			if(confirm == 2)//doorgaan met afgerond bedrag
			{
				Jget.withdraw(rekeningnummer, afgerond);
				if(Jget.getBooleanBalance()==true) //genoeg saldo om te pinnen
				{
					try
					{
						serialPort.writeInt(50); 
					}
					catch(Exception e)
					{
						System.out.println("Error in serialWrite");
					}
					//Hier naar bon page
					wk.toReceipt();
					Dispenser.printMoney(biljetten[2], biljetten[1], biljetten[0]); //A , B , C | 50 , 20 , 10 
				}
				else //niet genoeg saldo
				{
					resetWithdraw();
					try
					{
						serialPort.writeInt(49); 
					}
					catch(Exception e)
					{
						System.out.println("Error in serialWrite");	
					}
					//Hier terug naar withdraw page
					wk.invalidSaldo();
				}
				
			}	
			else //niet doorgaan
			{
				resetWithdraw();
				wk.sendClearWithdrawInput();
			}
			result = "withdraw: " + withdrawAmount;		
			break;
		}
		System.out.println("EINDE VAN CASE "+caseFromArduino);
		System.out.println(result+"\n"); //check reply
	}
	
	public static void switchCase(int caseFromArduino, String restBytes)
	{	
		String result= "";	
		switch(caseFromArduino)
		{
			case 20: //pinpas login
			rekeningnummer = restBytes.substring(0, 14);
			int pinsub = restBytes.length();
			String pin = restBytes.substring((pinsub-4),pinsub);
			accountExist = Jget.login(rekeningnummer,pin);
			try
			{
				if(accountExist == true)
				{
					serialPort.writeInt(50);
				}
				else
				{
					serialPort.writeInt(49);
				}
			}
			catch(Exception e)
			{
				System.out.println("Writing to serialPort: Failed");
			}
			System.out.println("accountExist: "+accountExist);
			
			result = "rekeningnummer: "+rekeningnummer+"\npin: "+pin;
			

			wk.sendAccExist(accountExist);
			break;
			
			case 7:
			pinLength = restBytes;
			result = "pin length"+pinLength;

			wk.sendPinLength(pinLength);
			break;
			
			case 9:
			withdrawDigit = restBytes;
			result = "withdrawDigit: "+withdrawDigit;
			wk.sendWithdrawDigit(withdrawDigit);
			break;
			
		}
		
		System.out.println("case "+caseFromArduino);
		System.out.println(result+"\n");
	}
	
	public static int[] biljet(int withdrawAmount)
	{
		int oldWithdraw = withdrawAmount;
		int withdraw = withdrawAmount;
		
		int bills[] = {50,20,10};
		int outputs[] = {0,0,0};
		
		for(int i =0; i<bills.length; i++)
		{
			outputs[i] = withdraw/bills[i];
			withdraw = withdraw-(outputs[i]*bills[i]);
			System.out.println("Aantal "+bills[i]+" : " + outputs[i]);				
		}
		afgerond = ""+(oldWithdraw - withdraw);
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