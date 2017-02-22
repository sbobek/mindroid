package geist.re.mindlib.tasks;

import android.util.Log;

import geist.re.mindlib.RobotService;
import geist.re.mindlib.hardware.Motor;

/**
 * Created by sbk on 21.02.17.
 */

public class MotorStateQueryTask extends RobotQueryTask {
    public static final int IDX_MOTOR_PORT = 4;
    byte [] query = {0x03,0x00,(byte)0x00,0x06,0x00};

    public MotorStateQueryTask(Motor motor){
        query[IDX_MOTOR_PORT] = motor.getPort();
    }

    @Override
    public byte[] getRawQuery() {
        return query;
    }

    @Override
    public void execute(RobotService rs) {
        Log.d(TAG, "Executing robot motor state query");
        rs.writeToNXTSocket(getRawQuery());
    }
}
