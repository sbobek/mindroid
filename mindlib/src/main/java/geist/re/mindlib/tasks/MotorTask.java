package geist.re.mindlib.tasks;

import android.util.Log;

import java.nio.ByteBuffer;

import geist.re.mindlib.RobotService;
import geist.re.mindlib.hardware.Motor;
import geist.re.mindlib.utils.BluetoothProtocolUtils;

/**
 * Created by sbk on 09.02.17.
 */

public class MotorTask extends RobotQueryTask {

    public static final byte VAL_LENGTH_LSB = 0x0c;
    public static final byte VAL_LENGTH_MSB = 0x00;
    public static final byte VAL_DIRECT_CMD = (byte) 0x80;
    

    public static final int IDX_OUTPUT_PORT = 4;
    public static final int IDX_POWER_SET_POINT = 5;
    public static final int IDX_MODE = 6;
    public static final int IDX_REGULATION = 7;
    public static final int IDX_TURN = 8;
    public static final int IDX_RUN_STATE = 9;

    public static final int IDX_TACHO_START = 10;


    byte[] resetMotorPosition = {Motor.VAL_RESET_MESSAGE_LENGTH_LSB, Motor.VAL_RESET_MESSAGE_LENGTH_MSB, VAL_DIRECT_CMD, 0x0A, 0x00, (byte)0x00};
    byte[] data = {VAL_LENGTH_LSB, VAL_LENGTH_MSB, VAL_DIRECT_CMD, Motor.VAL_CMD_TYPE, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

    public MotorTask(byte[] data, byte[] resetMotorPosition) {
        this.data = data;
        this.resetMotorPosition = resetMotorPosition;
    }

    public MotorTask(Motor m) {
        setOutputPort(m.getPort());
        enableRegulationMode();
        enableSpeedRegulation();
        enableUseBreaksMode();
        enableMotorOnMode();

    }


    public void setOutputPort(byte port) {
        data[IDX_OUTPUT_PORT] = port;
        resetMotorPosition[IDX_OUTPUT_PORT] = port;
    }

    public void setPowerSetPoint(byte powerSetPoint) {
        data[IDX_POWER_SET_POINT] = powerSetPoint;
    }

    public void enableSpeedRegulation() {
        data[IDX_REGULATION] |= Motor.VAL_REGULATION_POWER;
    }

    public void sync() {
        data[IDX_REGULATION] |= Motor.VAL_REGULATION_SYNC;
    }

    public void setTurnRatio(byte turnRatio) {
        data[IDX_TURN] = turnRatio;
    }

    public void setRunStateRunning() {
        data[IDX_RUN_STATE] = Motor.VAL_RUN_STATE_RUNNING;
    }

    public void setRunStateRampup() {
        data[IDX_RUN_STATE] = Motor.VAL_RUN_STATE_RAMPPUP;
    }
    public void setRunStateRampdown() {
        data[IDX_RUN_STATE] = Motor.VAL_RUN_STATE_RAMPDOWN;
    }

    public void setRunStateIdle(){
        data[IDX_RUN_STATE] = Motor.VAL_RUN_STATE_IDLE;
    }

    public void enableMotorOnMode() {
        data[IDX_MODE] |= Motor.VAL_MODE_MOTORON;
    }

    public void enableUseBreaksMode() {
        data[IDX_MODE] |= Motor.VAL_MODE_USE_BREAKES;
    }

    public void enableRegulationMode() {
        data[IDX_MODE] |= Motor.VAL_MODE_ENABLE_REGULATION;
    }

    public void setTachoLimit(int limit) {
        byte bytes [] = BluetoothProtocolUtils.integerToLittleEndian(limit);
        data[IDX_TACHO_START] = bytes[0];
        data[IDX_TACHO_START + 1] = bytes[1];
        data[IDX_TACHO_START + 2] = bytes[2];
        data[IDX_TACHO_START + 3] = bytes[3];
        Log.d(TAG, "Tacho limit: " + Integer.toHexString(limit) + " was translated to " +
                Integer.toHexString(bytes[0]) + "-" +
                Integer.toHexString(bytes[1]) + "-" +
                Integer.toHexString(bytes[2]) + "-" +
                Integer.toHexString(bytes[3]));

    }


    @Override
    public void execute(RobotService rs) {
        rs.writeToNXTSocket(resetMotorPosition);
        rs.writeToNXTSocket(data);
    }

    @Override
    public byte[] getRawQuery() {
        byte[] mergedData = new byte[resetMotorPosition.length + data.length];
        System.arraycopy(resetMotorPosition, 0, mergedData, 0, resetMotorPosition.length);
        System.arraycopy(data, 0, mergedData, resetMotorPosition.length, data.length);
        return mergedData;
    }


}
