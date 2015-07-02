package MLB;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


/**
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
             connected = true;
             
            }
        });
       
        server.addConnectListener(new ConnectionListener() {
    	  @Override
          public void onDisconnect(SocketIOClient clientc) 
          {
           client = null; //  Disconnect
           System.out.println("Disconnected");
           connected = false;
           
          }
        });
        
      server.start();
    }
    
    public void clearMoney() {
        client.sendEvent("clear");
    }
    
    public void sendObject(JSONObject sendObj)
    {
        if (connected)
        {
            client.sendEvent("update", sendObj.toJSONString());
        }
        else 
        {
            System.out.println("Je bent niet connected met Node Webkit");
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
            System.out.println("Je bent niet connected met Node Webkit, kill");
        }
    }
}