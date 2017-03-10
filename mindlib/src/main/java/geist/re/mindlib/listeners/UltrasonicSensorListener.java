package geist.re.mindlib.listeners;

import geist.re.mindlib.events.Event;
import geist.re.mindlib.events.UltrasonicStateEvent;

/**
 * Created by sbk on 09.03.17.
 */

public abstract class UltrasonicSensorListener extends RobotListener {
    @Override
    public void onEventOccurred(Event e) {
        onEventOccurred((UltrasonicStateEvent)e);
    }
    public abstract void onEventOccurred(UltrasonicStateEvent e);
}
