package geist.re.mindlib.tasks;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import geist.re.mindlib.RobotService;

/**
 * Created by sbk on 09.02.17.
 */

public class WaitTask implements RobotTask {
    private long delay;
    Timer timer =  new Timer();

    public WaitTask(long delay){
        this.delay = delay;
    }


    @Override
    public void execute(final RobotService rs) {
        rs.setState(RobotService.STATE_WAIT);
        Log.d(TAG, "Connection set to wait by wait task");
        timer.schedule(new TimerTask() {
            public void run() {
                rs.setState(RobotService.STATE_CONNECTED);
                Log.d(TAG, "Connection reset to connected by wait task");
            }
        }, delay);
        //create timer task
    }

}
