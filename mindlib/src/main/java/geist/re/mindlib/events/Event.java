package geist.re.mindlib.events;

/**
 * Created by sbk on 17.02.17.
 */

public abstract class Event {
    public static final int IDX_TELEGRAM_TYPE=0;
    public static final int IDX_RESPONSE_TYPE=1;


    public static final byte TELEGRAM_RESPONSE = 0x02;
    public static final byte TYPE_GETOUTPUTSTATE = 0x06;

    protected byte []  data;
    protected int mType;


    public int getType(){
        return mType;
    }

    @Override
    public String toString() {
        String msg = "";
        if(data != null) {
            for (byte b : data) {
                msg += Byte.toString(b)+" : ";
            }
        }
        return msg;
    }

}
