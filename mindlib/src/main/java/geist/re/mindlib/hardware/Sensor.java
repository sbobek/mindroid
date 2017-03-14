package geist.re.mindlib.hardware;

import java.util.Timer;
import java.util.TimerTask;

import geist.re.mindlib.RobotService;
import geist.re.mindlib.events.Event;
import geist.re.mindlib.listeners.RobotListener;
import geist.re.mindlib.tasks.SensorStateQueryTask;

/**
 * Created by sbk on 10.03.17.
 */

public abstract class Sensor {
    protected static final String TAG = "Motor";

    protected final RobotService owner;

    private byte port;
    private byte mode;
    private byte type;

    protected Timer stateQueryTimer;
    protected Event currentStateUpdate;
    protected RobotListener stateListener;

    public enum Port {
        ONE((byte)0x01),
        TWO((byte)0x02),
        THREE((byte)0x03),
        FOUR((byte)0x04);

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




    public Sensor(RobotService owner, byte port, byte mode, byte type) {
        this.owner = owner;
        this.port = port;
        this.mode = mode;
        this.type = type;
        stateQueryTimer = new Timer();
    }

    public Port getPort(){
        return Port.valueOf(getRawType());
    }

    public Mode getMode(){
        return Mode.valueOf(getRawMode());
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

    protected synchronized void registerListener(final Sensor s, RobotListener rsl, long rate){
        if(stateListener != null){
             stateQueryTimer.cancel();
             stateQueryTimer = new Timer();
        }
        currentStateUpdate = null;
        stateListener =rsl;
         stateQueryTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                owner.addToQueryQueue(new SensorStateQueryTask(s));
            }
        },0,rate);

    }

    public synchronized void unregisterListener(){
        stateListener = null;
        stateQueryTimer.cancel();
    }

    public abstract void pushSensorStateEvent(Event event);
}
