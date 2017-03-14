package geist.re.mindlib.hardware;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import geist.re.mindlib.RobotService;
import geist.re.mindlib.events.Event;
import geist.re.mindlib.events.SoundStateEvent;
import geist.re.mindlib.events.TouchStateEvent;
import geist.re.mindlib.events.UltrasonicStateEvent;
import geist.re.mindlib.exceptions.SensorDisconnectedException;
import geist.re.mindlib.listeners.SoundSensorListener;
import geist.re.mindlib.listeners.TouchSensorListener;
import geist.re.mindlib.listeners.UltrasonicSensorListener;
import geist.re.mindlib.tasks.SensorStateQueryTask;

/**
 * Created by sbk on 09.03.17.
 */

public class UltrasonicSensor extends Sensor{

    public UltrasonicSensor(RobotService owner){
        super(owner);
    }

    public UltrasonicSensor(RobotService owner, Port port, Mode mode, Type type) {
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
        this.type = Type.LOWSPEED_9V.getRaw();
    }

    public synchronized void disconnet(){
        unregisterListener();
        this.port = Port.DISCONNECTED.getRaw();
    }


    public synchronized void registerListener(UltrasonicSensorListener msl, long rate) throws SensorDisconnectedException {
        registerListener(this,msl,rate);
    }

    public synchronized void pushSensorStateEvent(Event event) {
        Log.d(TAG, "Pushing motor state event");
        if(stateListener == null) return;
        currentStateUpdate = (UltrasonicStateEvent) event;
        ((UltrasonicSensorListener)stateListener).onEventOccurred(event);
    }

}
