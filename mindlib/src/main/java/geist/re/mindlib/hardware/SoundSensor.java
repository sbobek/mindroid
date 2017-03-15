package geist.re.mindlib.hardware;

import android.util.Log;

import geist.re.mindlib.RobotService;
import geist.re.mindlib.events.Event;
import geist.re.mindlib.events.SoundStateEvent;
import geist.re.mindlib.exceptions.SensorDisconnectedException;
import geist.re.mindlib.listeners.RobotListener;
import geist.re.mindlib.listeners.SoundSensorListener;

/**
 * Created by sbk on 09.03.17.
 */

public class SoundSensor extends Sensor{

    public SoundSensor(RobotService owner){
        super(owner);
    }

    public SoundSensor(RobotService owner, Port port, Mode mode, Type type) {
        super(owner, port.getRaw(), mode.getRaw(), type.getRaw());
    }

    public synchronized void connect(Port p, Type t){
        connect(p,Mode.RAWMODE,t);
    }

    public synchronized void registerListener(SoundSensorListener ssl) throws SensorDisconnectedException {
        registerListener(this,ssl, RobotListener.DEFAULT_LISTENING_RATE);
    }

    public synchronized void registerListener(SoundSensorListener ssl, long rate) throws SensorDisconnectedException {
        registerListener(this,ssl,rate);
    }


    public synchronized void pushSensorStateEvent(Event event) {
        Log.d(TAG, "Pushing sound state event");
        if(stateListener == null) return;
        currentStateUpdate = (SoundStateEvent) event;
        ((SoundSensorListener)stateListener).onEventOccurred(event);
    }
}
