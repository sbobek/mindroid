package geist.re.mindlib.tasks;

import geist.re.mindlib.RobotService;

/**
 * Created by sbk on 21.02.17.
 */
public abstract class RobotQueryTask {
    public static final int IDX_LENGTH_LSB = 0;
    public static final int IDX_LENGTH_MSB = 1;
    public static final int IDX_COMMAND_TYPE = 2;

    public static final String TAG = "QueryTask";

    public abstract byte[] getRawQuery();
    public abstract void execute(RobotService rs);
}
