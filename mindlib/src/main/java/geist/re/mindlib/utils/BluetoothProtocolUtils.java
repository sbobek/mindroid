package geist.re.mindlib.utils;

import android.util.Log;

import java.nio.ByteBuffer;
import java.util.Collections;

/**
 * Created by sbk on 22.02.17.
 */

public class BluetoothProtocolUtils {
    public static final int INT_SIZE  = 4;

    public static int littleEndianToInteger(byte[] data, int startIdx){
        byte [] flipped = new byte[INT_SIZE];
        for(int s = startIdx+INT_SIZE-1,d=0; s >=startIdx; s--,d++){
            flipped[d] =  data[s];
        }

        return  ByteBuffer.wrap(flipped).getInt();
    }
    public static int littleEndianToInteger(byte[] data){
        return littleEndianToInteger(data,0);
    }

    public static byte [] integerToLittleEndian(int data){
        byte[] bytes = ByteBuffer.allocate(INT_SIZE).putInt(data).array();
        byte [] result = new byte[bytes.length];
        for(int i = 0; i < result.length; i++){
            result[i] = bytes[bytes.length-i-1];
        }
        return result;
    }

    public static byte [] integerToLittleEndian(int data, byte [] destination, int startIdx){
        byte [] source = BluetoothProtocolUtils.integerToLittleEndian(data);
        for(int d = startIdx,s =0; d < startIdx+INT_SIZE;d++,s++){
            destination[d] = source[s];
        }
        return destination;
    }
}
