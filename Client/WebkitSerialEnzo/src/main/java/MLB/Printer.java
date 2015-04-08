package MLB;
import java.util.Date;
import java.text.SimpleDateFormat;

public class Printer 
{
	public void print()
	{
		
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy h:mm:ss a");
		String formattedDate = sdf.format(date);
		
		String[] text = new String[7];
		text[0] = "Welcome to";
		text[1] = "MLB";
		text[2] = "Automaat ID: MLB-1";
		text[3] = "Transactie ID: ";
		text[4] = "Rekening nummer: ";
		text[5] = "Withdraw Amount: ";
		text[6] = formattedDate;
		/*text[7] = "LMAO";
		text[8] = "LMAO";
		text[9] = "LMAO";
		text[10] = "LMAO";
		text[11] = "LMAO";*/

		LabelWriter labelwriter = new LabelWriter(); 
		labelwriter.printLabel(text);

		
	}
}
