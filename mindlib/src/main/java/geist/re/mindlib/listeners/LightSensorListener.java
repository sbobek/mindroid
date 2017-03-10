package geist.re.mindlib.listeners;

import geist.re.mindlib.events.Event;
import geist.re.mindlib.events.LightStateEvent;

/**
 * Created by sbk on 09.03.17.
 */

public abstract class LightSensorListener extends RobotListener {
    @Override
    public void onEventOccurred(Event e) {
        onEventOccurred((LightStateEvent)e);
    }
    public abstract void onEventOccurred(LightStateEvent e);
}
