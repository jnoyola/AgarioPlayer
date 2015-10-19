import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.Callable;

import javax.xml.bind.DatatypeConverter;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

@WebSocket(maxTextMessageSize = 64 * 1024)
public class AgarioConnection {

    @SuppressWarnings("unused")
    private Session session;
    
    AgarioServer server;
    AgarioData data;
    Callable<Void> onCloseCallback;
    Callable<Void> onConnectCallback;
    
	public AgarioConnection(AgarioServer server, AgarioData data, Callable<Void> onCloseCallback, Callable<Void> onConnectCallback) throws Exception {
		this.server = server;
		this.data = data;
		this.onCloseCallback = onCloseCallback;
		this.onConnectCallback = onConnectCallback;
		
		// Establish connection
		WebSocketClient client = new WebSocketClient();
		client.start();
		ClientUpgradeRequest request = new ClientUpgradeRequest();
		request.setHeader("Origin", "http://agar.io");
		System.out.println(server.uri);
		client.connect(this, new URI(server.uri), request);
	}

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.printf("Connection closed: %d - %s%n", statusCode, reason);
        try {
			onCloseCallback.call();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
 
    @OnWebSocketConnect
    public void onConnect(Session session) {
        this.session = session;
        try {
    		// First message
    		ByteBuffer buf = ByteBuffer.wrap(new byte[] { (byte) 0xfe,
						    							  (byte) 0x05,
						    							  (byte) 0x00,
						    							  (byte) 0x00,
						    							  (byte) 0x00 });
        	session.getRemote().sendBytes(buf);
    		
    		// Second message
    		buf = ByteBuffer.wrap(new byte[] { (byte) 0xff,
			    							   (byte) 0x33,
			    							   (byte) 0x18,
			    							   (byte) 0x22,
			    							   (byte) 0x83 });
    		session.getRemote().sendBytes(buf);
    		
    		// Token
    		byte[] bytes = new byte[server.token.length() + 1];
    		bytes[0] = (byte) 0x50;
    		for (int i = 0; i < server.token.length(); ++i)
    			bytes[i+1] = (byte) server.token.charAt(i);
    		session.getRemote().sendBytes(ByteBuffer.wrap(bytes));
    		
    		onConnectCallback.call();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
 
    @OnWebSocketMessage
    public void onMessage(byte bytes[], int offset, int length) {
        ByteBuffer buf = ByteBuffer.wrap(bytes);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        
        int start = 0;
        if (buf.get() == (byte) 240)
        	start += 5;
        
        switch (buf.get(start)) {
        case (byte) 16:
        	break;
        case (byte) 17:
        	break;
        case (byte) 20:
        	break;
        case (byte) 21:
        	break;
        case (byte) 32:
        	break;
        case (byte) 49: // Leaderboard
        	int count = buf.getInt();
        	for (int i = 0; i < count; ++i) {
        		int id = buf.getInt();
        		String name = parseString(buf);
        		data.setLeader(i, id, name);
        	}
        	data.printLeaderboard();
        	break;
        }
    }
    
    private String parseString(ByteBuffer buf) {
    	String str = "";
    	while (true) {
    		char c = buf.getChar();
    		if (c == 0)
    			break;
    		str += c;
    	}
    	return str;
    }
}
