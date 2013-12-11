package app_kvServer;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;



import logger.LogSetup;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import server.KVClientConnection;
import server.KVServerListener;
import server.storage.Storage;


public class KVServer extends Thread implements KVServerListener{
	
	private static Logger logger = Logger.getRootLogger();
	
	private int port;
    private ServerSocket serverSocket;
    private boolean running;
    
    //private HashMap<String, String> data;
    
    /*
    Java HashMap is NOT synchronized. So we use ConcurrentHashMap
    as it allows concurrent modification of map 
	*/
    
    //ConcurrentHashMap<String, String> data;    
    private Storage storage;
    
    /**
	 * Start KV Server at given port
	 * @param port given port for storage server to operate
	 */
    /**
     * Constructs a KV Server object which listens to connection attempts 
     * at the given port.
     * 
     * @param port a port number which the Server is listening to in order to 
     * 		establish a socket connection to a client. The port number should 
     * 		reside in the range of dynamic ports, i.e 49152 – 65535.
     */
	public KVServer(int port) {
		this.port = port;
		storage = Storage.init();
//		String[] args = new String[2];
//		args[0]=Integer.toString(port);
//		args[1]="DEBUG";
//		KVServer.main(args);
	}
    
    /**
     * Initializes and starts the server. 
     * Loops until the the server should be closed.
     */
    public void run() {
        
    	running = initializeServer();
        
        if(serverSocket != null) {
	        while(isRunning()){
	            try {
	                Socket client = serverSocket.accept();                
	                KVClientConnection connection = 
	                		new KVClientConnection(client);
	                
	                connection.addListener(this);
	                
	                new Thread(connection).start();
	                	                
	                logger.info("Connected to " 
	                		+ client.getInetAddress().getHostName() 
	                		+  " on port " + client.getPort());
	            } catch (IOException e) {
	            	logger.error("Error! " +
	            			"Unable to establish connection. \n", e);
	            }
	        }
        }
        logger.info("Server stopped.");
    }
    
    private boolean isRunning() {
        return this.running;
    }

    /**
     * Stops the server insofar that it won't listen at the given port any more.
     */
    public void stopServer(){
        running = false;
        try {
			serverSocket.close();
		} catch (IOException e) {
			logger.error("Error! " +
					"Unable to close socket on port: " + port, e);
		}
    }

    private boolean initializeServer() {
    	logger.info("Initialize server ...");
    	try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server listening on port: " 
            		+ serverSocket.getLocalPort());
            logger.info("Server listening on port: " 
            		+ serverSocket.getLocalPort());    
            return true;
        
        } catch (IOException e) {
        	logger.error("Error! Cannot open server socket:");
            if(e instanceof BindException){
            	logger.error("Port " + port + " is already bound!");
            }
            return false;
        }
    }
    /**
     * 
     * @param key
     * @param value
     * @return
     */
    public String put(String key, String value){
  		//return data.put(key, value);
        return storage.put(key, value);
	}
    
    /**
     * 
     */
    public String delete(String key){
    	
		//return data.remove(key);
        return storage.delete(key);
		
	}
    
	/**
	 * 
	 * @param key
	 * @return
	 */
	public String get(String key){				
		//return data.get(key);
	    return storage.get(key);
	}

	private static String setLevel(String levelString) {
		
		if(levelString.equals(Level.ALL.toString())) {
			logger.setLevel(Level.ALL);
			return Level.ALL.toString();
		} else if(levelString.equals(Level.DEBUG.toString())) {
			logger.setLevel(Level.DEBUG);
			return Level.DEBUG.toString();
		} else if(levelString.equals(Level.INFO.toString())) {
			logger.setLevel(Level.INFO);
			return Level.INFO.toString();
		} else if(levelString.equals(Level.WARN.toString())) {
			logger.setLevel(Level.WARN);
			return Level.WARN.toString();
		} else if(levelString.equals(Level.ERROR.toString())) {
			logger.setLevel(Level.ERROR);
			return Level.ERROR.toString();
		} else if(levelString.equals(Level.FATAL.toString())) {
			logger.setLevel(Level.FATAL);
			return Level.FATAL.toString();
		} else if(levelString.equals(Level.OFF.toString())) {
			logger.setLevel(Level.OFF);
			return Level.OFF.toString();
		} else {
			return LogSetup.UNKNOWN_LEVEL;
		}
	}
    
    /**
     * Main entry point for the echo server application. 
     * @param args contains the port number at args[0].
     */
    public static void main(String[] args) {
    	try {
			new LogSetup("logs/server/server.log", Level.ALL);
			if(args.length < 1 && args.length>2) {
				System.out.println("Error! Invalid number of arguments!");
				System.out.println("Usage: KVServer <port> <logLevel>!");
				System.out.println("Usage: <logLevel> is optional. Possible levels <ALL | DEBUG | INFO | WARN | ERROR | FATAL | OFF>");
				System.exit(1);
			} else {
				if(args.length==1) {
					int port = Integer.parseInt(args[0]);
					new KVServer(port).start();	
				} else if(LogSetup.isValidLevel(args[1])) {
					int port = Integer.parseInt(args[0]);
					setLevel(args[1]); 
					new KVServer(port).start();
				} else {
					System.out.println("Error! Invalid logLevel");
					System.out.println("Possible levels <ALL | DEBUG | INFO | WARN | ERROR | FATAL | OFF>");
					System.exit(1);
				}
				
				
			}
		} catch (IOException e) {
			System.out.println("Error! Unable to initialize logger!");
			e.printStackTrace();
			System.exit(1);
		} catch (NumberFormatException nfe) {
			System.out.println("Error! Invalid argument <port>! Not a number!");
			System.out.println("Usage: KVServer <port> <logLevel>!");
			System.out.println("Usage: KVServer <port> <logLevel>!");
			System.out.println("Usage: <logLevel> is optional. Possible levels <ALL | DEBUG | INFO | WARN | ERROR | FATAL | OFF>");
			System.exit(1);
		}
    }
	
	
	
}
