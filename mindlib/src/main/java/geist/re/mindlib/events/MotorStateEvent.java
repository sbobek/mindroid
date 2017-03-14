package geist.re.mindlib.events;

import geist.re.mindlib.exceptions.TelegramTypeException;
import geist.re.mindlib.utils.BluetoothProtocolUtils;

/**
 * Created by sbk on 21.02.17.
 */

public class MotorStateEvent extends Event {
    public static final int IDX_POWER = 6;
    public static final int IDX_MODE = 7;
    public static final int IDX_REGULATION_MODE = 8;
    public static final int IDX_TURN_RATIO = 9;
    public static final int IDX_RUN_STATE = 10;
    public static final int IDX_TACHO_LIMIT_START = 11;
    public static final int IDX_TACHO_LIMIT_END = 14;
    public static final int IDX_TACHO_COUNT_START = 15;
    public static final int IDX_TACHO_COUNT_END = 18;
    public static final int IDX_BLOCK_TACHO_COUNT_START = 19;
    public static final int IDX_BLOCK_TACHO_COUNT_END = 22;
    public static final int IDX_ROTATION_COUNT_START = 23;
    public static final int IDX_ROTATION_COUNT_END = 26;

    public static final int STATUS_TRUE = 0;
    public static final int STATUS_FALSE = 1;

    private boolean status;
    private int motor;
    private int power;



    private int mode;
    private int turnRatio;
    private int runState;
    private int tachoLimit;
    private int tachoCount;
    private int blockTachoCount;
    private int rotationCount;




    public MotorStateEvent(byte[] telegram) throws TelegramTypeException {
        super(telegram);
        if(telegram[IDX_RESPONSE_TYPE] != RESPONSE_TYPE_GETOUTPUTSTATE) throw new TelegramTypeException("Response routed to incorrect object");
        if(telegram[IDX_STATUS] == 0){
            status = true;
        }else{
            status = false;
        }
        motor = telegram[IDX_INPUT_PORT];
        power = telegram[IDX_POWER];
        mode = telegram[IDX_MODE];
        turnRatio = telegram[IDX_TURN_RATIO];
        runState = telegram[IDX_RUN_STATE];
        tachoLimit = BluetoothProtocolUtils.littleEndianToInteger(telegram,IDX_TACHO_LIMIT_START);
        tachoCount = BluetoothProtocolUtils.littleEndianToInteger(telegram,IDX_TACHO_COUNT_START);
        blockTachoCount = BluetoothProtocolUtils.littleEndianToInteger(telegram,IDX_BLOCK_TACHO_COUNT_START);
        rotationCount = BluetoothProtocolUtils.littleEndianToInteger(telegram,IDX_ROTATION_COUNT_START);
    }

    public boolean isStatus() {
        return status;
    }

    public int getMotor() {
        return motor;
    }

    public int getPower() {
        return power;
    }

    public int getMode() {
        return mode;
    }

    public int getTurnRatio() {
        return turnRatio;
    }

    public int getRunState() {
        return runState;
    }

    public int getTachoLimit() {
        return tachoLimit;
    }

    public int getTachoCount() {
        return tachoCount;
    }

    public int getBlockTachoCount() {
        return blockTachoCount;
    }

    public int getRotationCount() {
        return rotationCount;
    }

}
