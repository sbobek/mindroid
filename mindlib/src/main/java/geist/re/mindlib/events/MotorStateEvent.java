package geist.re.mindlib.events;

import geist.re.mindlib.exceptions.TelegramTypeException;
import geist.re.mindlib.utils.BluetoothProtocolUtils;

/**
 * Created by sbk on 21.02.17.
 */

public class MotorStateEvent extends Event {
    public static final int IDX_STATUS = 2;
    public static final int IDX_MOTOR_PORT = 3;
    public static final int IDX_POWER = 4;
    public static final int IDX_MODE = 5;
    public static final int IDX_REGULATION_MODE = 6;
    public static final int IDX_TURN_RATIO = 7;
    public static final int IDX_RUN_STATE = 8;
    public static final int IDX_TACHO_LIMIT_START = 9;
    public static final int IDX_TACHO_LIMIT_END = 12;
    public static final int IDX_TACHO_COUNT_START = 13;
    public static final int IDX_TACHO_COUNT_END = 16;
    public static final int IDX_BLOCK_TACHO_COUNT_START = 17;
    public static final int IDX_BLOCK_TACHO_COUNT_END = 20;
    public static final int IDX_ROTATION_COUNT_START = 21;
    public static final int IDX_ROTATION_COUNT_END = 24;

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
        if(telegram[IDX_TELEGRAM_TYPE] != TYPE_GETOUTPUTSTATE) throw new TelegramTypeException();
        if(telegram[IDX_STATUS] == 0){
            status = true;
        }else{
            status = false;
        }
        motor = telegram[IDX_MOTOR_PORT];
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
