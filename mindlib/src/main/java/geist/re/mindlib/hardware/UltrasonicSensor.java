package geist.re.mindlib.hardware;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import geist.re.mindlib.RobotService;
import geist.re.mindlib.events.Event;
import geist.re.mindlib.events.SoundStateEvent;
import geist.re.mindlib.events.TouchStateEvent;
import geist.re.mindlib.events.UltrasonicStateEvent;
import geist.re.mindlib.listeners.SoundSensorListener;
import geist.re.mindlib.listeners.TouchSensorListener;
import geist.re.mindlib.listeners.UltrasonicSensorListener;
import geist.re.mindlib.tasks.SensorStateQueryTask;

/**
 * Created by sbk on 09.03.17.
 */

public class UltrasonicSensor extends Sensor{
    public enum Type{
        /**
         * There is only one possibility
         */
        LOWSPEED_9V((byte)0x0B);

        private byte val;
        Type(byte val){
            this.val = val;
        }

        public static Type valueOf(byte raw){
            for(Type t : Type.values()){
                if(t.getRaw() == raw){
                    return t;
                }
            }
            return null;
        }


        public byte getRaw() {
            return val;
        }
    }


    public UltrasonicSensor(RobotService owner, Port port, Mode mode, Type type) {
        super(owner, port.getRaw(), mode.getRaw(), type.val);
    }


    public synchronized void registerListener(UltrasonicSensorListener msl, long rate) {
        registerListener(this,msl,rate);
    }

    public synchronized void pushSensorStateEvent(Event event) {
        Log.d(TAG, "Pushing motor state event");
        if(stateListener == null) return;
        currentStateUpdate = (UltrasonicStateEvent) event;
        ((UltrasonicSensorListener)stateListener).onEventOccurred(event);
    }

}
