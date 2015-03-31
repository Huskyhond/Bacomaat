package MLB;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


/**
 * Nu doet iet het weer. Ik zie trouwens dat jij projects niet import en ook geen maven gebruikt, dit gaat telkens fout qua dependancies btw
 * Dusehh.. Hopelijk doet dit het dalijk, als ie klaar is met updaten.
 * @author Gun
 */
public class WebkitConnect
{
    public static SocketIOClient client;
    
    JSONParser parser=new JSONParser();
    
    public void connect()
    {
    Configuration config = new Configuration();
      config.setHostname("localhost");
      config.setPort(80);
      SocketIOServer server = new SocketIOServer(config);
      server.addConnectListener(new ConnectionListener() 
      {
         @Override
         public void onConnect(SocketIOClient clientc) {
             client = clientc; // Set static connection ( only 1 allowed )
             System.out.println("Connected");
             client.sendEvent("{" +
                            "\"page\": \"code\"" +
                        "}"); 
         }
        
      });
      
      server.start();
    }
    public void sendObject(JSONObject sendObj)
    {
        client.sendEvent("update", sendObj.toString());
    }
}