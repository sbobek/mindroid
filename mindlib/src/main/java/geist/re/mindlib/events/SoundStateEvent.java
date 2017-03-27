package geist.re.mindlib.events;

import geist.re.mindlib.exceptions.TelegramTypeException;

/**
 * Created by sbk on 09.03.17.
 */

public class SoundStateEvent extends SensorStateEvent {
    public SoundStateEvent(byte[] rawData) throws TelegramTypeException {
        super(rawData);
    }

    /**
     * Returns sound intensity in percents.
     * It uses {@link #getNormalizedOutput()} function to calculate the intensity.
     * @return sound intensity
     */
    public double getSoundIntensity(){
        return ((double)getNormalizedOutput())/NORMALIZED_MAX_VALUE;
    }
}
