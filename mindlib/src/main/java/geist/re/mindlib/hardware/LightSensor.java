package geist.re.mindlib.hardware;

/**
 * Created by sbk on 09.03.17.
 */

public class LightSensor extends Sensor{
    public enum Type{
        /**
         * Led on a sensor is on
         */
        LIGHT_ACTIVE((byte)0x05),
        /**
         * Led on a sensor is off
         */
        LIGHT_INCTIVE((byte)0x06);

        private byte val;
        Type(byte val){
            this.val = val;
        }
    }



    public LightSensor(Port port, Mode mode, Type type) {
        super(port.getRaw(), mode.getRaw(), type.val);
    }



}
