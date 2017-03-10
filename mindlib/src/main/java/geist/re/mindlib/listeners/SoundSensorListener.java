package geist.re.mindlib.listeners;

import geist.re.mindlib.events.Event;
import geist.re.mindlib.events.SoundStateEvent;

/**
 * Created by sbk on 09.03.17.
 */

public abstract class SoundSensorListener extends RobotListener {
    @Override
    public void onEventOccurred(Event e) {
        onEventOccurred((SoundStateEvent)e);
    }
    public abstract void onEventOccurred(SoundStateEvent e);
}
