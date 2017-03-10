package geist.re.mindlib.hardware;

/**
 * Created by sbk on 09.03.17.
 */

public class SoundSensor extends Sensor{
    public enum Type{
        /**
         * Sound in DB units
         */
        SOUND_DB((byte)0x07),
        /**
         * Sound in DBA units
         */
        SOUND_DBA((byte)0x08);

        private byte val;
        Type(byte val){
            this.val = val;
        }

        public byte getRaw() {
            return val;
        }
    }

    public SoundSensor(Port port, Mode mode, Type type) {
        super(port.getRaw(), mode.getRaw(), type.getRaw());
    }
}
