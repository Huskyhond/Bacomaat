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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author NiekLap
 */


public class Main 
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
   
    public void connect() {

    
      //** -- VOORBEELD HTTPS REQUEST NAAR SERVER + PARSEN NAAR JAVA! ///
        try {
        String html = httpsGet("https://145.24.222.177/balance/200000001");

        //^ BEKIJK DE ServerJson classe
       
      }
      catch(Exception e) {
          System.out.println(e.getMessage());
      }
        
     ///** -- EINDE VOORBEELD ///
      
      ///** -- START SOCKET IO SERVER 
      Configuration config = new Configuration();
      config.setHostname("localhost");
      config.setPort(80);
      SocketIOServer server = new SocketIOServer(config);

      server.addConnectListener(new ConnectionListener() {
         @Override
         public void onConnect(SocketIOClient clientc) {
             client = clientc; // Set static connection ( only 1 allowed )
             System.out.println("Connected");
             client.sendEvent("update", "{"
                                        + "\"page\": \"code\""
                                      + "}");
         }
        
      });
      
      server.start();
      //** -- EINDE SOCKET IO SERVER 
    }
    
}
