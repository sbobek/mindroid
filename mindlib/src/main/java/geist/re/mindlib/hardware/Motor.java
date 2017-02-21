package geist.re.mindlib.hardware;

import java.util.Timer;
import java.util.TimerTask;

import geist.re.mindlib.RobotService;
import geist.re.mindlib.events.MotorStateEvent;
import geist.re.mindlib.listeners.MotorStateListener;
import geist.re.mindlib.tasks.MotorStateQueryTask;
import geist.re.mindlib.tasks.MotorTask;

/**
 * Created by sbk on 09.02.17.
 */

public class Motor {
    public static final byte A = 0x00;
    public static final byte B = 0x01;
    public static final byte C = 0x02;
    private static final int STATE_STOPPED = 0;
    private static final int STATE_RUNNING = 1;
    private final RobotService owner;


    byte port;

    private Timer motorStateQueryTimer;
    private int mState;

    public Motor(byte port, RobotService owner){
        this.port = port;
        this.owner = owner;
        this.mState = STATE_STOPPED;
        motorStateQueryTimer = new Timer();
    }

    public byte getPort(){
        return port;
    }

    public MotorTask run(int speed, int angle){
        MotorTask rmt = new MotorTask(this);
        rmt.setPowerSetPoint((byte)speed);
        rmt.setTachoLimit(angle);
        return rmt;
    }

    public MotorTask run(int speed){
        MotorTask rmt = new MotorTask(this);
        rmt.setPowerSetPoint((byte)speed);
        registerMotorStateListener(motorStateListener,200);
        return rmt;
    }


    public MotorTask stop(){
        MotorTask rmt = new MotorTask(this);
        rmt.setPowerSetPoint((byte)0);
        unregisterMotorStateListener();
        setState(STATE_STOPPED);
        return rmt;
    }

    public void registerMotorStateListener(MotorStateListener msl, long rate){
        if(motorStateListener != null){
            motorStateQueryTimer.cancel();
        }
        motorStateListener=msl;
        motorStateQueryTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                owner.addToQueryQueue(new MotorStateQueryTask(Motor.this));
            }
        },0,rate);

    }

    public void unregisterMotorStateListener(){
        motorStateListener = null;
        motorStateQueryTimer.cancel();
    }

    public void setState(int state){
        mState = state;
    }

    private MotorStateListener motorStateListener = new MotorStateListener() {
        @Override
        public void onEventOccurred(MotorStateEvent e) {
                //set state
        }
    };
}
