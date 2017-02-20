package geist.re.mindlib.tasks;

import java.nio.ByteBuffer;

import geist.re.mindlib.RobotService;
import geist.re.mindlib.hardware.Motor;

/**
 * Created by sbk on 09.02.17.
 */

public class RobotMotorTask implements RobotTask {

    private static final byte VAL_LENGTH_LSB = 0x0c;
    private static final byte VAL_LENGTH_MSB = 0x00;
    private static final byte VAL_DIRECT_CMD = (byte) 0x80;
    private static final byte VAL_CMD_TYPE = 0x04;

    private static final byte VAL_MODE_MOTORON = 0x01;
    private static final byte VAL_MODE_USE_BREAKES = 0x02;
    private static final byte VAL_MODE_ENABLE_REGULATION = 0x04;

    private static final byte VAL_REGULATION_IDLE = 0x00;
    private static final byte VAL_REGULATION_POWER = 0x01;
    private static final byte VAL_REGULATION_SYNC = 0x02;

    private static final byte VAL_RUN_STATE_IDLE = 0x00;
    private static final byte VAL_RUN_STATE_RAMPPUP = 0x10;
    private static final byte VAL_RUN_STATE_RUNNING = 0x20;
    private static final byte VAL_RUN_STATE_RAMPDOWN = 0x40;



    private static final int IDX_OUTPUT_PORT = 4;
    private static final int IDX_POWER_SET_POINT = 5;
    private static final int IDX_MODE = 6;
    private static final int IDX_REGULATION = 7;
    private static final int IDX_TURN = 8;
    private static final int IDX_RUN_STATE = 9;

    private static final int IDX_TACHO_START = 10;





    byte [] data = {VAL_LENGTH_LSB, VAL_LENGTH_MSB, VAL_DIRECT_CMD, VAL_CMD_TYPE, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

    public RobotMotorTask(byte [] data){
        this.data = data;
    }

    public RobotMotorTask(Motor m){
        setOutputPort(m.getPort());
        enableRegulationMode();
        enableSpeedRegulation();
        enableUseBreaksMode();
        enableMotorOnMode();
        setRunStateRunning();

    }


    public void setOutputPort(byte port){
        data[IDX_OUTPUT_PORT] = port;
    }

    public void setPowerSetPoint(byte powerSetPoint){
        data[IDX_POWER_SET_POINT] = powerSetPoint;
    }

    public void enableSpeedRegulation(){
        data[IDX_REGULATION] |= VAL_REGULATION_POWER;
    }

    public void sync(){
        data[IDX_REGULATION] |= VAL_REGULATION_SYNC;
    }

    public RobotMotorTask syncWith(RobotMotorTask other){
        sync();
        other.sync();

        byte [] mergedData = new byte[data.length+other.data.length];
        System.arraycopy(data, 0, mergedData, 0, data.length);
        System.arraycopy(other.data, 0, mergedData, data.length,other.data.length);

        return new RobotMotorTask(mergedData);

    }

    public void setTurnRatio(byte turnRatio){
        data[IDX_TURN] = turnRatio;
    }

    public void setRunStateRunning(){
        data[IDX_RUN_STATE] = VAL_RUN_STATE_RUNNING;
    }

    public void enableMotorOnMode(){
        data[IDX_MODE] |= VAL_MODE_MOTORON;
    }
    public void enableUseBreaksMode(){
        data[IDX_MODE] |= VAL_MODE_USE_BREAKES;
    }
    public void enableRegulationMode(){
        data[IDX_MODE] |= VAL_MODE_ENABLE_REGULATION;
    }

    public void setTachoLimit(int limit){
        byte[] bytes = ByteBuffer.allocate(4).putInt(limit).array();

        data[IDX_TACHO_START] = bytes[3];
        data[IDX_TACHO_START+1] = bytes[2];
        data[IDX_TACHO_START+2] = bytes[1];
        data[IDX_TACHO_START+3] = bytes[0];
    }


    @Override
    public void execute(RobotService rs) {
        rs.writeToNXTSocket(data);
    }

    @Override
    public void dismiss(RobotService rs) {
        //do nothing, just do not execute
        //or actually do something - cancel timer for tacho, stop motors if they run for infinity?
    }
}
