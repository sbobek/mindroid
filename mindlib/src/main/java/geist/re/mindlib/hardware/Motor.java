package geist.re.mindlib.hardware;

import geist.re.mindlib.RobotService;
import geist.re.mindlib.tasks.RobotMotorTask;

/**
 * Created by sbk on 09.02.17.
 */

public class Motor {
    public static final byte A = 0x00;
    public static final byte B = 0x01;
    public static final byte C = 0x02;


    byte port;

    public Motor(byte port){
        this.port = port;
    }

    public byte getPort(){
        return port;
    }

    public RobotMotorTask run(int speed){
        RobotMotorTask rmt = new RobotMotorTask(this);
        rmt.setPowerSetPoint((byte)speed);
        return rmt;
    }


    public RobotMotorTask stop(){
        RobotMotorTask rmt = new RobotMotorTask(this);
        rmt.setPowerSetPoint((byte)0);
        rmt.enableRegulationMode();
        rmt.enableMotorOnMode();
        rmt.enableSpeedRegulation();
        rmt.setRunStateRunning();
        return rmt;
    }
}
