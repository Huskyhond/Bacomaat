package MLB;
import java.sql.*;
import java.util.logging.*;

//import com.jcraft.jsch.*;
//import java.util.Properties;


public class SQLDataBase 
{
    Connection con = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    
    public void connectdb()
	{
        String url = "jdbc:mysql://145.24.222.177/banknode";
        String user = "atm";
        String password = "Emorage00";
        
       
        try 
        {

            Class.forName("com.mysql.jdbc.Driver").newInstance();
            con = DriverManager.getConnection(url, user, password);        	
        } 
        catch (Exception ex) 
        {
            Logger lgr = Logger.getLogger(SQLDataBase.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        } 
	}
	
	public String lock(String rekeningnummer)
	{
		String open = "OPEN";
		String lock = "LOCK";
		try
		{	
			System.out.println("[----lock initiated----]");
			pst = con.prepareStatement("SELECT failCount FROM Account WHERE accountNumber = "+rekeningnummer);
			rs = pst.executeQuery();
			
			int failCount=1;
			while (rs.next()) 
			{            
                failCount = rs.getInt(1)+1;
            }
			
			System.out.print("rekeningnummer: "+rekeningnummer+"\nfailCount: "+(failCount)+"\n");

			if(failCount <3)
			{
				String updateTableSQL2 = "UPDATE Account SET failCount = "+failCount+", pinLock = \"OPEN\" WHERE accountNumber = " + rekeningnummer;
				pst = con.prepareStatement(updateTableSQL2);
				pst .executeUpdate();

			}
			
			if(failCount ==3||failCount ==4)
			{
				String updateTableSQL = "UPDATE Account SET pinLock = \"LOCK\", failCount = 3 WHERE accountNumber = " + rekeningnummer;
				pst = con.prepareStatement(updateTableSQL);
				pst .executeUpdate();
				System.out.println("[----lock done----]\n");
				return lock;
			}
		}
		catch(Exception ex)
		{
			exceptionLog(ex);
		}
		System.out.println("[----lock done----]\n");
		return open;

	}
	
	public void updatedb(String withdrawBalance, String rekeningnummer)
	{	
		try
		{	
			System.out.println("[----updatedb initiated----]");
			String newBalance = "0";
			String oldBalance ="";
			pst = con.prepareStatement("SELECT balance FROM Account WHERE accountNumber = "+rekeningnummer);
			rs = pst.executeQuery();
			while (rs.next()) 
			{
				oldBalance = Integer.toString(rs.getInt(1));
                newBalance = Integer.toString(rs.getInt(1)-Integer.parseInt(withdrawBalance));
            }
			pst = con.prepareStatement("UPDATE Account SET balance = ? WHERE accountNumber = "+rekeningnummer);
			pst.setString(1, newBalance);
			pst.executeUpdate();
			
            System.out.print("rekeningnummer: "+rekeningnummer+"\nold balance: "+oldBalance+"\nnew balance: "+newBalance+"\nwithdraw: "+withdrawBalance+"\n");

		}
		catch(Exception ex)
		{
			exceptionLog(ex);
		}
		System.out.println("[----updatedb done----]\n");
	}
	public int getBalance(String rekeningnummer)
	{
		try
		{
			System.out.println("[----getBalance initiated----]");
			pst = con.prepareStatement("SELECT balance FROM Account WHERE accountNumber = "+rekeningnummer);
			rs = pst.executeQuery();
			while (rs.next()) 
			{
                System.out.print("rekeningnummer: "+rekeningnummer+"\nbalance: "+rs.getInt(1)+"\n");
        		System.out.println("[----getBalance done----]\n");
                return rs.getInt(1);     
            }
		}
		catch(Exception ex)
		{
			exceptionLog(ex);
		}
		System.out.println("[----getBalance done----]\n");
		return 0;
	}
	
	public int checkAccountnumber(String rekeningnummer)
	{
		try
		{
			System.out.println("[----checkAccountnumber initiated----]");
			pst = con.prepareStatement("SELECT count(*) FROM Account where accountNumber = "+rekeningnummer);
			rs = pst.executeQuery();
			while (rs.next()) 
			{
                        System.out.print("rekeningnummer: "+rekeningnummer+"\nExist in db: "+rs.getInt(1)+"\n");
        		System.out.println("[----checkAccountnumber done----]\n");
        		return rs.getInt(1);
                }
		}
		catch(Exception ex)
		{
		exceptionLog(ex);
		}
		return 0;
	}
	
	//EXCEPTION ERROR
	public void exceptionLog(Exception ex)
	{
            Logger lgr = Logger.getLogger(SQLDataBase.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
	}
}