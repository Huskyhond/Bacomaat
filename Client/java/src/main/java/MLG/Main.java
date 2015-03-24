package MLG;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author NiekLap
 */
public class Main {
    
    public static SocketIOClient client;
    
    public static void main(String args[]) {
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
                                        + "\"data\": \"Hoihoihoi\""
                                      + "}");
         }
        
      });
      
      server.start();
    }
}
