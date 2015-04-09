package MLB;
import java.util.Date;
import java.text.SimpleDateFormat;

public class Printer 
{	
	private String reknummer;
	private String withdrawAmount;
	private String transactieID;

	public void print()
	{
		
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy h:mm:ss a");
		String formattedDate = sdf.format(date);
		
		String[] text = new String[7];
		text[0] = "Welcome to";
		text[1] = "MLB";
		text[2] = "Automaat ID: MLB-1";
		text[3] = "Transactie ID: "+transactieID;
		text[4] = "Rekening nummer: "+reknummer;
		text[5] = "Withdraw Amount: "+withdrawAmount;
		text[6] = formattedDate;
		
		/*text[7] = "LMAO";
		text[8] = "LMAO";
		text[9] = "LMAO";
		text[10] = "LMAO";
		text[11] = "LMAO";*/

		LabelWriter labelwriter = new LabelWriter(); 
		labelwriter.printLabel(text);	
	}
	
	public void setPrinter(String reknummer, String withdrawAmount, String transactieID)
	{
		this.reknummer = reknummer;
		this.withdrawAmount = withdrawAmount;
		this.transactieID = transactieID;

	}

}
