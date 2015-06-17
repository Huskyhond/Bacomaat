package MLB;
import lejos.nxt.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
public class Dispenser
{
	public static void printMoney(int ma, int mb, int mc) //50 , 20 , 10 || ma = onderste tray, mc = bovenste
	{
		//Setting java files process:
		ArrayList<String> lines = new ArrayList<String>();
		String line = null;
		try 
		{
            File f1 = new File("./Lejos.java");
            FileReader fr = new FileReader(f1);
            BufferedReader br = new BufferedReader(fr);
            while ((line = br.readLine()) != null) 
			{
                if (line.contains("int dispenserA"))
				line = "int dispenserA = " + ma + ";";
                else if(line.contains("int dispenserB"))
				line = "int dispenserB = " + mb + ";";
                else if(line.contains("int dispenserC"))
				line = "int dispenserC = " + mc + ";";
                line = line + "\r\n";
                lines.add(line);
			}
            fr.close();
            br.close();
			
            FileWriter fw = new FileWriter(f1);
            BufferedWriter out = new BufferedWriter(fw);
            for(String s : lines)
			out.write(s);
            out.flush();
            out.close();
		} catch (Exception ex) 
		{
            ex.printStackTrace();
		}
		
		//Building process:
		ProcessBuilder builder = new ProcessBuilder(
		"cmd.exe", "/c", "nxjc Lejos.java && nxj -r Lejos");
        builder.redirectErrorStream(true);
        try 
		{
	        Process p = builder.start();
	        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
	        while (true) 
			{
	            line = r.readLine();
	            if (line == null) { break; }
	            System.out.println(line);
			}
		}
        catch(IOException ie) 
		{	
		}
	}
}
