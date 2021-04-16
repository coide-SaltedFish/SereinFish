package sereinfish.bot.entity.lolicon.sf;

import java.util.ArrayList;

public class Response {
    private int code;
    private String msg;
    private int count;
    private ArrayList<Setu> list = new ArrayList<>();

    public Response(int code, String msg, int count, ArrayList<Setu> list) {
        this.code = code;
        this.msg = msg;
        this.count = count;
        this.list = list;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<Setu> getList() {
        return list;
    }

    public void setList(ArrayList<Setu> list) {
        this.list = list;
    }

    public static class Setu{
        String setu;
        boolean isR18;
        String md5;

        public Setu(String setu, boolean isR18, String md5) {
            this.setu = setu;
            this.isR18 = isR18;
            this.md5 = md5;
        }

        public String getSetu() {
            return setu;
        }

        public void setSetu(String setu) {
            this.setu = setu;
        }

        public boolean isR18() {
            return isR18;
        }

        public void setR18(boolean r18) {
            isR18 = r18;
        }

        public String getMd5() {
            md5 = md5.replace("-","").toUpperCase();
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }
    }
}
