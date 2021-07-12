package sereinfish.bot.net.mc;
 
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import sereinfish.bot.entity.mc.JsonColor;
import sereinfish.bot.file.FileHandle;
import sereinfish.bot.file.NetHandle;
import sereinfish.bot.file.image.ImageHandle;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.net.mc.rcon.Rcon;
import sereinfish.bot.net.mc.rcon.RconConf;
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

        if (response != null){
            if (response.favicon != null && !response.favicon.equals("")){
                response.favicon = response.favicon.substring(0, response.favicon.length() - 1);//截掉后面的=
            }else {
                response.favicon = "";
            }
        }
        return response;
    }

    /**
     * 生成服务器状态图
     * @param statusResponse
     * @return
     */
    public static BufferedImage getServerInfoImage(StatusResponse statusResponse, Rcon rcon) throws Exception{
        try {
            Color defaultColor = Color.decode("#EEEEEE");//背景颜色
            Color shadeColor = new Color(0, 0, 0, 60);

            int serverInfoH = 235;//服务器信息235高，服务器图标203高
            int serverHeadH = 203;

            int barWidth = 8;//服务器信息边框宽度

            //计算宽高
            int width = 1920;

            //延迟图标在y25，x-20
            //名字x262，y24
            //描述x237，y93
            //人数：-100，相对延迟图标20

            //玩家信息一条90 间隔10

            //tps信息高125
            int height = 235 //服务器信息
                    + 10 + (10 + 90) * statusResponse.players.online //玩家信息
                    + 27 //边框高度
                    ;
            int tpsHeight = 126;
            if (rcon != null){
                height += + tpsHeight; //tps,mpts
            }

            int windowWidth = width - 18;

            Font font = null;
            //字体
            try {
                font = Font.createFont(Font.TRUETYPE_FONT, ServerListPing.class.getClassLoader().getResourceAsStream(FileHandle.mcResDefaultFontFile));
            } catch (Exception e) {
                SfLog.getInstance().e(ServerListPing.class, "默认字体加载失败：" + ServerListPing.class.getClassLoader().getResource(FileHandle.mcResDefaultFontFile), e);
                font = new Font("宋体", Font.PLAIN, 24);
            }

            //读取背景材质
            int dirtWidth = 1920  / 12;
            BufferedImage dirtImage = null;
            try {
                dirtImage = ImageHandle.imageToBufferedImage(Toolkit.getDefaultToolkit().getImage(ServerListPing.class.getClassLoader().getResource(FileHandle.mcResOptionsBackgroundFile)));
                BufferedImage initImage = ImageHandle.imageToBufferedImage(Toolkit.getDefaultToolkit().getImage(ServerListPing.class.getClassLoader().getResource(FileHandle.mcResInitFile)));
                Graphics2D graphics2D = dirtImage.createGraphics();
                graphics2D.drawImage(initImage, 0, 0, dirtImage.getWidth(), dirtImage.getHeight(), null);
                graphics2D.dispose();
            } catch (Exception e) {
                SfLog.getInstance().e(ServerListPing.class, "材质错误：" + ServerListPing.class.getClassLoader().getResource(FileHandle.mcResOptionsBackgroundFile).getPath());
                dirtImage = new BufferedImage(dirtWidth, dirtWidth, BufferedImage.TYPE_4BYTE_ABGR);
                Graphics2D graphics2D = dirtImage.createGraphics();
                graphics2D.setBackground(defaultColor);
                graphics2D.clearRect(0, 0, dirtImage.getWidth(), dirtImage.getHeight());//通过使用当前绘图表面的背景色进行填充来清除指定的矩形
                graphics2D.dispose();
            }

            //读取边框材质
            BufferedImage windowImage = null;
            try {
                windowImage = ImageHandle.picturesStretch9(ImageHandle.imageToBufferedImage(Toolkit.getDefaultToolkit().getImage(ServerListPing.class.getClassLoader().getResource(FileHandle.mcResServerStateFrameImageFile))), width, height);
            } catch (Exception e) {
                SfLog.getInstance().e(ServerListPing.class, "材质错误：" + ServerListPing.class.getClassLoader().getResource(FileHandle.mcResServerStateFrameImageFile).getPath());
                windowImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
                Graphics2D graphics2D = windowImage.createGraphics();
                graphics2D.setBackground(defaultColor);
                graphics2D.clearRect(0, 0, windowImage.getWidth(), windowImage.getHeight());//通过使用当前绘图表面的背景色进行填充来清除指定的矩形
                graphics2D.dispose();
            }

            int startY = 18;//开始Y轴坐标
            int startX = 9;//边框X轴占用宽度

            //生成底图
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D graphics2D = bufferedImage.createGraphics();

            for(int j = 0; j < height; j += dirtWidth){
                for (int i = 0; i < width; i += dirtWidth){
                    graphics2D.drawImage(dirtImage, i, j, dirtWidth, dirtWidth, null);
                }
            }
            //绘制边框
            graphics2D.drawImage(windowImage,0, 0, windowImage.getWidth(), windowImage.getHeight(), null);

            //graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);//抗锯齿
            //绘制服务器头像
            int sHeadStartX = startX + 16;
            int sHeadStartY = startY + 16;

            //绘制服务器信息bar
            graphics2D.setPaint(Color.BLACK);
            graphics2D.fillRect(startX + 4, startY + 4, windowWidth - 8, serverInfoH - 8);
            graphics2D.setStroke(new BasicStroke(6));
            graphics2D.setPaint(Color.lightGray);
            graphics2D.drawRect(startX + 4, startY + 4, windowWidth - 8, serverInfoH - 8);

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
            graphics2D.drawImage(serverHeadImage, sHeadStartX, sHeadStartY, serverHeadImage.getWidth(), serverHeadImage.getHeight(), null);//服务器头像
            //绘制服务器名字
            graphics2D.setPaint(Color.WHITE);
            font = font.deriveFont(Font.PLAIN, 62f);
            graphics2D.setFont(font);
            FontDesignMetrics metrics = FontDesignMetrics.getMetrics(font);
            int serverNameHeight = metrics.getAscent();
            graphics2D.drawString(statusResponse.getVersion().getName(), startX + 262, startY + serverNameHeight);//服务器名称

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
            int delayStartX = bufferedImage.getWidth() - startX - msStrLen;
            int delayStartY = startY + metrics.getAscent() + (metrics.getAscent() - serverNameHeight);
            graphics2D.drawString(msStr, delayStartX, delayStartY);//绘制延迟

            //绘制人数
            graphics2D.setPaint(Color.WHITE);
            String playerNumStr = statusResponse.getPlayers().getOnline() + "/" + statusResponse.getPlayers().getMax();//人数文本
            int playerNumStartX = delayStartX - metrics.stringWidth(playerNumStr);
            int playerNumStartY = delayStartY;
            graphics2D.drawString(playerNumStr, playerNumStartX, playerNumStartY);//绘制人数
            //绘制服务器描述
            int descriptionStartX = startX + 262;
            int descriptionStartY = startY + serverNameHeight + 14;
            graphics2D.setPaint(Color.WHITE);
            font = font.deriveFont(52f);//设置字体大小
            graphics2D.setFont(font);
            metrics = FontDesignMetrics.getMetrics(font);
            graphics2D.drawString(statusResponse.getDescription().getText(), descriptionStartX, descriptionStartY);
            descriptionStartX += metrics.stringWidth(statusResponse.getDescription().getText());
            for (Extra extra:statusResponse.getDescription().getExtra()){
                graphics2D.setPaint(extra.getColor());//设置颜色
                //设置字体样式
                if (extra.getBold()){
                   font = font.deriveFont(Font.BOLD);
                }else {
                   font = font.deriveFont(Font.PLAIN);
                }
                graphics2D.setFont(font);
                metrics = FontDesignMetrics.getMetrics(font);
                //绘制
                if (extra.getText().contains("\n")){
                    descriptionStartX = startX + 262;
                    descriptionStartY += metrics.getAscent() + 14;
                }
                graphics2D.drawString(extra.getText(), descriptionStartX, descriptionStartY + metrics.getAscent());
                //坐标计算
                descriptionStartX += metrics.stringWidth(extra.getText());
            }

            //绘制玩家列表
            int playerListBGStartX = startX + 4;//列表背景
            int playerListBGStartY = startY + 235 + 4;
            int playerListBGWidth = bufferedImage.getWidth() - playerListBGStartX * 2;
            int playerListBGHeight = bufferedImage.getHeight() - playerListBGStartY - 9 - 8;

            graphics2D.setPaint(JsonColor.getColor("black"));
            graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.4f));
            graphics2D.fillRect(playerListBGStartX, playerListBGStartY, playerListBGWidth, playerListBGHeight);
            graphics2D.setPaint(Color.WHITE);

            graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1f));

            int playerListStartY = playerListBGStartY + 10;
            int playerListStartX = playerListBGStartX + 10;

            graphics2D.setPaint(Color.WHITE);
            font = font.deriveFont(Font.BOLD, 62f);
            metrics = FontDesignMetrics.getMetrics(font);
            graphics2D.setFont(font);
            if (statusResponse.getPlayers() != null && statusResponse.getPlayers().getSample() != null){
                for(Player player:statusResponse.getPlayers().getSample()){
                    //绘制玩家item背景
                    graphics2D.setPaint(JsonColor.getColor("dark_gray"));
                    graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.3f));
                    graphics2D.fillRect(playerListStartX, playerListStartY, width - playerListStartX * 2, 90);
                    graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1f));
                    graphics2D.setPaint(Color.WHITE);
                    BufferedImage playerHeadImage = null;
                    try {
                        playerHeadImage = NetHandle.getMcPlayerHeadImage(player.id, 90);
                    } catch (IOException e) {
                        SfLog.getInstance().e(ServerListPing.class, e);
                    }
                    graphics2D.drawImage(playerHeadImage, playerListStartX, playerListStartY, null);//绘制头像
                    graphics2D.drawString(player.name, playerListStartX + 90 + 10, playerListStartY + (90 - metrics.getAscent() / 2));

                    playerListStartY += 100;
                }
            }
            //绘制tps和mspt
            int tpsStartX = playerListStartX;
            int tpsStartY = playerListStartY + 10 + (90 - metrics.getAscent() / 2);

            String tps = "";
            String mspt = "";
            if (rcon != null){
                try{
                    tps = rcon.cmd("tps").split(":")[1];
                }catch (Exception e){
                    tps = "未知";
                }
                try {
                    mspt = rcon.cmd("mspt").split(":")[1].split(",")[0].split("/")[2];
                }catch (Exception e){
                    mspt = "未知";
                }
            }
            String info = "§fTPS:" + tps + "   §fMSPT:" + mspt;
            //绘制
            graphics2D.setPaint(Color.WHITE);
            for (int i = 0; i < info.toCharArray().length; i++){
                char ch = info.charAt(i);
                if (ch == '§'){
                    i++;
                    graphics2D.setPaint(JsonColor.getColor(new String(new char[]{ch, info.charAt(i)})));
                }else {
                    metrics = FontDesignMetrics.getMetrics(font);
                    graphics2D.drawString(new String(new char[]{ch}), tpsStartX, tpsStartY);
                    tpsStartX += metrics.stringWidth(new String(new char[]{ch}));
                }
            }

            graphics2D.dispose();
            return bufferedImage;
        }catch (Exception e){
            throw e;
        }
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
        private boolean bold;
        private String text = "";
        private String color = "";

        public boolean getBold(){
            return bold;
        }
        public String getText() {
            return text;
        }

        public Color getColor(){
            //如果是哈希值颜色
            if (color != null){
                if (color.startsWith("#")){
                    return Color.decode(color);
                }
                return JsonColor.getColor(color);
            }
            return Color.WHITE;
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