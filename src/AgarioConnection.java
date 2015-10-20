import java.awt.Color;
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
    Callable<Void> onUpdateCallback;
    
	public AgarioConnection(AgarioServer server, AgarioData data, Callable<Void> onCloseCallback, Callable<Void> onUpdateCallback) throws Exception {
		this.server = server;
		this.data = data;
		this.onCloseCallback = onCloseCallback;
		this.onUpdateCallback = onUpdateCallback;
		
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
    		ByteBuffer buf = ByteBuffer.wrap(new byte[] {
    				(byte) 0xfe,
					(byte) 0x05,
					(byte) 0x00,
					(byte) 0x00,
					(byte) 0x00 });
        	session.getRemote().sendBytes(buf);
    		
    		// Second message
    		buf = ByteBuffer.wrap(new byte[] {
    				(byte) 0xff,
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
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
 
    @OnWebSocketMessage
    public void onMessage(byte bytes[], int offset, int length) {
    	System.out.println(DatatypeConverter.printHexBinary(bytes));
        ByteBuffer buf = ByteBuffer.wrap(bytes);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        
        int start = 0;
        if (buf.get() == 240)
        	start += 5;
        
        switch (buf.get(start)) {
        case 16:
        	msgUpdate(buf);
        	break;
        case 17:
        	break;
        case 20:
        	break;
        case 21:
        	break;
        case 32:
        	break;
        case 49: // Leaderboard
        	msgUpdateLeaderboard(buf);
        	break;
        case 50:
        	break;
        case 64: // Start
        	msgStart(buf);
        	break;
        }
    }
    
    private String parseUtf16(ByteBuffer buf) {
    	String str = "";
    	while (true) {
    		char c = buf.getChar();
    		if (c == 0)
    			break;
    		str += c;
    	}
    	return str;
    }
    
    private String parseUtf8(ByteBuffer buf) {
    	String str = "";
    	while (true) {
    		byte c = buf.get();
    		if (c == 0)
    			break;
    		str += (char) c;
    	}
    	return str;
    }
    
    private short unsign(byte b) {
    	short s = b;
    	if (s < 0) {
    		s += 256;
    	}
    	return s;
    }
    
    private void msgUpdate(ByteBuffer buf) {
    	/*sb = F = Date.now();
    	if (!ha) {
    		ha = true;
    		Wb();
    	}
    	boolean Ra = false;*/
    	
    	short count = buf.getShort();
    	System.out.println("n: " + count);
    	for (int i = 0; i < count; ++i) {
    		int w = buf.getInt();
    		int l = buf.getInt();
    	}
    	
    	int f = 0;
    	while (true) {
    		int id = buf.getInt();
    		if (id == 0)
    			break;
    		++f;
    		int x = buf.getInt();
    		int y = buf.getInt();
    		short size = buf.getShort();
    		short r = unsign(buf.get());
    		short g = unsign(buf.get());
    		short b = unsign(buf.get());
    		Color color = new Color(r, g, b);
    		// isFood = (S & 1) != 0
    		
    		byte S = buf.get();
    		if ((S & 2) != 0) {
    			int offset = buf.getInt();
    			buf.position(buf.position() + offset);
    		}
    		if ((S & 4) != 0) {
    			System.out.println(parseUtf8(buf));
    		}
    		
    		String name = parseUtf16(buf);
    		AgarioData.Cell cell = data.getCell(id);
    		if (cell == null) {
    			cell = data.addCell(id, x, y, size, color, name);
    		} else {
    			cell.x = x;
    			cell.y = y;
    			cell.size = size;
    		}
    	}
    	try {
			onUpdateCallback.call();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private void msgUpdateLeaderboard(ByteBuffer buf) {
    	int count = buf.getInt();
    	for (int i = 0; i < count; ++i) {
    		int id = buf.getInt();
    		String name = parseUtf16(buf);
    		data.setLeader(i, id, name);
    	}
    	//data.printLeaderboard();
    }
    
    private void msgStart(ByteBuffer buf) {
    	/*double xa = buf.getDouble();
    	double ya = buf.getDouble();
    	double za = buf.getDouble();
    	double Aa = buf.getDouble();
    	double ia = (za + xa) / 2;
    	double ja = (Aa + ya) / 2;
    	double ka = 1;*/
		buf.position(buf.position() + 32);
    	if (buf.remaining() > 0) {
    		buf.position(buf.position() + 4);
    		System.out.println("Server Version: " + parseUtf16(buf));
    	}
    }
}
