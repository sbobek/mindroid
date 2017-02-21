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
        rs.setOperationState(RobotService.OPERATION_STATE_DEAF);
        Log.d(TAG, "Connection set to wait by wait task");
        rs.addToPendingTasks(this);
        timer.schedule(new TimerTask() {
            public void run() {
                if(rs.getOperationState() == rs.OPERATION_STATE_DEAF) {
                    rs.setOperationState(RobotService.OPERATION_STATE_READY);
                    Log.d(TAG, "Connection reset to connected by wait task");
                    rs.removeFromPendingTasks(WaitTask.this);
                }
            }
        }, delay);
    }

    @Override
    public void dismiss(RobotService rs) {
        timer.cancel();
        rs.removeFromPendingTasks(this);
        if(rs.getOperationState() == rs.OPERATION_STATE_DEAF) {
            rs.setOperationState(RobotService.OPERATION_STATE_READY);
        }
    }

}