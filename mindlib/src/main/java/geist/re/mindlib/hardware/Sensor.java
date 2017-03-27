package geist.re.mindlib.hardware;

import java.util.Timer;
import java.util.TimerTask;

import geist.re.mindlib.RobotService;
import geist.re.mindlib.events.Event;
import geist.re.mindlib.exceptions.SensorDisconnectedException;
import geist.re.mindlib.listeners.RobotListener;
import geist.re.mindlib.tasks.ConnectSensorTask;
import geist.re.mindlib.tasks.SensorStateQueryTask;

/**
 * Created by sbk on 10.03.17.
 */

public abstract class Sensor {
    protected static final String TAG = "Sensor";

    protected final RobotService owner;

    protected byte port;
    protected byte mode;
    protected byte type;

    protected Timer stateQueryTimer;
    protected Event currentStateUpdate;
    protected RobotListener stateListener;

    public enum Type{
        /**
         * There is only one possibility for touch sensor
         */
        SWITCH((byte)0x01),
        /**
         * Led on a sensor is on
         */
        LIGHT_ACTIVE((byte)0x05),
        /**
         * Led on a sensor is off
         */
        LIGHT_INCTIVE((byte)0x06),
        /**
         * Sound in DB units
         */
        SOUND_DB((byte)0x07),
        /**
         * Sound in DBA units
         */
        SOUND_DBA((byte)0x08),
        /**
         * There is only one possibility for ultrasonic sensor
         */
        LOWSPEED_9V((byte)0x0B);

        private byte val;
        Type(byte val){
            this.val = val;
        }

        public static Type valueOf(byte raw){
            for(Type t : Type.values()){
                if(t.getRaw() == raw){
                    return t;
                }
            }
            return null;
        }


        public byte getRaw() {
            return val;
        }
    }


    public enum Port {
        ONE((byte)0x00),
        TWO((byte)0x01),
        THREE((byte)0x02),
        FOUR((byte)0x03),
        DISCONNECTED((byte)0x05);

        private byte val;

        public static Port valueOf(byte raw){
            for(Port p : Port.values()){
                if(p.getRaw() == raw){
                    return p;
                }
            }
            return null;
        }

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

        public static Mode valueOf(byte raw){
            for(Mode v : Mode.values()){
                if(v.getRaw() == raw){
                    return v;
                }
            }
            return null;
        }

        public byte getRaw(){
            return val;
        }
    }


    public Sensor(RobotService owner){
        this.owner = owner;
        this.port = Port.DISCONNECTED.getRaw();
        stateQueryTimer = new Timer();

    }


    public Sensor(RobotService owner, byte port, byte mode, byte type) {
        this.owner = owner;
        this.port = port;
        this.mode = mode;
        this.type = type;
        stateQueryTimer = new Timer();
    }

    public synchronized void connect(Port p, Mode m, Type t){
        this.port = p.getRaw();
        this.mode = m.getRaw();
        this.type = t.getRaw();
        owner.addToQueryQueue(new ConnectSensorTask(this));
    }

    public synchronized void disconnect(){
        unregisterListener();
        this.port = Port.DISCONNECTED.getRaw();
    }

    public Port getPort(){
        return Port.valueOf(getRawPort());
    }

    public Mode getMode(){
        return Mode.valueOf(getRawMode());
    }

    public Type getType() {
        return Type.valueOf(getRawType());
    }

    public byte getRawPort() {
        return port;
    }

    public byte getRawMode() {
        return mode;
    }

    public byte getRawType() {
        return type;
    }

    protected synchronized void registerListener(final Sensor s, RobotListener rsl, long rate) throws SensorDisconnectedException {
        if(s.getPort() == Port.DISCONNECTED){
            throw new SensorDisconnectedException("Cannot register listener to disconnected senor. Connect sensor first.");
        }
        if(stateListener != null){
             stateQueryTimer.cancel();
             stateQueryTimer.purge();
        }
        stateQueryTimer = new Timer();
        currentStateUpdate = null;
        stateListener =rsl;
         stateQueryTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                owner.addToQueryQueue(new SensorStateQueryTask(Sensor.this));
            }
        },0,rate);

    }

    public synchronized void unregisterListener(){
        stateListener = null;
        stateQueryTimer.cancel();
    }

    public abstract void pushSensorStateEvent(Event event);
}
