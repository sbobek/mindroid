package geist.re.mindlib.hardware;

import java.util.Timer;
import java.util.TimerTask;

import geist.re.mindlib.RobotService;
import geist.re.mindlib.listeners.MotorStateListener;
import geist.re.mindlib.tasks.MotorTask;

/**
 * Created by sbk on 09.02.17.
 */

public class Motor {
    public static final byte A = 0x00;
    public static final byte B = 0x01;
    public static final byte C = 0x02;
    private final RobotService owner;


    byte port;

    private MotorStateListener motorStateListener;
    private Timer motorStateQueryTimer;

    public Motor(byte port, RobotService owner){
        this.port = port;
        this.owner = owner;
        motorStateQueryTimer = new Timer();
    }

    public byte getPort(){
        return port;
    }

    public MotorTask run(int speed){
        MotorTask rmt = new MotorTask(this);
        rmt.setPowerSetPoint((byte)speed);
        return rmt;
    }


    public MotorTask stop(){
        MotorTask rmt = new MotorTask(this);
        rmt.setPowerSetPoint((byte)0);
        return rmt;
    }

    public void registerMotorStateListener(MotorStateListener msl, long rate){
        if(motorStateListener != null){
            motorStateQueryTimer.cancel();
        }
        motorStateListener=msl;
        motorStateQueryTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                //write to socket queryabout the state
            }
        },rate);

    }
}
