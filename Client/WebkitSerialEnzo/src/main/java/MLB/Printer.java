package MLB;


public class Printer 
{	
	private String reknummer;
	private String withdrawAmount;
	private String transactieID;
	private String date;

	public void print()
	{
		
		
		String[] text = new String[8];
		text[0] = "Welcome to";
		text[1] = "MLB";
		text[2] = "Automaat ID: MLB-1";
		text[3] = "Transactie ID: "+transactieID;
		text[4] = "Rekening nummer: ";
		text[5] = "xxxxxxxxxx"+reknummer;
		text[6] = "Withdraw Amount: "+withdrawAmount;
		text[7] = date;
		
		/*text[7] = "LMAO";
		text[8] = "LMAO";
		text[9] = "LMAO";
		text[10] = "LMAO";
		text[11] = "LMAO";*/

		LabelWriter labelwriter = new LabelWriter(); 
		labelwriter.printLabel(text);	
	}
	
	public void setPrinter(String reknummer, String withdrawAmount, String transactieID, String date)
	{
		this.reknummer = reknummer.substring(10);
		this.withdrawAmount = withdrawAmount;
		this.transactieID = transactieID;
		this.date = date;
		System.out.println("Setting up printer");

	}

}
