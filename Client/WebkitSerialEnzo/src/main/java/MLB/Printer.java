package MLB;


public class Printer 
{	
	private String reknummer;
	private String withdrawAmount;
	private String transactieID;
	private String date;

	public void print()
	{
		
		
		String[] text = new String[7];
		text[0] = "Welcome to";
		text[1] = "MLB";
		text[2] = "Automaat ID: MLB-1";
		text[3] = "Transactie ID: "+transactieID;
		text[4] = "Rekening nummer: "+reknummer;
		text[5] = "Withdraw Amount: "+withdrawAmount;
		text[6] = date;
		
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
		this.reknummer = reknummer;
		this.withdrawAmount = withdrawAmount;
		this.transactieID = transactieID;
		this.date = date;

	}

}
