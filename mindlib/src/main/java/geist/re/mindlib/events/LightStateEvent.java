package geist.re.mindlib.events;

import geist.re.mindlib.exceptions.TelegramTypeException;
import geist.re.mindlib.hardware.LightSensor;
import geist.re.mindlib.hardware.Sensor;

/**
 * Created by sbk on 09.03.17.
 */

public class LightStateEvent extends SensorStateEvent {

    LightSensor.Type type;

    public LightStateEvent(byte[] telegram) throws TelegramTypeException {
        super(telegram);
        type = LightSensor.Type.valueOf(telegram[IDX_SENSOR_TYPE]);
    }
}
