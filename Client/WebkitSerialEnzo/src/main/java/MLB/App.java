
	package MLB;

	import jssc.SerialPort;
	import jssc.SerialPortException;
	public class App {

	    /**
	     * @param args the command line arguments
	     */
	    public static void main(String[] args) 
	    {
	        SerialPort serialPort = new SerialPort("COM3");
	        SQLDataBase db = new SQLDataBase();
	        Webkit wk = new Webkit(); //GUN'S CLASS NAAR WEBKIT
	        db.connectdb(); //CONNECT met DATABASE
                
                
	        
	        //************db methodes, please no touch************//
	        //db.updatedb("10","0200000002");       //verander balance in db d.m.v withdraw amount
	        //db.getBalance("0200000001");	 	    //RETURNT EEN INT(balance)
	        //db.lock("0200000002");			    //RETURNT EEN STRING(OPEN OF LOCK)
	        //db.checkAccountnumber("0200000001");	//RETURNT EEN INT(hoeveelheid rekeningnummers in db, dus 0 of meer) 
	        
	        
	        //***********OPGESLAGEN VARIABELEN DIE GEBRUIKT WORDEN VOOR DB EN WEBKIT**********//
	        String reknummer = "0200000002"; //slaat reknummer van arduino hierin op, de string die er nu in zit is maar een placeholder.
	        String withdrawAmount = "000";
	        int maxWithdrawAmount = 999;
	        int balance = 0;
	        int accountExist = 0;
	        boolean pinVerify = false;
	        String accountState = "OPEN";
	        boolean receipt = true;
	        String pinLength = "";

	       
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
	            	int caseFromArduino = Integer.parseInt(new String(serialPort.readBytes(2)));
	            	
	            	String result = "case 0: no case";
	            	switch(caseFromArduino)
	            	{
	            	case 01: 
	            	reknummer = new String(serialPort.readBytes(10));
	            	accountExist = db.checkAccountnumber(reknummer); //checken of reknummer bestaat in db
	            	result = "rekeningnummer: "+reknummer; //print rekeningnummer van Arduino
	            	
	            	//HIER MOET JE accountExist NAAR WEBKIT STUREN
                        wk.sendAccExist(accountExist);
	            	break;
	            	
	            	case 21: result = "pin gelukt!";
	            	pinVerify = true;
	            	
	            	//HIER MOET JE pinVerify NAAR WEBKIT STUREN
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
	            	db.updatedb(withdrawAmount,reknummer);
	            	result = "withdraw: " + withdrawAmount;
	            	
	            	//HIER MOET JE withdrawAmount NAAR WEBKIT STUREN
                        wk.sendWithdrawAmount(withdrawAmount);
	            	break;
	            	
	            	case 51: result = "receipt: yes";
	            	receipt = true;
	            	
	            	//HIER MEOT JE DE BOOLEAN VAN receipt NAAR WEBKIT STUREN
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
	            	}
	            	System.out.println("case "+caseFromArduino);
	            	System.out.println(result+"\n"); //check reply
                        try {
                            Thread.sleep(1000);
                        }
                        catch(InterruptedException e) {
                            
                        }
                        
	            } 
	            //***end reading***//
	            
	            serialPort.closePort();//Close serial port
	        }
	        catch (SerialPortException ex){
	            System.out.println(ex);
	        } 
	        
	    } 
	    
	}

