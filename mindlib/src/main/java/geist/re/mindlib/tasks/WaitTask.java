package geist.re.mindlib.tasks;

/**
 * Created by sbk on 09.02.17.
 */

public class WaitTask implements RobotTask {
    private long delay;
    public WaitTask(long delay){
        this.delay = delay;
    }

    public long getDelay() {
        return delay;
    }

    @Override
    public byte[] getTaskData() {
        return new byte[0];
    }
}
