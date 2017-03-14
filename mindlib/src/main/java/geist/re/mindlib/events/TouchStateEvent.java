package geist.re.mindlib.events;

import geist.re.mindlib.exceptions.TelegramTypeException;

/**
 * Created by sbk on 09.03.17.
 */

public class TouchStateEvent extends SensorStateEvent {
    public TouchStateEvent(byte[] rawData) throws TelegramTypeException {
        super(rawData);
    }

    /**
     * Returns if the current status of the touch sensor is pressed.
     * It uses {@link #getNormalizedOutput()} function to calculate the state.
     * @return true is the sensor is pressed
     */
    public boolean isPressed(){
        return getNormalizedOutput() != 0;
    }
}
