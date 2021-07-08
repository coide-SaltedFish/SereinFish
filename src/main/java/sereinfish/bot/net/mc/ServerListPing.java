package sereinfish.bot.net.mc;
 
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.file.NetHandle;
import sereinfish.bot.file.image.ImageHandle;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.ui.tray.AppTray;
import sun.font.FontDesignMetrics;
import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
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
        //SfLog.getInstance().d(this.getClass(), "得到服务器信息：" + json);
        response.setTime((now - pingTime));
        response.setDelay(((float) (endTime - startTime) / (1000 * 1000)) / 2);
        
        dataOutputStream.close();
        outputStream.close();
        inputStreamReader.close();
        inputStream.close();
        socket.close();

        response.favicon = response.favicon.substring(0, response.favicon.length() - 1);//截掉后面的=
        return response;
    }

    /**
     * 生成服务器状态图
     * @param statusResponse
     * @return
     */
    public static BufferedImage getServerInfoImage(StatusResponse statusResponse){
        Color defaultColor = Color.decode("#EEEEEE");//背景颜色
        Color shadeColor = new Color(0, 0, 0, 60);

        Font font = null;
        //字体
        try {

            font = Font.createFont(Font.TRUETYPE_FONT, new File(ServerListPing.class.getClassLoader().getResource(FileHandle.mcResDefaultFontFile).toURI()));
        } catch (Exception e) {
            SfLog.getInstance().e(ServerListPing.class, "默认字体加载失败", e);
            font = new Font("宋体", Font.PLAIN, 24);
        }

        //读取泥土材质
        int dirtWidth = 1920  / 12;
        BufferedImage dirtImage = null;
        try {
            dirtImage = ImageHandle.imageToBufferedImage(Toolkit.getDefaultToolkit().getImage(ServerListPing.class.getClassLoader().getResource(FileHandle.mcResDirtFile)));
        } catch (Exception e) {
            SfLog.getInstance().e(ServerListPing.class, "材质丢失：" + ServerListPing.class.getClassLoader().getResource(FileHandle.mcResDirtFile).getPath());
            dirtImage = new BufferedImage(dirtWidth, dirtWidth, BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D graphics2D = dirtImage.createGraphics();
            graphics2D.setBackground(defaultColor);
            graphics2D.clearRect(0, 0, dirtImage.getWidth(), dirtImage.getHeight());//通过使用当前绘图表面的背景色进行填充来清除指定的矩形
            graphics2D.dispose();
        }

        //计算宽高
        int width = 1920;
        //服务器信息235高，服务器图标203高
        //延迟图标在y25，x-20
        //名字x262，y24
        //描述x237，y93
        //人数：-100，相对延迟图标20

        //玩家信息一条90 间隔10

        //tps信息高125
        int height = 235 + 10 + (10 + 90) * statusResponse.players.online + 125;
        //生成底图
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        //绘制底色
        for(int j = 0; j < height; j += dirtWidth){
            for (int i = 0; i < width; i += dirtWidth){
                graphics2D.drawImage(dirtImage, i, j, dirtWidth, dirtWidth, null);
            }
        }
        //调整底色颜色
        for(int j = 0; j < bufferedImage.getHeight(); j++){
            for (int i = 0; i < bufferedImage.getWidth(); i++){
                int rgb = bufferedImage.getRGB(i, j);

                int black = 70;

                int red = ((rgb >> 16) & 0xff) - black - 30;
                int green = ((rgb >> 8) & 0xff) - black - 20;
                int blue = (rgb & 0xff) - black;

                if (red < 0) red = 0;
                if (green < 0) green = 0;
                if (blue < 0) blue = 0;

                bufferedImage.setRGB(i, j, new Color(red, green, blue).getRGB());
            }
        }

        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);//抗锯齿
        //绘制服务器头像
        BufferedImage serverHeadImage = new BufferedImage(203, 203, BufferedImage.TYPE_4BYTE_ABGR);
        try {
            BufferedImage bi1 = ImageHandle.base64ToImage(statusResponse.favicon);
            Graphics2D GSI = serverHeadImage.createGraphics();
            GSI.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);//抗锯齿
            GSI.drawImage(bi1, 0, 0, 203, 203, null);
            GSI.dispose();
        } catch (IOException e) {
            Graphics2D GSI = serverHeadImage.createGraphics();
            GSI.setBackground(defaultColor);
            GSI.clearRect(0, 0, dirtImage.getWidth(), dirtImage.getHeight());//通过使用当前绘图表面的背景色进行填充来清除指定的矩形
            GSI.dispose();
        }
        graphics2D.drawImage(serverHeadImage, 16, 16, serverHeadImage.getWidth(), serverHeadImage.getHeight(), null);//服务器头像
        //绘制服务器名字
        graphics2D.setPaint(Color.WHITE);
        font = font.deriveFont(Font.PLAIN, 72f);
        graphics2D.setFont(font);
        FontDesignMetrics metrics = FontDesignMetrics.getMetrics(font);
        int serverNameHeight = metrics.getAscent();
        graphics2D.drawString(statusResponse.getVersion().getName(), 262, 16 + serverNameHeight);//服务器名称

        //绘制延迟
        font = font.deriveFont(Font.PLAIN, 52f);
        graphics2D.setFont(font);
        if(statusResponse.getDelay() >= 120){
            graphics2D.setPaint(Color.RED);
        }else if (statusResponse.getDelay() >= 80){
            graphics2D.setPaint(Color.ORANGE);
        }else {
            graphics2D.setPaint(Color.GREEN);
        }

        String msStr = ((int) statusResponse.getDelay()) + "ms";//延迟文本
        int msStrLen = metrics.stringWidth(msStr);
        graphics2D.drawString(msStr, bufferedImage.getWidth() - 32 - msStrLen, 16 + metrics.getAscent() + (metrics.getAscent() - serverNameHeight));//绘制延迟

        //绘制人数
        graphics2D.setPaint(Color.WHITE);
        String playerNumStr = statusResponse.getPlayers().getOnline() + "/" + statusResponse.getPlayers().getMax();//人数文本
        graphics2D.drawString(playerNumStr, bufferedImage.getWidth() - 32 - msStrLen - metrics.stringWidth(playerNumStr) - 10, 16 + metrics.getAscent() + (metrics.getAscent() - serverNameHeight));//绘制人数
        //TODO:绘制服务器描述
        //绘制玩家列表
        graphics2D.setPaint(Color.WHITE);
        font = font.deriveFont(Font.BOLD, 62f);
        metrics = FontDesignMetrics.getMetrics(font);
        graphics2D.setFont(font);
        int startY = 235 + 10;
        int startX = 32;
        for(Player player:statusResponse.getPlayers().getSample()){
            BufferedImage playerHeadImage = null;
            try {
                playerHeadImage = NetHandle.getMcPlayerHeadImage(player.id, 90);
            } catch (IOException e) {
                SfLog.getInstance().e(ServerListPing.class, e);
            }
            graphics2D.drawImage(playerHeadImage, startX, startY, null);//绘制头像
            graphics2D.drawString(player.name, startX + 90 + 10, startY + (90 - metrics.getAscent() / 2));

            startY += 100;
        }

        graphics2D.dispose();
        return bufferedImage;
    }
    
    
    public class StatusResponse {
        private Description description;//玩家名单
        private Players players;        //玩家信息
        private Version version;        //服务器版本
        private String favicon;         //服务器头像
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