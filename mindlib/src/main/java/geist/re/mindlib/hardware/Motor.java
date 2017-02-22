package geist.re.mindlib.hardware;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import geist.re.mindlib.RobotService;
import geist.re.mindlib.events.Event;
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

    public static final byte VAL_CMD_TYPE = 0x04;

    public static final byte VAL_MODE_MOTORON = 0x01;
    public static final byte VAL_MODE_USE_BREAKES = 0x02;
    public static final byte VAL_MODE_ENABLE_REGULATION = 0x04;

    public static final byte VAL_REGULATION_IDLE = 0x00;
    public static final byte VAL_REGULATION_POWER = 0x01;
    public static final byte VAL_REGULATION_SYNC = 0x02;

    public static final byte VAL_RUN_STATE_IDLE = 0x00;
    public static final byte VAL_RUN_STATE_RAMPPUP = 0x10;
    public static final byte VAL_RUN_STATE_RUNNING = 0x20;
    public static final byte VAL_RUN_STATE_RAMPDOWN = 0x40;

    public static final byte VAL_RESET_MESSAGE_LENGTH_LSB = 0x04;
    public static final byte VAL_RESET_MESSAGE_LENGTH_MSB = 0x00;


    public static final int STATE_STOPPED = 0;
    public static final int STATE_RUNNING = 1;
    private static final long MOTOR_STATE_UPDATE_RATE = 200;
    private static final String TAG = "Motor";

    private final RobotService owner;


    byte port;

    private Timer motorStateQueryTimer;
    private MotorStateEvent previousStateUpdate;
    private MotorStateEvent currentStateUpdate;
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
        setState(STATE_RUNNING);
        MotorTask rmt = new MotorTask(this);
        rmt.setPowerSetPoint((byte)speed);
        rmt.setTachoLimit(angle);
        registerMotorStateListener(motorStateListener,MOTOR_STATE_UPDATE_RATE);
        return rmt;
    }

    public MotorTask run(int speed){;
        return run(speed, 0);
    }


    public MotorTask stop(){
        MotorTask rmt = new MotorTask(this);
        rmt.setPowerSetPoint((byte)0);
        unregisterMotorStateListener();
        setState(STATE_STOPPED);
        return rmt;
    }

    public synchronized void registerMotorStateListener(MotorStateListener msl, long rate){
        if(motorStateListener != null){
            motorStateQueryTimer.cancel();
            motorStateQueryTimer = new Timer();
        }
        currentStateUpdate = null;
        previousStateUpdate = null;
        motorStateListener=msl;
        motorStateQueryTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                owner.addToQueryQueue(new MotorStateQueryTask(Motor.this));
            }
        },0,rate);

    }

    public synchronized void unregisterMotorStateListener(){
        motorStateListener = null;
        motorStateQueryTimer.cancel();
    }

    public synchronized void setState(int state){
        mState = state;
        Log.d(TAG,"Setting state to "+state);
    }

    public synchronized int getState(){
        return mState;
    }

    private MotorStateListener motorStateListener = new MotorStateListener() {
        @Override
        public void onEventOccurred(MotorStateEvent e) {
            if(previousStateUpdate != null && previousStateUpdate.getRotationCount() != 0){
                Log.d(TAG, "onStateChanged: "+previousStateUpdate.getRotationCount()+" vs "+currentStateUpdate.getRotationCount());
                if(previousStateUpdate.getRotationCount() == currentStateUpdate.getRotationCount()){
                    setState(STATE_STOPPED);
                }else{
                    setState(STATE_RUNNING);
                }
            }
        }
    };

    public synchronized void pushMotorStateEvent(Event event) {
        Log.d(TAG, "Pushing motor state event");
        if(motorStateListener == null) return;
        previousStateUpdate = currentStateUpdate;
        currentStateUpdate = (MotorStateEvent) event;
        motorStateListener.onEventOccurred(event);
    }
}
