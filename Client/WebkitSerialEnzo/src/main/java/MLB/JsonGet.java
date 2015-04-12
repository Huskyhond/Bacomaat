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
    
    public static SocketIOClient client;
    
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
   
    public void getBalance(String rekeningnummer)
    {
    	try
    	{
   	        String str = httpsGet(url+"/balance/"+rekeningnummer+token);
   	    	JSONObject obj = new JSONObject(str);
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

       }
       catch(Exception e)
       {
       	System.out.println(e.getMessage());
       }
    }
    
}
