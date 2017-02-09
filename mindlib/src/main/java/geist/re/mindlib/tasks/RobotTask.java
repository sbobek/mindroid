package geist.re.mindlib.tasks;

import geist.re.mindlib.RobotService;

/**
 * Created by sbk on 09.02.17.
 */

public interface RobotTask {
    public static final String TAG = "Task";
    void execute(RobotService rs);
}
