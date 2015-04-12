
	package MLB;

	//import com.corundumstudio.socketio.SocketIOClient;
	import org.json.JSONObject;

import jssc.SerialPort;

	//import org.json.*;

	import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
	
	public class App 
	{
		final static Printer printer = new Printer();
        final static SerialPort serialPort = new SerialPort("COM4");
        final static SQLDataBase db = new SQLDataBase();
        final static Webkit wk = new Webkit(); //GUN'S CLASS NAAR WEBKIT
        final static JsonGet Jget = new JsonGet();


	    /**
	     * @param args the command line arguments
	     */
	    public static void main(String[] args) 
	    {
	    	String rekeningnummer="MLBI0200000001"; //Deze zijn om mee te testen
	    	String withdrawAmount="10";
	    	
	    	//***********JsonGet methodes***********//
	    	//Jget.getBalance(rekeningnummer);
	    	//Jget.withdraw(rekeningnummer,withdrawAmount);
	    	//Jget.pinFail(rekeningnummer);
	    	//Jget.pinSucces(rekeningnummer);
	    	
	    	
	        //******************TESTINGS************************//
	    	//Jget.test();

	        
	    	//*********Attempt at JSON parsing*********//
	    	
	    	//String str = "{ \"name\": \"Alice\", \"age\": 20 }";
	    	//JSONObject obj = new JSONObject(str);
	    	//String n = obj.getString("name");
	    	//int a = obj.getInt("age");
	    	//System.out.println(n + " " + a);
	        
	        
	        //*************Serial to Java********************//
                
	        try {
                          
	        	serialPort.openPort();//Open serial port
	            serialPort.setParams(2000000, 8, 1, 0);//Set params.

	            //******Serial to Java read*****//                  
	            serialPort.addEventListener(new SerialPortEventListener() {
					
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
				            	String caseArduino = read.substring(0,2);
				            	String restBytes = read.substring(2);
				            	switchCase(caseArduino,restBytes);
				            //	System.out.println(caseArduino);
				            //	System.out.println(restBytes);
				   

							} 
				            catch (SerialPortException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
				        }
					}
				});
	         
	        }
	        catch (SerialPortException ex){
	            System.out.println(ex);
	           // serialPort.closePort();//Close serial port
	        } 
	        
	    }//einde main methode
	
	    public static void switchCase(String caseFromArduino, String restBytes)
	    {
	    	String rekeningnummer = "";
        	String withdrawAmount = "000";
 	        int balance = 0;
 	        int accountExist = 0;
 	        boolean receipt = true;
 	        String pinLength = "";
 	        boolean pinVerify = false;
 	        String readlength = "";
 	        
 	        String result= "";
	    	switch(caseFromArduino)
        	{
            	case "01":
       
					rekeningnummer = restBytes;
	            	result = "rekeningnummer: "+rekeningnummer; //print rekeningnummer van Arduino	
	       	
	            	//serialPort.writeBytes("1".getBytes());
	            	
	            	//HIER MOET JE accountExist NAAR WEBKIT STUREN
	                 wk.sendAccExist(accountExist);
	            	break;
            	
            	case "21": 
            		result = "pin gelukt!";
	            	pinVerify = true;
	            	Jget.pinSucces(rekeningnummer);
	            	
	            	// HIER MOET JE pinVerify NAAR WEBKIT STUREN
	                wk.sendPinStatus(true,"OPEN");
	                break;
            	
            	case "22": 
            		result = "pin gefaalt!"; 
	            	pinVerify = false;
	            	Jget.pinFail(rekeningnummer);
	            	
	            	//HIER MOET JE pinVerify EN accountState NAAR WEBKIT STUREN
	                wk.sendPinStatus(false,"LOCK");
	            	break;
            	
            	case "03":  
            		balance = Jget.getBalance(rekeningnummer);
            		result = Integer.toString(balance);
            	
            		//HIER MOET JE balance NAAR WEBKIT STUREN
                    wk.sendBalance(balance);
                    break;
            	
            	case "04":  
	            	withdrawAmount = restBytes;
	            	Jget.withdraw(rekeningnummer, withdrawAmount);
	            	
	            	////////////////////////////HIER ARRAY STUREN//////////////////////////////////////
	            	biljet(Integer.parseInt(withdrawAmount)); // DIT IS EEN ARRAY VAN BILJETTEN
	
	            	result = "withdraw: " + withdrawAmount;
	            	
	            	//HIER MOET JE withdrawAmount NAAR WEBKIT STUREN
	                wk.sendWithdrawAmount(withdrawAmount);
	            	break;
            	
            	case "05": 
            		result = "receipt: yes";
	            	receipt = true;
	            	//printer hier
	            	
	            	//HIER MOET JE DE BOOLEAN VAN receipt NAAR WEBKIT STUREN
	                wk.sendReceiptStatus(receipt);
	            	break;
            
            	case "06": 
            		result = "cancel";
	            	//HIER MOET EEN CANCEL REQUEST NAAR WEBKIT
	            	wk.sendCancelRequest();
	            	break;
            	
            	case "07": 
	            	pinLength = restBytes;
	            	result = "pin length"+pinLength;
	            	//HIER MOET STRING LENGTE VAN PIN NAAR WEBKIT
	        		wk.sendPinLength(pinLength);
	            	break;
	            	
            	case "02":
	            	result = "clear input";
	            	//HIER MOET CLEAR INPUT REQUEST
	        		wk.sendClearInput();
	            	break;
            	
            	case "10":
	            	result = "Back input";
	            	//HIER MOET BACK REQUEST
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
				String message = "Withdraw Afgerond naar : "+(oldWithdraw - withdraw)+ " euro";
				System.out.println("Withdraw: "+oldWithdraw+"\nWithdraw Afgerond naar : "+(oldWithdraw - withdraw)+ " euro");
			}
			return outputs;
        }
	    
	}

