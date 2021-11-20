package sereinfish.bot.entity.sf.msg.code.entity;

public class Parameter {
    private String[] parameters;

    public Parameter(String[] paras){
        if (paras == null){
            paras = new String[]{};
        }

        this.parameters = paras;
    }

    public String getString(int i){
        return parameters[i];
    }

    public int getInt(int i){
        return Integer.decode(getString(i));
    }

    public Float getFloat(int i){
        return Float.valueOf(getString(i));
    }

    public Long getLong(int i){
        return Long.decode(getString(i));
    }

    public Double getDouble(int i){
        return Double.valueOf(getString(i));
    }

    public int size(){
        return parameters.length;
    }
}
