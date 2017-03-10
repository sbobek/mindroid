package geist.re.mindlib.hardware;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import geist.re.mindlib.RobotService;
import geist.re.mindlib.events.Event;
import geist.re.mindlib.events.SoundStateEvent;
import geist.re.mindlib.events.TouchStateEvent;
import geist.re.mindlib.listeners.LightSensorListener;
import geist.re.mindlib.listeners.SoundSensorListener;
import geist.re.mindlib.listeners.TouchSensorListener;
import geist.re.mindlib.tasks.SensorStateQueryTask;

/**
 * Created by sbk on 09.03.17.
 */

public class TouchSensor extends Sensor{
    public enum Type{
        /**
         * There is only one possibility
         */
        SWITCH((byte)0x01);

        private byte val;
        Type(byte val){
            this.val = val;
        }
    }


    public TouchSensor(RobotService owner, Port port, Mode mode, Type type) {
        super(owner, port.getRaw(), mode.getRaw(), type.val);
    }

    public synchronized void registerListener(TouchSensorListener tsl, long rate){
        registerListener(this,tsl,rate);
    }

    public synchronized void pushSensorStateEvent(Event event) {
        Log.d(TAG, "Pushing motor state event");
        if(stateListener == null) return;
        currentStateUpdate = (TouchStateEvent) event;
        ((TouchSensorListener)stateListener).onEventOccurred(event);
    }
}
