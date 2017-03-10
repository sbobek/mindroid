package geist.re.mindlib.hardware;

/**
 * Created by sbk on 09.03.17.
 */

public class UltrasonicSensor extends Sensor{
    public enum Type{
        /**
         * There is only one possibility
         */
        LOWSPEED_9V((byte)0x0B);

        private byte val;
        Type(byte val){
            this.val = val;
        }
    }

    public UltrasonicSensor(Port port, Mode mode, Type type) {
        super(port.getRaw(), mode.getRaw(), type.val);
    }
}
