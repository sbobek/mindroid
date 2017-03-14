package geist.re.mindlib.hardware;

import android.util.Log;

import geist.re.mindlib.RobotService;
import geist.re.mindlib.events.Event;
import geist.re.mindlib.events.TouchStateEvent;
import geist.re.mindlib.exceptions.SensorDisconnectedException;
import geist.re.mindlib.listeners.TouchSensorListener;
import geist.re.mindlib.tasks.ConnectSensorTask;
import geist.re.mindlib.tasks.SensorStateQueryTask;

/**
 * Created by sbk on 09.03.17.
 */

public class TouchSensor extends Sensor{

    public TouchSensor(RobotService owner){
        super(owner);
    }

    public TouchSensor(RobotService owner, Port port, Mode mode, Type type) {
        super(owner, port.getRaw(), mode.getRaw(), type.getRaw());
    }


    public synchronized void connect(Port p){
        connect(p,Mode.RAWMODE,Type.SWITCH);
    }

    public synchronized void registerListener(TouchSensorListener tsl, long rate) throws SensorDisconnectedException {
        registerListener(this,tsl,rate);
    }

    public synchronized void pushSensorStateEvent(Event event) {
        Log.d(TAG, "Pushing touch state event");
        if(stateListener == null) return;
        currentStateUpdate = (TouchStateEvent) event;
        ((TouchSensorListener)stateListener).onEventOccurred(event);
    }
}
