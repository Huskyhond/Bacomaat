package MLB;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.json.JSONObject;
/*
import org.json.JSONArray;
import org.json.simple.JSONValue;
import org.json.*;
import org.json.simple.parser.*;*/


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
    private Webkit wk;
    private String token;
    private String url = "https://145.24.222.177";
	private String https = null;
	private boolean balance;
	private int failedAttempts;
    
    public JsonGet(Printer _printer, Webkit _wk)
    {
    	printer = _printer;
    	wk = _wk;
    }
    
    public String sendPost(String url,String urlParameters,String token) throws Exception 
    {
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

		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		con.setRequestProperty("token",token);


		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		//System.out.println("\nSending 'POST' request to URL : " + url);
		//System.out.println("Post parameters : " + urlParameters);
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
  
    public int login(String rekeningnummer, String pin)//<<---DONE
    {
    	System.out.println("Trying to login using " + rekeningnummer + " & " + pin);
    	try
    	{
   	        https = sendPost(url+"/login","cardId="+rekeningnummer+"&pin="+pin,"");
   	        System.out.println(https);
    		JSONObject obj1 = new JSONObject(https);
	    	JSONObject obj = obj1.getJSONObject("success");
   	    	token = obj.getString("token");   
    		System.out.println("cardId: Exist , pin: SUCCES, token: "+token);
   	    	return 2; 	    	
   	    	
       }
       catch(Exception e)
       {	
    	   try
    	   {
        	   return catchError(https);
    	   }
    	   catch(Exception ex)
    	   {
    	       	System.out.println("error in catchError");
    	   }
    	   return 0;
       }
    }
    
    
    public int getBalance(String rekeningnummer) //<<---DONE
    {
    	System.out.println(rekeningnummer);
    	try
    	{
   	        https = sendPost(url+"/balance/","",token);
   	    	JSONObject obj1 = new JSONObject(https);
   	    	JSONObject obj = obj1.getJSONObject("success");
   	    	int bankid = obj.getInt("bankid");
   	    	String cardId = obj.getString("cardId");
   	    	int saldo = obj.getInt("saldo");
   	    	int failCount = obj.getInt("failCount");
   	    	int dailyLimit = obj.getInt("dailyLimit");
   	    	
   	    	System.out.println("bankid: "+bankid);
   	    	System.out.println("cardId: "+cardId);
   	    	System.out.println("saldo: "+saldo);
   	    	System.out.println("failCount: "+failCount);
   	    	System.out.println("dailyLimit: "+dailyLimit);	
   	    	return saldo;

       }
       catch(Exception e)
       {
    	   try
    	   {
        	   catchError(https);
    	   }
    	   catch(Exception ex)
    	   {
    		   System.out.println("error in catchError");
    	   }
       }
    	return 0;
    }
    
    public void withdraw(String rekeningnummer, String withdrawAmount)//<-----DONE
    {
    	System.out.println("JsonGet: withdraw:"+withdrawAmount);
    	Date date = new Date();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    	String datenow = sdf.format(new Date());
    	try
    	{
    		https = sendPost(url+"/withdraw/","amount="+withdrawAmount,token);
    		JSONObject obj = new JSONObject(https);
   	    	JSONObject obj1 = obj.getJSONObject("success");
	    	//JSONObject obj2 = obj1.getJSONObject("transaction");
	    	
	    	//int transactionid = obj2.getInt("id");
	    	//int amount = obj2.getInt("amount");
	    	//int machineid = obj2.getInt("machineID");
	    	//String date = obj2.getString("date");
	    	
   	    	printer.setPrinter(rekeningnummer, withdrawAmount,datenow);

		    
		  //  System.out.println("transactionid: "+transactionid);
		    System.out.println("cardId: "+ rekeningnummer);
	    	//System.out.println("amount: "+amount);
	    	//System.out.println("machineid: "+machineid);
	    	//System.out.println("date: "+date);
	    	balance = true;
    	}
    	catch(Exception e)
    	{
    	   try
      	   {
          	   catchError(https);
      	   }
      	   catch(Exception ex)
      	   {
      		 System.out.println("error in catchError");
      	   }
    	}
    }

    public void test()
    {
    	try
    	{
	    	String https = sendPost("https://145.24.222.177/balance/","",token);
	    	JSONObject obj1 = new JSONObject(https);
	    	JSONObject obj2 = obj1.getJSONObject("error");
	    
	    	int errorCode = obj2.getInt("error");
	    	String message = obj2.getString("message");
	    	System.out.println(errorCode);
	    	System.out.println(message);
	

    	}
    	catch(Exception e)
    	{
           	System.out.println(e.getMessage());
    	}
    }
    public int catchError(String https)
    {
    	try
    	{
    		System.out.println("catchError: ");
	    	JSONObject obj1 = new JSONObject(https);
	    	JSONObject obj2 = obj1.getJSONObject("error");
	    	
	    	int code = obj2.getInt("code");
	    	String message = obj2.getString("message");
	    	
	    	System.out.println("Error: "+code);
	    	System.out.println("Message: "+message);
	    	if(code == 14) 
	    	{
	    		return 0;
	    	}
	    	else if(code == 15)
	    	{
	    		failedAttempts = obj2.getInt("failedAttempts");
				wk.sendFailCount(failedAttempts);
				

	    		System.out.println("failedAttempts: "+failedAttempts);
	    	}
	    	else if(code == 16) 
	    	{
	    		return 0;
	    	}
	    	else if(code == 32)
	    	{
	    		balance = false;
	    	}

    	}
    	catch(Exception e)
    	{
           	System.out.println(e.getMessage());
    	}
    	return  1;
    }
    public int getAttempts()
    {
    	return failedAttempts;
    }
    
    public boolean getBooleanBalance()
    {
    	return balance;
    }
}
