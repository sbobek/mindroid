package geist.re.mindlib.utils;

import java.nio.ByteBuffer;
import java.util.Collections;

/**
 * Created by sbk on 22.02.17.
 */

public class BluetoothProtocolUtils {
    public static final int INT_SIZE  = 4;

    public static int littleEndianToInteger(byte[] data, int startIdx){
        int result = 0;
        for(int i = startIdx+INT_SIZE-1; i >=0; i--){
            //TODO: negative integers?
            result += data[i];
        }
        return result;
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

    public static byte [] integerToLittleendian(int data, byte [] destination, int startIdx){
        byte [] source = BluetoothProtocolUtils.integerToLittleEndian(data);
        for(int d = startIdx,s =0; d < startIdx+INT_SIZE;d++,s++){
            destination[d] = source[s];
        }
        return destination;
    }
}
