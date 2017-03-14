package geist.re.mindlib.events;

import geist.re.mindlib.exceptions.TelegramTypeException;

/**
 * Created by sbk on 09.03.17.
 */

public class UltrasonicStateEvent extends SensorStateEvent {
    public UltrasonicStateEvent(byte[] rawData) throws TelegramTypeException {
        super(rawData);
    }

    /**
     * Returns distance from an obstacle.
     * It uses {@link #getNormalizedOutput()} function to calculate the distance.
     * @return distance from obstacle in centimeters (0-255)
     */
    public double getDistanceInCentimeters(){
        return getNormalizedOutput();
    }
}
