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
			
	static final Callable<Void> playCallback =
			new Callable<Void>() {
				public Void call() {
					play();
					return null;
				}
			};
			
	static AgarioData data = new AgarioData();

	public static void main(String[] args) {
		connect();
	}
	
	public static void connect() {
		try {
			AgarioServer server = new AgarioServer(AgarioServer.China);
			AgarioConnection conn = new AgarioConnection(server, data, reconnectCallback, playCallback);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void play() {
		//AgarioGui gui = new AgarioGui();
	}
}
