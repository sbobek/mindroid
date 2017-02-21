package geist.re.mindlib.tasks;

import geist.re.mindlib.RobotService;

/**
 * Created by sbk on 09.02.17.
 */

public abstract class RobotTask {
    public static final String TAG = "Task";
    public abstract void  execute(RobotService rs);
    public abstract byte[] getRawQuery();
}
