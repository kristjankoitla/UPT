import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

public class Utilities {
    static Random r = new Random();

    static double tanH(double in){
        return(Math.tanh(in));
    }

    static double logistics(double in){
        return(1/(1+Math.exp(-in)));
    }

    public static int minIndex(int[] in){
        int min = in[0];
        int index = 0;

        for(int i = 0; i < in.length; i++)
        {
            if(min > in[i])
            {
                min = in[i];
                index=i;
            }
        }
        return index;
    }

    public static Object deepClone(Object object) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static double RNGDouble(double min, double max){
        double randomValue = min + (max - min) * r.nextDouble();
        return randomValue;
    }

}
