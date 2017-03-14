package geist.re.mindlib.tasks;

import android.util.Log;

import geist.re.mindlib.RobotService;
import geist.re.mindlib.hardware.LightSensor;
import geist.re.mindlib.hardware.Motor;
import geist.re.mindlib.hardware.Sensor;

/**
 * Created by sbk on 09.03.17.
 */

public class ConnectSensorTask extends RobotQueryTask {

    public static final int IDX_SENSOR_PORT = 4;
    public static final int IDX_SENSOR_TYPE = 5;
    public static final int IDX_SENSOR_MODE = 6;


    byte [] query = {0x05,0x00,(byte)0x00,0x05,0x00,0x00,0x00};

    public ConnectSensorTask(Sensor s){
        query[IDX_SENSOR_PORT] = s.getRawPort();
        query[IDX_SENSOR_TYPE] = s.getRawType();
        query[IDX_SENSOR_MODE] = s.getRawMode();
    }

    @Override
    public byte[] getRawQuery() {
        return query;
    }

    @Override
    public void execute(RobotService rs) {
        Log.d(TAG, "Executing robot sensor state query");
        rs.writeToNXTSocket(getRawQuery());
    }

}
