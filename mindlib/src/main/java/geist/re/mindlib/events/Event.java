package geist.re.mindlib.events;

/**
 * Created by sbk on 17.02.17.
 */

public abstract class Event {
    public static final int IDX_TELEGRAM_TYPE=2;
    public static final int IDX_RESPONSE_TYPE=3;
    public static final int IDX_STATUS = 4;
    public static final int IDX_INPUT_PORT =5;


    public static final byte TELEGRAM_RESPONSE = 0x02;
    public static final byte RESPONSE_TYPE_GETOUTPUTSTATE = 0x06;
    public static final byte RESPONSE_TYPE_GETINPUTVALUES = 0x07;

    protected byte []  data;
    protected int responseType;

    public Event(byte [] rawData){
        data = rawData;
        responseType = rawData[IDX_RESPONSE_TYPE];
    }

    public int getResponseType(){
        return responseType;
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
