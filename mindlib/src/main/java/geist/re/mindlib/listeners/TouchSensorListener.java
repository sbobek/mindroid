package geist.re.mindlib.listeners;

import geist.re.mindlib.events.Event;
import geist.re.mindlib.events.TouchStateEvent;

/**
 * Created by sbk on 09.03.17.
 */

public abstract class TouchSensorListener extends RobotListener {
    @Override
    public void onEventOccurred(Event e) {
        onEventOccurred((TouchStateEvent)e);
    }
    public abstract void onEventOccurred(TouchStateEvent e);
}
