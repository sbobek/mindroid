package geist.re.mindlib.tasks;

import geist.re.mindlib.RobotService;

/**
 * Created by sbk on 09.03.17.
 */

public class LightSensorStateQueryTask extends RobotQueryTask {
    @Override
    public byte[] getRawQuery() {
        return new byte[0];
    }

    @Override
    public void execute(RobotService rs) {

    }
}
