package sereinfish.bot.net.mc;
 
import com.google.gson.Gson;
import sereinfish.bot.mlog.SfLog;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author zh32 <zh32 at zh32.de>
 */
public class ServerListPing {
    
    private InetSocketAddress host;
    private int timeout = 7000;
    private Gson gson = new Gson();
    
    public void setAddress(InetSocketAddress host) {
        this.host = host;
    }
 
    public InetSocketAddress getAddress() {
        return this.host;
    }
 
    void setTimeout(int timeout) {
        this.timeout = timeout;
    }
 
    int getTimeout() {
        return this.timeout;
    }
 
    public int readVarInt(DataInputStream in) throws IOException {
        int i = 0;
        int j = 0;
        while (true) {
            int k = in.readByte();
            i |= (k & 0x7F) << j++ * 7;
            if (j > 5) throw new RuntimeException("VarInt too big");
            if ((k & 0x80) != 128) break;
        }
        return i;
    }
 
    public void writeVarInt(DataOutputStream out, int paramInt) throws IOException {
        while (true) {
            if ((paramInt & 0xFFFFFF80) == 0) {
              out.writeByte(paramInt);
              return;
            }

            out.writeByte(paramInt & 0x7F | 0x80);
            paramInt >>>= 7;
        }
    }
    
    public StatusResponse fetchData() throws IOException {

        Socket socket = new Socket();
        OutputStream outputStream;
        DataOutputStream dataOutputStream;
        InputStream inputStream;
        InputStreamReader inputStreamReader;

        socket.setSoTimeout(this.timeout);

        socket.connect(host, timeout);

        outputStream = socket.getOutputStream();
        dataOutputStream = new DataOutputStream(outputStream);

        inputStream = socket.getInputStream();
        inputStreamReader = new InputStreamReader(inputStream);

        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream handshake = new DataOutputStream(b);
        handshake.writeByte(0x00); //packet id for handshake
        writeVarInt(handshake, 4); //protocol version
        writeVarInt(handshake, this.host.getHostString().length()); //host length
        handshake.writeBytes(this.host.getHostString()); //host string
        handshake.writeShort(host.getPort()); //port
        writeVarInt(handshake, 1); //state (1 for handshake)

        writeVarInt(dataOutputStream, b.size()); //prepend size
        dataOutputStream.write(b.toByteArray()); //write handshake packet


        dataOutputStream.writeByte(0x01); //size is only 1
        dataOutputStream.writeByte(0x00); //packet id for ping
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        int size = readVarInt(dataInputStream); //size of packet
        int id = readVarInt(dataInputStream); //packet id
        
        if (id == -1) {
            throw new IOException("Premature end of stream.");
        }
        
        if (id != 0x00) { //we want a status response
            throw new IOException("Invalid packetID");
        }
        int length = readVarInt(dataInputStream); //length of json string
        
        if (length == -1) {
            throw new IOException("Premature end of stream.");
        }

        if (length == 0) {
            throw new IOException("Invalid string length.");
        }
        
        byte[] in = new byte[length];
        dataInputStream.readFully(in);  //read json string
        String json = new String(in,"utf-8");
        
        long now = System.currentTimeMillis();
        dataOutputStream.writeByte(0x09); //size of packet
        dataOutputStream.writeByte(0x01); //0x01 for ping
        dataOutputStream.writeLong(now); //time!?

        long startTime=System.nanoTime();   //获取开始时间
        readVarInt(dataInputStream);
        long endTime=System.nanoTime();   //获取开始时间
        id = readVarInt(dataInputStream);
        if (id == -1) {
            throw new IOException("Premature end of stream.");
        }
        
        if (id != 0x01) {
            throw new IOException("Invalid packetID");
        }
        long pingTime = dataInputStream.readLong(); //read response


        StatusResponse response = gson.fromJson(json, StatusResponse.class);
        SfLog.getInstance().d(this.getClass(), "得到服务器信息：" + json);
        response.setTime((now - pingTime));
        response.setDelay(((float) (endTime - startTime) / (1000 * 1000)));
        
        dataOutputStream.close();
        outputStream.close();
        inputStreamReader.close();
        inputStream.close();
        socket.close();
        
        return response;
    }
    
    
    public class StatusResponse {
        private Description description;//玩家名单
        private Players players;        //玩家信息
        private Version version;        //服务器版本
        private float delay = 0;
        private long time;

        public Description getDescription() {
            return description;
        }

        public Players getPlayers() {
            return players;
        }

        public Version getVersion() {
            return version;
        }

        public float getDelay() {
            return delay;
        }

        public void setDelay(float delay) {
            this.delay = delay;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }
    }

    public class Description{
        private ArrayList<Extra> extra = new ArrayList<>();
        private String text;

        public ArrayList<Extra> getExtra() {
            return extra;
        }

        public String getText() {
            return text;
        }
    }

    public class Extra{
        private String text = "";

        public String getText() {
            return text;
        }
    }
    
    public class Players {
        private int max = 0;
        private int online = 0;
        private List<Player> sample = new ArrayList<>();

        public int getMax() {
            return max;
        }

        public int getOnline() {
            return online;
        }

        public List<Player> getSample() {
            return sample;
        }        
    }
    
    public class Player {
        private String name = "";
        private String id = "";

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }
        
    }
    
    public class Version {
        private String name = "";
        private String protocol = "";

        public String getName() {
            return name;
        }

        public String getProtocol() {
            return protocol;
        }
    }
}