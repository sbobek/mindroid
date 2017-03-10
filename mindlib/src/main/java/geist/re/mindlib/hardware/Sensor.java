package geist.re.mindlib.hardware;

/**
 * Created by sbk on 10.03.17.
 */

public abstract class Sensor {
    private byte port;
    private byte mode;
    private byte type;

    public enum Port {
        ONE((byte)0x01),
        TWO((byte)0x02),
        THREE((byte)0x03),
        FOUR((byte)0x04);

        private byte val;

        Port(byte val){
            this.val = val;
        }

        public byte getRaw(){
            return val;
        }
    }

    public enum Mode{
        RAWMODE((byte)0x00),
        BOOLEANMODE((byte)0x20),
        TRABNSITIONCNTMODE((byte)0x40),
        PERIODCOUNTERMODE((byte)0x60),
        PCTFULLSCALEMODE((byte)0x80),
        CELSIUSMODE((byte)0xA0),
        FAHRENHEITMODE((byte)0xC0),
        ANGLESTEPSMODE((byte)0xE0),
        SLOPEMASK((byte)0x1F),
        MODEMASK((byte)0xE0);


        private byte val;


        Mode(byte val){
            this.val = val;
        }

        public byte getRaw(){
            return val;
        }
    }


    public Sensor(byte port, byte mode, byte type) {
        this.port = port;
        this.mode = mode;
        this.type = type;
    }

    public byte getPort() {
        return port;
    }

    public byte getMode() {
        return mode;
    }

    public byte getType() {
        return type;
    }
}
