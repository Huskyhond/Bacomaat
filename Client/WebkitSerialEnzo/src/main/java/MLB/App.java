
	package MLB;

	import jssc.SerialPort;

import org.json.*;

import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
	public class App 
	{
		final static Printer printer = new Printer();
        final static SerialPort serialPort = new SerialPort("COM4");
        final static SQLDataBase db = new SQLDataBase();
        final static Webkit wk = new Webkit(); //GUN'S CLASS NAAR WEBKIT
        final static Main main = new Main();


	    /**
	     * @param args the command line arguments
	     */
	    public static void main(String[] args) 
	    {
	        //db.connectdb(); //CONNECT met DATABASE
    
	        //************db methodes, please no touch************//
	        //db.updatedb("10","MLBI0200000002");         //Verander balance in db d.m.v withdraw amount
	        //db.getBalance("MLBI0200000001");	 	      //RETURNT EEN INT(balance)
	        //db.lock("0200000002");			          //RETURNT EEN STRING(OPEN OF LOCK)
	        //db.checkAccountnumber("0200000001");	      //RETURNT EEN INT(hoeveelheid rekeningnummers in db, dus 0 of meer) 
	        //db.updateTransaction("200000001","10","1"); //update transaction log met recente withdraw
	        //db.getTransactionID();                      //RETUNT EEN INT, de laatste transactionID om uit te printen
	     
	        
	        
	        //***********OPGESLAGEN VARIABELEN DIE GEBRUIKT WORDEN VOOR DB EN WEBKIT**********//
	       
	        //******************TESTINGS************************//
	        //System.out.println(db.getBalance(reknummer));
	        //db.updatedb("25",reknummer);
	        //System.out.println(db.checkAccountnumber(reknummer));
	        //db.updateTransaction(reknummer,"10","1");
	       //  System.out.println(db.getTransactionID());

	    	
	    	
	    	/** -- START SOCKET IO SERVER */
	        Configuration config = new Configuration();
	        config.setHostname("localhost");
	        config.setPort(80);
	        SocketIOServer server = new SocketIOServer(config);

	        server.addConnectListener(new ConnectionListener() {
	           @Override
	           public void onConnect(SocketIOClient clientc) {
	               client = clientc; // Set static connection ( only 1 allowed )
	               System.out.println("Connected");
	               client.sendEvent("update", "{"
	                                          + "\"page\": \"code\""
	                                        + "}");
	           }
	          
	        });
	        
	        server.start();
	        /** -- EINDE SOCKET IO SERVER */
	        
	      //*********Attempt at JSON parsing*********//
	    	main.httpsGet("https://145.24.222.177/balance/200000001");
	    	
	    	String str = "{ \"name\": \"Alice\", \"age\": 20 }";
	    	JSONObject obj = new JSONObject(str);
	    	String n = obj.getString("name");
	    	int a = obj.getInt("age");
	    	System.out.println(n + " " + a);
	        
	        
	        //*************Serial to Java********************//
                
	        try {
                    
	            
	        	serialPort.openPort();//Open serial port
	            serialPort.setParams(2000000, 8, 1, 0);//Set params.

	            boolean power = true;

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
	    	String reknummer = "";
        	String withdrawAmount = "000";
 	        int maxWithdrawAmount = 999;
 	        int balance = 0;
 	        int accountExist = 0;
 	        String accountState = "OPEN";
 	        boolean receipt = true;
 	        String pinLength = "";
 	        String transactieID = "";
 	        boolean pinVerify = false;
 	        String readlength = "";
 	        
 	        String result= "";
	    	switch(caseFromArduino)
        	{
            	case "01":
       
				reknummer = restBytes;
            	//accountExist = db.checkAccountnumber(reknummer); //checken of reknummer bestaat in db
            	
            	result = "rekeningnummer: "+reknummer; //print rekeningnummer van Arduino	
            	
            	
            	//serialPort.writeBytes("1".getBytes());
            	
            	//HIER MOET JE accountExist NAAR WEBKIT STUREN
                 wk.sendAccExist(accountExist);
            	break;
            	
            	case "21": result = "pin gelukt!";
            	pinVerify = true;
            	
            	// HIER MOET JE pinVerify NAAR WEBKIT STUREN
                   wk.sendPinStatus(true,"OPEN");
            	break;
            	
            	case "22": result = "pin gefaalt!"; 
            	pinVerify = false;
            	accountState = db.lock(reknummer); //checken of deze reknummer over de faallimiet zit, zo ja zal zijn account op LOCK gaan
            	
            	//HIER MOET JE pinVerify EN accountState NAAR WEBKIT STUREN
                    wk.sendPinStatus(false,"LOCK");
            	break;
            	
            	case "03":  
            	balance = db.getBalance(reknummer);
            	result = Integer.toString(balance);
            	
            	//HIER MOET JE balance NAAR WEBKIT STUREN
                    wk.sendBalance(balance);
            	break;
            	
            	case "04":  
            	withdrawAmount = restBytes;
            	
            	if(Integer.parseInt(withdrawAmount) > db.getBalance(reknummer)) //checken of er genoeg saldo is om te pinnen
            	{
            		result = "Niet genoeg Saldo!";
            		
            		//HIER MOET EEN ERROR REQUEST(bijvoorbeeld: "Niet genoeg Saldo" naar webkit sturen)
                           wk.sendWithdrawError();
            		break;
            	}
            	////////////////////////////HIER ARRAY STUREN//////////////////////////////////////
            	biljet(Integer.parseInt(withdrawAmount)); // DIT IS EEN ARRAY VAN BILJETTEN
            	
            	db.updatedb(withdrawAmount,reknummer);
            	db.updateTransaction(reknummer, withdrawAmount, "1");
            	transactieID = Integer.toString(db.getTransactionID());
            	result = "withdraw: " + withdrawAmount;
            	
            	//HIER MOET JE withdrawAmount NAAR WEBKIT STUREN
                    wk.sendWithdrawAmount(withdrawAmount);
            	break;
            	
            	case "05": result = "receipt: yes";
            	receipt = true;
            	printer.setPrinter(reknummer, withdrawAmount, transactieID);
            	printer.print();
            	
            	//HIER MOET JE DE BOOLEAN VAN receipt NAAR WEBKIT STUREN
                    wk.sendReceiptStatus(receipt);
            	break;
            
            	case "06": result = "cancel";
            	
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

