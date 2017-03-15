package geist.re.mindlib.hardware;

import android.util.Log;

import geist.re.mindlib.RobotService;
import geist.re.mindlib.events.Event;
import geist.re.mindlib.events.UltrasonicStateEvent;
import geist.re.mindlib.exceptions.SensorDisconnectedException;
import geist.re.mindlib.listeners.RobotListener;
import geist.re.mindlib.listeners.UltrasonicSensorListener;

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


    public synchronized void connect(Port p){
        connect(p,Mode.RAWMODE,Type.LOWSPEED_9V);
    }

    public synchronized void registerListener(UltrasonicSensorListener msl) throws SensorDisconnectedException {
        registerListener(this,msl, RobotListener.DEFAULT_LISTENING_RATE);
    }

    public synchronized void registerListener(UltrasonicSensorListener msl, long rate) throws SensorDisconnectedException {
        registerListener(this,msl,rate);
    }

    public synchronized void pushSensorStateEvent(Event event) {
        Log.d(TAG, "Pushing ultrasonic state event");
        if(stateListener == null) return;
        currentStateUpdate = (UltrasonicStateEvent) event;
        ((UltrasonicSensorListener)stateListener).onEventOccurred(event);
    }

}
