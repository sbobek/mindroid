package geist.re.mindlib.tasks;

import geist.re.mindlib.RobotService;

/**
 * Created by sbk on 21.02.17.
 */
public abstract class RobotQueryTask {

    public abstract byte[] getRawQuery();
    public abstract void execute(RobotService rs);
}
