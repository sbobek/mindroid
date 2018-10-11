package geist.re.mindlib.tasks;

import geist.re.mindlib.RobotService;

/**
 * Created by sbk on 11.10.18.
 */

public class PlaySoundTask extends RobotQueryTask {
    private String filename;

    public PlaySoundTask(String filename){
        this.filename = filename;

    }

    private byte[] getRawFilename(){
        return null;
    }

    @Override
    public byte[] getRawQuery() {
        return new byte[0];
    }

    @Override
    public void execute(RobotService rs) {

    }
}
