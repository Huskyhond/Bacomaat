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
    static boolean connected;
    // b
    
    public WebkitConnect()
    {
        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(80);
        SocketIOServer server = new SocketIOServer(config);
        server.addConnectListener(new ConnectionListener() 
        {
            @Override
            public void onConnect(SocketIOClient clientc) 
            {
             client = clientc; // Set static connection ( only 1 allowed )
             System.out.println("Connected");
             client.sendEvent("update", "{" +
                            "\"page\": \"code\"" +
                        "}");
             connected = true;
             
            }
            public void onDisConnect(SocketIOClient clientc) 
            {
             client = null; //  Disconnect
             System.out.println("Disconnected");
             connected = false;
             
            }
      });
      
      server.start();
    }
    public void sendObject(JSONObject sendObj)
    {
        if (connected)
        {
            client.sendEvent("update", sendObj.toJSONString());
        }
        else 
        {
            System.out.println("je bent niet connected kill");
        }
    }
    public void sendObjectArray(JSONObject sendObj)
    {
        if (connected)
        {
            client.sendEvent("update", sendObj.toJSONString());
        }
        else
        {
            System.out.println("je bent niet connected kill");
        }
    }
}