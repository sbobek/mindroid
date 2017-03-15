package geist.re.mindlib.events;

import geist.re.mindlib.exceptions.TelegramTypeException;
import geist.re.mindlib.hardware.Sensor;
import geist.re.mindlib.utils.BluetoothProtocolUtils;

/**
 * Created by sbk on 14.03.17.
 */

public class SensorStateEvent extends Event {
    public static final int IDX_VALID = 6;
    public static final int IDX_CALIBRATED = 7;
    public static final int IDX_SENSOR_TYPE = 8;
    public static final int IDX_SENSOR_MODE = 9;
    public static final int IDX_RAW_AD_START = 10;
    public static final int IDX_RAW_AD_END = 11;
    public static final int IDX_NORM_AD_START = 12;
    public static final int IDX_NORM_AD_END = 13;
    public static final int IDX_SCALED_VAL_START = 14;
    public static final int IDX_SCALED_VAL_END = 15;
    public static final int IDX_CALIB_VAL_START = 16;
    public static final int IDX_CALIB_VAL_END = 17;

    public static final int NORMALIZED_MAX_VALUE = 1023;


    private final Sensor.Port port;
    private final Sensor.Type type;
    private final boolean status;
    private final boolean valid;
    private final boolean calibrated;
    private final Sensor.Mode mode;
    private final int rawOutput;
    private final int normalizedOutput;
    private final int scaledOutput;
    private final int calibratedOutput;

    public SensorStateEvent(byte[] telegram) throws TelegramTypeException {
        super(telegram);
        if(telegram[IDX_RESPONSE_TYPE] != RESPONSE_TYPE_GETINPUTVALUES) throw new TelegramTypeException("Response routed to incorrect object");
        if(telegram[IDX_STATUS] == 0){
            status = true;
        }else{
            status = false;
        }

        valid = (telegram[IDX_VALID] == 0 ? true : false);
        calibrated = (telegram[IDX_CALIBRATED] == 0 ? true : false);

        port = Sensor.Port.valueOf(telegram[IDX_INPUT_PORT]);
        type = Sensor.Type.valueOf(telegram[IDX_SENSOR_TYPE]);

        mode = Sensor.Mode.valueOf(telegram[IDX_SENSOR_MODE]);
        byte temp[] = new byte[4];
        temp[0] = telegram[IDX_RAW_AD_START];
        temp[1] = telegram[IDX_RAW_AD_END];
        rawOutput = BluetoothProtocolUtils.littleEndianToInteger(temp);

        temp[0] = telegram[IDX_NORM_AD_START];
        temp[1] = telegram[IDX_NORM_AD_END];
        normalizedOutput = BluetoothProtocolUtils.littleEndianToInteger(temp);


        temp[0] = telegram[IDX_SCALED_VAL_START];
        temp[1] = telegram[IDX_SCALED_VAL_END];
        scaledOutput = BluetoothProtocolUtils.littleEndianToInteger(temp);


        temp[0] = telegram[IDX_CALIB_VAL_START];
        temp[1] = telegram[IDX_CALIB_VAL_END];
        calibratedOutput = BluetoothProtocolUtils.littleEndianToInteger(temp);

    }


    public Sensor.Port getPort() {
        return port;
    }

    public boolean isStatus() {
        return status;
    }

    public boolean isValid() {
        return valid;
    }

    public boolean isCalibrated() {
        return calibrated;
    }

    public Sensor.Mode getMode() {
        return mode;
    }

    public int getRawOutput() {
        return rawOutput;
    }

    public int getNormalizedOutput() {
        return normalizedOutput;
    }

    public int getScaledOutput() {
        return scaledOutput;
    }

    public int getCalibratedOutput() {
        return calibratedOutput;
    }

    public Sensor.Type getType() {
        return type;
    }
}
