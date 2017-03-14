package geist.re.mindlib.hardware;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import geist.re.mindlib.RobotService;
import geist.re.mindlib.events.Event;
import geist.re.mindlib.events.SoundStateEvent;
import geist.re.mindlib.events.TouchStateEvent;
import geist.re.mindlib.exceptions.SensorDisconnectedException;
import geist.re.mindlib.listeners.LightSensorListener;
import geist.re.mindlib.listeners.SoundSensorListener;
import geist.re.mindlib.listeners.TouchSensorListener;
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

    public synchronized void connect(Port p, Mode m, Type t){
        this.port = p.getRaw();
        this.mode = m.getRaw();
        this.type = t.getRaw();
    }

    public synchronized void connect(Port p){
        this.port = p.getRaw();
        this.mode = Mode.RAWMODE.getRaw();
        this.type = Type.SWITCH.getRaw();
    }

    public synchronized void disconnet(){
        unregisterListener();
        this.port = Port.DISCONNECTED.getRaw();
    }

    public synchronized void registerListener(TouchSensorListener tsl, long rate) throws SensorDisconnectedException {
        registerListener(this,tsl,rate);
    }

    public synchronized void pushSensorStateEvent(Event event) {
        Log.d(TAG, "Pushing motor state event");
        if(stateListener == null) return;
        currentStateUpdate = (TouchStateEvent) event;
        ((TouchSensorListener)stateListener).onEventOccurred(event);
    }
}
