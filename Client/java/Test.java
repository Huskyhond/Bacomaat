import com.corundumstudio.socketio.*;

public class Test {

	public static void main(String[] args) {
				
		Configuration config = new Configuration();
	    config.setHostname("localhost");
	    config.setPort(80);

	  SocketIOServer server = new SocketIOServer(config);
	  
	}

}