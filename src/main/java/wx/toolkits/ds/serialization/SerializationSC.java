package wx.toolkits.ds.serialization;

import lombok.Data;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by apple on 16/5/30.
 */
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SerializationSC implements Serializable{

    private static final long serialVersionUID = -1874850715617681161L;
    private int type;
    private String name;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public SerializationSC(int type, String name) {
        super();
        this.type = type;
        this.name = name;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        // TODO Auto-generated method stub
        //serialize object SerializationSC
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        SerializationSC SerializationSC = new SerializationSC(1, "charlie");
        oos.writeObject(SerializationSC);

        //输出内容
        System.out.println("序列化后的内容:"+ new String(bos.toByteArray()));

        //deserialize object, get new object newSerializationSC
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bis);
        SerializationSC newSerializationSC = (SerializationSC) ois.readObject();

        System.out.println(newSerializationSC.getType()+":"+newSerializationSC.getName());
    }
}
