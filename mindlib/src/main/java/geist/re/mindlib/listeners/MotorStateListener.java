package geist.re.mindlib.listeners;

import geist.re.mindlib.events.Event;
import geist.re.mindlib.events.MotorStateEvent;

/**
 * Created by sbk on 21.02.17.
 */

public abstract class MotorStateListener extends RobotListener {

    @Override
    public void onEventOccurred(Event e) {
        onEventOccurred((MotorStateEvent)e);
    }

    public abstract void onEventOccurred(MotorStateEvent e);
}
