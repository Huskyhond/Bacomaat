
	package MLB;

	import jssc.SerialPort;
        import jssc.SerialPortException;
	public class App {

	    /**
	     * @param args the command line arguments
	     */
	    public static void main(String[] args) 
	    {
	    	Printer printer = new Printer();
	        SerialPort serialPort = new SerialPort("COM3");
	        SQLDataBase db = new SQLDataBase();
	        Webkit wk = new Webkit(); //GUN'S CLASS NAAR WEBKIT
	        db.connectdb(); //CONNECT met DATABASE
                /*for (int i = 0;i < 500;i++)
                {
	                wk.sendAccExist(1);
	                wk.sendBalance(200);
	                wk.sendPinLength("6");
	                wk.sendReceiptStatus(true);
	                wk.sendWithdrawAmount("25");
	                wk.sendMoneyOptions(new int[] {1,1,1,1,1});
                }*/
	        //************db methodes, please no touch************//
	        db.updatedb("10","MLBI0200000002");       //verander balance in db d.m.v withdraw amount
	        //db.getBalance("MLBI0200000001");	 	    //RETURNT EEN INT(balance)
	        //db.lock("0200000002");			    //RETURNT EEN STRING(OPEN OF LOCK)
	        //db.checkAccountnumber("0200000001");	//RETURNT EEN INT(hoeveelheid rekeningnummers in db, dus 0 of meer) 
	        //db.updateTransaction("200000001","10","1");
	        //db.getTransactionID();
	        
	        
	        //***********OPGESLAGEN VARIABELEN DIE GEBRUIKT WORDEN VOOR DB EN WEBKIT**********//
	        String reknummer = "MLBI0200000002"; //slaat reknummer van arduino hierin op, de string die er nu in zit is maar een placeholder.
	        String withdrawAmount = "000";
	        int maxWithdrawAmount = 999;
	        int balance = 0;
	        int accountExist = 0;
	        boolean pinVerify = false;
	        String accountState = "OPEN";
	        boolean receipt = true;
	        String pinLength = "";
	        String transactieID = "";
	        
	      //  System.out.println(db.getBalance(reknummer));

	       
	        //*************Serial to Java********************//
                
	        try {
                    
	            
	        	serialPort.openPort();//Open serial port
	            serialPort.setParams(9600, 8, 1, 0);//Set params.

	            boolean power = true;
	            
	            /*//TODO
	            case 01: accountExist doorgeven webkit
	            case 21: pin gelukt doorgeven webkit
	            case 22: pin faal doorgeven webkit
						 bij 3x falen zal accountState op "LOCK" gaan, verstuur dit dus ook
	            case 03: getBalance doorgeven webkit
	            case 04: withdrawAmount doorgeven webkit
	            		 ook een error doorgeven als er niet genoeg saldo is om te withdrawen (dit gebeurt in de if van case 04)
	            case 51 & 52: receipt : Ja of Nee, doorgeven webkit
	            case 06: cancel request doorgeven webkit
	            **/
	            
	            //******Serial to Java read*****//
	            while(power)
	            {         
	            	int caseFromArduino =Integer.parseInt(new String(serialPort.readBytes(2)));
	            	
	            	String result = "case 0: no case";
	            	switch(caseFromArduino)
	            	{

		            	case 01: 
		            	reknummer = new String(serialPort.readBytes(14));
		            	accountExist = db.checkAccountnumber(reknummer); //checken of reknummer bestaat in db
		            	
		            	result = "rekeningnummer: "+reknummer; //print rekeningnummer van Arduino
		            	
		            	//HIER MOET JE accountExist NAAR WEBKIT STUREN
	                        wk.sendAccExist(accountExist);
		            	break;
		            	
		            	case 21: result = "pin gelukt!";
		            	pinVerify = true;
		            	
		            	// HIER MOET JE pinVerify NAAR WEBKIT STUREN
	                        wk.sendPinStatus(true,"OPEN");
		            	break;
		            	
		            	case 22: result = "pin gefaalt!"; 
		            	pinVerify = false;
		            	accountState = db.lock(reknummer); //checken of deze reknummer over de faallimiet zit, zo ja zal zijn account op LOCK gaan
		            	
		            	//HIER MOET JE pinVerify EN accountState NAAR WEBKIT STUREN
	                        wk.sendPinStatus(false,"LOCK");
		            	break;
		            	
		            	case 03:  
		            	balance = db.getBalance(reknummer);
		            	result = Integer.toString(balance);
		            	
		            	//HIER MOET JE balance NAAR WEBKIT STUREN
	                        wk.sendBalance(balance);
		            	break;
		            	
		            	case 04:  
		            	withdrawAmount = new String(serialPort.readBytes(3));
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
		            	
		            	case 51: result = "receipt: yes";
		            	receipt = true;
		            	printer.setPrinter(reknummer, withdrawAmount, transactieID);
		            	printer.print();
		            	
		            	//HIER MOET JE DE BOOLEAN VAN receipt NAAR WEBKIT STUREN
	                        wk.sendReceiptStatus(receipt);
		            	break;
		            	
		            	case 52: result = "receipt: no";
		            	receipt = false;
		            	
		            	//HIER MOET JE DE BOOLEAN VAN receipt NAAR WEBKIT STUREN
	                        wk.sendReceiptStatus(receipt);
		            	break;
		            	
		            	case 06: result = "cancel";
		            	
		            	//HIER MOET EEN CANCEL REQUEST NAAR WEBKIT
	                        wk.sendCancelRequest();
		            	break;
		            	
		            	case 07: 
		            	pinLength = new String(serialPort.readBytes(1));
		            	result = "pin length"+pinLength;
		            	wk.sendPinLength(pinLength);
		            	//HIER MOET STRING LENGTE VAN PIN NAAR WEBKIT
		            	break;
		            	
		            	case 02:
		            	result = "clear input";
		            	wk.sendClearInput();
		            	//HIER MOET CLEAR INPUT REQUEST
		            	break;
		            	
		            	case 10:
		            	result = "Back input";
		            	//HIER MOET BACK REQUEST
		            	break;
	            		
	            	}
	            	System.out.println("case "+caseFromArduino);
	            	System.out.println(result+"\n"); //check reply
                        
	            } 
	            //***end reading***//
	            
	            serialPort.closePort();//Close serial port
	        }
	        catch (SerialPortException ex){
	            System.out.println(ex);
	        } 
	        
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

