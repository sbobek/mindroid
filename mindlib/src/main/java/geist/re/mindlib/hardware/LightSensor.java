package geist.re.mindlib.hardware;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import geist.re.mindlib.RobotService;
import geist.re.mindlib.events.Event;
import geist.re.mindlib.events.LightStateEvent;
import geist.re.mindlib.events.MotorStateEvent;
import geist.re.mindlib.listeners.LightSensorListener;
import geist.re.mindlib.tasks.SensorStateQueryTask;

/**
 * Created by sbk on 09.03.17.
 */

public class LightSensor extends Sensor{
    public enum Type{
        /**
         * Led on a sensor is on
         */
        LIGHT_ACTIVE((byte)0x05),
        /**
         * Led on a sensor is off
         */
        LIGHT_INCTIVE((byte)0x06);

        private byte val;
        Type(byte val){
            this.val = val;
        }
    }




    public LightSensor(RobotService owner, Port port, Mode mode, Type type) {
        super(owner, port.getRaw(), mode.getRaw(), type.val);
    }

    public synchronized void registerListener(LightSensorListener lsl, long rate){
        registerListener(this,lsl,rate);
    }


    @Override
    public synchronized void pushSensorStateEvent(Event event) {
        Log.d(TAG, "Pushing motor state event");
        if(stateListener == null) return;
        currentStateUpdate = (LightStateEvent) event;
        ((LightSensorListener)stateListener).onEventOccurred(event);
    }

}
