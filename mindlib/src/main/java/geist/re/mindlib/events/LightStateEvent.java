package geist.re.mindlib.events;

import geist.re.mindlib.exceptions.TelegramTypeException;
import geist.re.mindlib.hardware.LightSensor;
import geist.re.mindlib.hardware.Sensor;

/**
 * Created by sbk on 09.03.17.
 */

public class LightStateEvent extends SensorStateEvent {
    public LightStateEvent(byte[] telegram) throws TelegramTypeException {
        super(telegram);
    }

    /**
     * Returns light intensity in percents.
     * It uses {@link #getNormalizedOutput()} function to calculate the intensity.
     * @return light intensity
     */
    public double getLightIntensity(){
        return ((double)getNormalizedOutput())/NORMALIZED_MAX_VALUE;
    }

}
