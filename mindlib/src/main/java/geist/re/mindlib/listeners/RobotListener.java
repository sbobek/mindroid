package geist.re.mindlib.listeners;

import geist.re.mindlib.events.Event;

/**
 * Created by sbk on 17.02.17.
 */

public abstract class RobotListener {
    public static final long DEFAULT_LISTENING_RATE = 200;
    protected abstract void onEventOccurred(Event e);
}
