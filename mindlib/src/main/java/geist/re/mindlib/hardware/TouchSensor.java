package geist.re.mindlib.hardware;

/**
 * Created by sbk on 09.03.17.
 */

public class TouchSensor extends Sensor{
    public enum Type{
        /**
         * There is only one possibility
         */
        SWITCH((byte)0x01);

        private byte val;
        Type(byte val){
            this.val = val;
        }
    }

    public TouchSensor(Port port, Mode mode, Type type) {
        super(port.getRaw(), mode.getRaw(), type.val);
    }
}
