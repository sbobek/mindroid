package geist.re.mindlib.hardware;

import android.util.Log;

import geist.re.mindlib.RobotService;
import geist.re.mindlib.events.Event;
import geist.re.mindlib.events.LightStateEvent;
import geist.re.mindlib.exceptions.SensorDisconnectedException;
import geist.re.mindlib.listeners.LightSensorListener;
import geist.re.mindlib.listeners.RobotListener;

/**
 * Created by sbk on 09.03.17.
 */

public class LightSensor extends Sensor{

    public LightSensor(RobotService owner){
        super(owner);
    }

    public LightSensor(RobotService owner, Port port, Mode mode, Type type) {
        super(owner, port.getRaw(), mode.getRaw(), type.getRaw());
    }

    public synchronized void connect(Port p, Type t){
        connect(p, Mode.RAWMODE,t);
    }

    public synchronized void registerListener(LightSensorListener lsl) throws SensorDisconnectedException {
        registerListener(this,lsl, RobotListener.DEFAULT_LISTENING_RATE);
    }

    public synchronized void registerListener(LightSensorListener lsl, long rate) throws SensorDisconnectedException {
        registerListener(this,lsl,rate);
    }


    @Override
    public synchronized void pushSensorStateEvent(Event event) {
        Log.d(TAG, "Pushing light state event");
        if(stateListener == null) return;
        currentStateUpdate = (LightStateEvent) event;
        ((LightSensorListener)stateListener).onEventOccurred(event);
    }

}
