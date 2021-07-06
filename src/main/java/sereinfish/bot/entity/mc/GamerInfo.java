package sereinfish.bot.entity.mc;

import com.icecreamqaq.yuq.error.SkipMe;
import lombok.Getter;
import sereinfish.bot.myYuq.MyYuQ;
import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.util.ArrayList;

public class GamerInfo {
    //    {
//        "id": "<profile identifier>",
//            "name": "<player name>",
//            "properties": [
//        {
//            "name": "textures",
//                "value": "<base64 string>",
//                "signature": "<base64 string; signed data using Yggdrasil's private key>" // Only provided if ?unsigned=false is appended to url
//        }
//    ]
//    }

    String id;
    String name;
    ArrayList<Properties> properties;

    public class Properties{
        String name;
        String value;
        String signature;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Properties> getProperties() {
        return properties;
    }

    public void setProperties(ArrayList<Properties> properties) {
        this.properties = properties;
    }

    /**
     * base64解密
     * @param base64
     * @return
     */
    public static Value getValue(String base64) throws IOException {
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] bytes = decoder.decodeBuffer(base64);
        return MyYuQ.toClass(new String(bytes), Value.class);
    }

    //value
    //{
    //  "timestamp" : 1625555547451,
    //  "profileId" : "5b4b5375022649d59eb8336c7eddb985",
    //  "profileName" : "Cold_Maple_",
    //  "textures" : {
    //    "SKIN" : {
    //      "url" : "http://textures.minecraft.net/texture/8358856a18432f539040e40aa4526cb9370eb9cdf116f7b72454914367ec3cb1",
    //      "metadata" : {
    //        "model" : "slim"
    //      }
    //    }
    //  }
    //}
    @Getter
    public class Value{
        long timestamp;//时间戳
        String profileId;//uuid
        String profileName;//玩家id
        Textures textures;
    }

    @Getter
    public class Textures{
        Skin SKIN;
    }

    @Getter
    public class Skin{
        String url;
        Metadata metadata;
    }

    @Getter
    public class Metadata{
        String model;
    }
}
