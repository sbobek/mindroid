package geist.re.mindlib.hardware;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import geist.re.mindlib.RobotService;
import geist.re.mindlib.events.Event;
import geist.re.mindlib.events.LightStateEvent;
import geist.re.mindlib.events.SoundStateEvent;
import geist.re.mindlib.listeners.LightSensorListener;
import geist.re.mindlib.listeners.SoundSensorListener;
import geist.re.mindlib.tasks.SensorStateQueryTask;

/**
 * Created by sbk on 09.03.17.
 */

public class SoundSensor extends Sensor{
    public enum Type{
        /**
         * Sound in DB units
         */
        SOUND_DB((byte)0x07),
        /**
         * Sound in DBA units
         */
        SOUND_DBA((byte)0x08);

        private byte val;
        Type(byte val){
            this.val = val;
        }

        public byte getRaw() {
            return val;
        }
    }

    public SoundSensor(RobotService owner, Port port, Mode mode, Type type) {
        super(owner, port.getRaw(), mode.getRaw(), type.val);
    }



    public synchronized void registerListener(SoundSensorListener ssl, long rate){
        registerListener(this,ssl,rate);
    }


    public synchronized void pushSensorStateEvent(Event event) {
        Log.d(TAG, "Pushing motor state event");
        if(stateListener == null) return;
        currentStateUpdate = (SoundStateEvent) event;
        ((SoundSensorListener)stateListener).onEventOccurred(event);
    }
}
