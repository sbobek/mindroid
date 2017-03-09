package geist.re.mindlib.events;

/**
 * Created by sbk on 09.03.17.
 */

public class UltrasonicStateEvent extends Event {
    public UltrasonicStateEvent(byte[] rawData) {
        super(rawData);
    }
}
