package sereinfish.bot.entity.mc;

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
}
