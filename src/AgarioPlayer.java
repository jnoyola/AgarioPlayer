import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.Callable;

import org.json.simple.parser.ParseException;

public class AgarioPlayer {
	
	static final Callable<Void> reconnectCallback =
			new Callable<Void>() {
				public Void call() {
					connect();
					return null;
				}
			};
			
	static final Callable<Void> updateCallback =
			new Callable<Void>() {
				public Void call() {
					gui.update(data);
					return null;
				}
			};
			
	static AgarioData data = new AgarioData();
	static AgarioGui gui = new AgarioGui();

	public static void main(String[] args) {
		connect();
	}
	
	public static void connect() {
		try {
			AgarioServer server = new AgarioServer(AgarioServer.Turkey);
			AgarioConnection conn = new AgarioConnection(server, data, reconnectCallback, updateCallback);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
