package geist.re.mindlib.tasks;

import geist.re.mindlib.RobotService;

/**
 * Created by sbk on 11.10.18.
 *
 *
 */

/**
 * Syntax
 * [0x17] [0x00] [0x00 or 0x80] [0x02] [0x00 or 0x01] [fileName.rso]
 *
 * Explanation:
 *   Byte 0-1: Command length LSB first.
 *   Byte 2: Command type- direct command.
 *   For direct command with response message use 0x00, otherwise, for direct command without the reply message, use 0x80.
 *   Byte 3: Command- play a sound file.
 *   Byte 4: Play in a loop. For playing the sound file only once use 0x0, otherwise, for playing the sound file indefinitely use 0x01.
 *   Byte 5-24: Sound file name, including its extension- rso.
 *
 * The system is case insensitive.
 * The bytes of file name and type characters should be ASCII encoded.
 * NULL terminator must appear at the end ('\0').
 * In case the name of the file is shorter than 15 characters,
 * NULL terminator byte should be added to the end of the file name extension for each missing character.
 * Total file name characters, including the NULL terminators, should always be 20.
 * The reply message that is coming back (if requested) looks like:
 * [0x03] [0x00] [0x02] [0x02] [0x00 or error]

 */
public class PlaySoundTask extends RobotQueryTask {

    public static final byte VAL_LENGTH_LSB = 0x17;
    public static final byte VAL_LENGTH_MSB = 0x00;
    public static final byte VAL_DIRECT_CMD = (byte) 0x00;
    public static final byte VAL_PLAY_SOUND = (byte) 0x02;
    public static final byte VAL_DEFAULT_NOLOOP = (byte) 0x00;
    public static final byte VAL_TERM = 0x00;

    public static final int IDX_PLAY_LOOP = 4;
    public static final int IDX_MSG_START = 5;

    byte [] query = {VAL_LENGTH_LSB,VAL_LENGTH_MSB,VAL_DIRECT_CMD,
            VAL_PLAY_SOUND,VAL_DEFAULT_NOLOOP,
            VAL_TERM,VAL_TERM,VAL_TERM,VAL_TERM,VAL_TERM,
            VAL_TERM,VAL_TERM,VAL_TERM,VAL_TERM,VAL_TERM,
            VAL_TERM,VAL_TERM,VAL_TERM,VAL_TERM,VAL_TERM,
            VAL_TERM,VAL_TERM,VAL_TERM,VAL_TERM,VAL_TERM};

    private String filename;

    public PlaySoundTask(String filename){
        this.filename = filename;
        query  = getRawQuery();

    }

    private byte[] getRawFilename(){
        byte[] raw = new byte[filename.length()];
        for(int i = 0; i < filename.length(); i++){
            raw[i] = (byte)filename.charAt(i);
        }
        return raw;
    }

    @Override
    public byte[] getRawQuery() {
        byte[] msg = getRawFilename();
        for( int i = 0; i < msg.length; i++){
            query[i+IDX_MSG_START] = msg[i];
        }
        return query;
    }

    @Override
    public void execute(RobotService rs) {
        rs.writeToNXTSocket(query);
    }
}
