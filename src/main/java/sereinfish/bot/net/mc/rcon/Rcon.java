package sereinfish.bot.net.mc.rcon;

import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.net.mc.rcon.ex.AuthenticationException;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Random;

public class Rcon {

	private final Object sync = new Object();
	private final Random rand = new Random();

	private int requestId;
	private Socket socket;
	private RconConf config;

	private Charset charset;

	/**
	 * 创建、连接和验证一个新的Rcon对象
	 *
	 * @param config 配置文件
	 *
	 * @throws IOException
	 * @throws AuthenticationException
	 */
	public  Rcon(RconConf config) throws IOException, AuthenticationException {
		// Default charset is utf8
		this.charset = Charset.forName("UTF-8");
		this.config = config;

		// Connect to host
		this.connect();
	}

    /**
     *
     * @return 这个Rcon对应配置文件
     */
    public RconConf getConfig() {
        return config;
    }

    /**
	 * 连接到rcon服务器
	 *
	 * @throws IOException
	 * @throws AuthenticationException
	 */
	public void connect() throws IOException, AuthenticationException {
		String host = config.getIp();
		int port = config.getPort();
		byte[] password = config.getPassword().getBytes();

		if(host == null || host.trim().isEmpty()) {
			throw new IllegalArgumentException("Host can't be null or empty");
		}
		
		if(port < 1 || port > 65535) {
			throw new IllegalArgumentException("Port is out of range");
		}
		
		// Connect to the rcon server
		synchronized(sync) {
			// New random request id
			this.requestId = rand.nextInt();
			
			// We can't reuse a socket, so we need a new one
			this.socket = new Socket(host, port);
		}
		
		// Send the auth packet
		RconPacket res = this.send(RconPacket.SERVERDATA_AUTH, password);
		
		// Auth failed
		if(res.getRequestId() == -1) {
			throw new AuthenticationException("Password rejected by server");
		}
	}
	
	/**
	 * 断开与当前服务器的连接
	 * 
	 * @throws IOException
	 */
	public void disconnect() throws IOException {
		synchronized(sync) {
			this.socket.close();
		}
	}
	
	/**
	 * 向服务器发送命令
	 * 
	 * @param payload The command to send
	 * @return The payload of the response
	 * 
	 * @throws IOException
	 */
	private String command(String payload) throws IOException, AuthenticationException {
		connect();
		if(payload == null || payload.trim().isEmpty()) {
			throw new IllegalArgumentException("Payload can't be null or empty");
		}
		
		RconPacket response = this.send(RconPacket.SERVERDATA_EXECCOMMAND, payload.getBytes());
		
		String result =  new String(response.getPayload(), this.getCharset());

		if (result.endsWith("\n")) {
			return result.substring(0, result.length() - 1);
		}
		disconnect();
		return result;
	}

	/**
	 * rcon命令执行
	 * @param payload
	 * @return
	 * @throws IOException
	 * @throws AuthenticationException
	 */
	public String cmd(String payload) throws IOException, AuthenticationException {
		try {
			return command(payload);
		} catch (IOException e) {
			disconnect();
			throw e;
		} catch (AuthenticationException e) {
			disconnect();
			throw e;
		}
	}
	
	private RconPacket send(int type, byte[] payload) throws IOException {
		synchronized(sync) {
			return RconPacket.send(this, type, payload);
		}
	}

	public int getRequestId() {
		return requestId;
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public Charset getCharset() {
		return charset;
	}
	
	public void setCharset(Charset charset) {
		this.charset = charset;
	}

}
