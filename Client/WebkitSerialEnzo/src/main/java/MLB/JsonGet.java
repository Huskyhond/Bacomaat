package MLB;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.simple.JSONValue;
import org.json.*;
import org.json.simple.parser.*;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author NiekLap
 */


public class JsonGet 
{
    
    public SocketIOClient client;
    private Printer printer;
    
    public JsonGet(Printer _printer)
    {
    	printer = _printer;
    }
    
    private String httpsGet(String url) throws Exception {
        String USER_AGENT = "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:36.0) Gecko/20100101 Firefox/36.0";
        URL obj = new URL(url);
        
        SslCert.trustMe(); // SSL certificaat laden ( self signed).
        
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session)
            {
                if (hostname.equals("145.24.222.177"))
                    return true;
                return false;
            }
        });

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        // Debugging
        //int responseCode = con.getResponseCode();
        //System.out.println("\nSending 'GET' request to URL : " + url);
        //System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
        new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        return response.toString();

    }
    
    private String token = "?token=Dk49D9dka13D9f03S9dj1D9da01Akd03";
    private String url = "https://145.24.222.177";
    
    public boolean checkAccount(String rekeningnummer)
    {
    	try
    	{
   	        String https = httpsGet(url+"/balance/"+rekeningnummer+token);
   	    	JSONObject obj = new JSONObject(https);
   	    	String message = obj.getString("message");
   	    	int error = obj.getInt("error");
   	 
   	    	System.out.println("error: "+error);
   	    	System.out.println("message: "+message);
   	    	return false; 	    	
       }
       catch(Exception e)
       {
       	System.out.println("account bestaat");
       }
    	return true;
    }
    
    
    public int getBalance(String rekeningnummer)//return saldo
    {
    	try
    	{
   	        String https = httpsGet(url+"/balance/"+rekeningnummer+token);
   	    	JSONObject obj = new JSONObject(https);
   	    	int bankid = obj.getInt("bankid");
   	    	String pasid = obj.getString("pasid");
   	    	int saldo = obj.getInt("saldo");
   	    	int failCount = obj.getInt("failCount");
   	    	int dailyLimit = obj.getInt("dailyLimit");

   	    	System.out.println("bankid: "+bankid);
   	    	System.out.println("pasid: "+pasid);
   	    	System.out.println("saldo: "+saldo);
   	    	System.out.println("failCount: "+failCount);
   	    	System.out.println("dailyLimit: "+dailyLimit);
   	    	
   	    	return saldo;

       }
       catch(Exception e)
       {
       	System.out.println(e.getMessage());
       }
    	return 0;
    }
    
    public void withdraw(String rekeningnummer, String withdrawAmount)//return transid,accountnumber,amount,machineid,date naar PRINTER
    {
    	System.out.println("JsonGet: withdraw:"+withdrawAmount);
    	String token = withdrawAmount+"&token=Dk49D9dka13D9f03S9dj1D9da01Akd03";

    	try
    	{
    		String https = httpsGet(url+"/balance/"+rekeningnummer+"?changeBalance="+token);
	    	JSONObject obj1 = new JSONObject(https);
	    	JSONObject obj2 = obj1.getJSONObject("transaction");
	    	
	    	int transactionid = obj2.getInt("id");
	    	String accountNumber = obj2.getString("accountNumber");
	    	int amount = obj2.getInt("amount");
	    	int machineid = obj2.getInt("machineID");
	    	String date = obj1.getString("date");
	    	
   	    	printer.setPrinter(accountNumber, Integer.toString(amount), Integer.toString(transactionid),date);

		    
		    System.out.println("transactionid: "+transactionid);
		    System.out.println("accountNumber: "+ accountNumber);
	    	System.out.println("amount: "+amount);
	    	System.out.println("machineid: "+machineid);
	    	System.out.println("date: "+date);
    	}
    	catch(Exception e)
    	{
           	System.out.println(e.getMessage());
    	}
    }
    
    public void pinFail(String rekeningnummer)//return failCount
    {
    	try
    	{
    	String https = httpsGet(url+"/account/"+rekeningnummer+"/failed"+token);
    	JSONObject obj = new JSONObject(https);
    	int failCount = obj.getInt("failCount");
    	
    	System.out.println("failCount: "+failCount);
    	}
    	catch(Exception e)
    	{
           	System.out.println(e.getMessage());
    	}
    }
    
    public void pinSucces(String rekeningnummer)
    {
    	try
    	{
        	String https = httpsGet(url+"/account/"+rekeningnummer+"/passed"+token);
    		
    	}
    	catch(Exception e)
    	{
           	System.out.println(e.getMessage());
    	}
    }
    
    public void test()
    {
    	try
    	{
	    	String https = httpsGet("https://145.24.222.177/balance/MLBI0200000002?changeBalance=1&token=Dk49D9dka13D9f03S9dj1D9da01Akd03");
	    	JSONObject obj1 = new JSONObject(https);
	    	JSONObject obj2 = obj1.getJSONObject("transaction");
	    	int id = obj2.getInt("id");
	    	String accountNumber = obj2.getString("accountNumber");
	    	int amount = obj2.getInt("amount");
	    	int machineid = obj2.getInt("machineID");
	    	
	    	System.out.println("id: "+id);
	    	System.out.println("accountNumber: "+accountNumber);
	    	System.out.println("amount: "+amount);
	    	System.out.println("machineid: "+machineid);

	

    	}
    	catch(Exception e)
    	{
           	System.out.println(e.getMessage());
    	}
    }
    
}
