package geist.re.mindroid;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import geist.re.mindlib.RobotControlActivity;
import geist.re.mindlib.RobotService;

public class RobotControl extends RobotControlActivity {
    private static final String TAG = "ControlApp";
    private static final String ROBOT_NAME = "02Bolek";
    private static final float VOLUME = 0.2f;

    FloatingActionButton start;
    FloatingActionButton stop;
    FloatingActionButton voice;
    FloatingActionButton connect;
    SoundPool sp = new SoundPool(5, AudioManager.STREAM_NOTIFICATION, 0);

    private static int READY;
    private static int YES;
    private static int ERROR;





    /**************************************************************/
    /**************************************************************/
    /**************************************************************/
    /**************************************************************/

    @Override
    public void commandProgram(){
        super.commandProgram();
        /*************** START YOUR PROGRAM HERE ***************/
        robot.executeMotorTask(robot.motorA.run(80));
        pause(1000);
        robot.executeMotorTask(robot.motorA.stop());
        robot.executeSyncTwoMotorTask(robot.motorA.run(30),robot.motorB.run(30));
        pause(1000);
        robot.executeSyncTwoMotorTask(robot.motorA.stop(), robot.motorB.stop());

    }

    @Override
    public void onVoiceCommand(String message) {
        super.onVoiceCommand(message);
        /*************** HANDLE VOICE MESSAGE HERE ***************/


        if(message.equals("run forward")){
            robot.executeSyncTwoMotorTask(robot.motorA.run(30),robot.motorB.run(30));
        }else if(message.equals("stop")){
            robot.executeSyncTwoMotorTask(robot.motorA.stop(), robot.motorB.stop());
        }else if(message.equals("destroy yourself")){
            robot.executeSyncTwoMotorTask(robot.motorA.run(30),robot.motorB.run(-30));
            stopRecognizer();
            int i=0;
            while(i < 10) {
                i++;
                playSound(ERROR);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }if(message.equals("run backward")) {
            robot.executeSyncTwoMotorTask(robot.motorA.run(-30), robot.motorB.run(-30));
        }else{
            Log.d(TAG, "Received wrong command: "+message);
            //error();
        }
    }

    /**************************************************************/
    /**************************************************************/
    /**************************************************************/
    /**************************************************************/

    private void pause(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    protected void error(){
        playSound(ERROR);
    }

    @Override
    protected void onStartListeningForVoiceCommands() {
        playSound(READY);
    }

    @Override
    protected void onStartListeningForVoiceWakeup() {
        playSound(READY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot_control);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        start = (FloatingActionButton) findViewById(R.id.start);
        stop = (FloatingActionButton) findViewById(R.id.stop);
        voice = (FloatingActionButton) findViewById(R.id.voice);
        connect = (FloatingActionButton) findViewById(R.id.connect);


        start.setVisibility(FloatingActionButton.INVISIBLE);
        stop.setVisibility(FloatingActionButton.INVISIBLE);
        voice.setVisibility(FloatingActionButton.INVISIBLE);
        connect.setVisibility(FloatingActionButton.INVISIBLE);

        /** soundId for Later handling of sound pool **/
        READY=sp.load(this,R.raw.ready, 1); // in 2nd param u have to pass your desire ringtone
        YES=sp.load(this,R.raw.yes, 1); // in 2nd param u have to pass your desire ringtone
        ERROR=sp.load(this,R.raw.error, 1); // in 2nd param u have to pass your desire ringtone

        //keep the screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    @Override
    protected void onRobotServiceConnected() {
        //enable connect button
        connect.setVisibility(FloatingActionButton.VISIBLE);

    }


    @Override
    protected void onRobotConnected() {
        //robot connected enable buttons;
        Toast.makeText(RobotControl.this,"Robot connected, go!",Toast.LENGTH_LONG).show();
        start.setVisibility(FloatingActionButton.VISIBLE);
        stop.setVisibility(FloatingActionButton.VISIBLE);
        voice.setVisibility(FloatingActionButton.VISIBLE);
        connect.setVisibility(FloatingActionButton.VISIBLE);

    }

    @Override
    protected void onRobotDisconnected() {
        //disable buttons and try connect again
        start.setVisibility(FloatingActionButton.INVISIBLE);
        stop.setVisibility(FloatingActionButton.INVISIBLE);
        voice.setVisibility(FloatingActionButton.INVISIBLE);
        connect.setVisibility(FloatingActionButton.VISIBLE);



    }

    public void start(View v){
        if(robot != null || robot.getConnectionState() != RobotService.CONN_STATE_CONNECTED){
            Toast.makeText(this, "Waiting for robot to connect",Toast.LENGTH_LONG).show();
            if(robot.getConnectionState() != RobotService.CONN_STATE_CONNECTING){
                robot.connectToRobot(ROBOT_NAME);
            }
            return;
        }
        new AsyncTask<Void, Void, Exception>(){
            @Override
            protected Exception doInBackground(Void... voids) {
                commandProgram();
                return null;
            }
        }.execute();

    }
    public void voice(View v){
        if(robot == null || robot.getConnectionState() != RobotService.CONN_STATE_CONNECTED){
            Toast.makeText(this, "Waiting for robbot to connect...", Toast.LENGTH_LONG).show();
            if(robot.getConnectionState() != RobotService.CONN_STATE_CONNECTING){
                robot.connectToRobot(ROBOT_NAME);
            }
            return;
        }
        startRecognizer();
    }

    public void stop(View c){
        if(robot == null || robot.getConnectionState() != RobotService.CONN_STATE_CONNECTED){
            Toast.makeText(this, "Waiting for robot to connect...", Toast.LENGTH_LONG).show();
            return;
        }
        stopRecognizer();
        robot.executeSyncThreeMotorTask(robot.motorA.stop(), robot.motorB.stop(), robot.motorC.stop());

    }

    public void connect(View v){
        if(robot == null){
            //bind to robot
            Toast.makeText(this,"Error, robot service is down...",Toast.LENGTH_LONG).show();
        } else if(robot.getConnectionState() != RobotService.CONN_STATE_CONNECTED &&
                robot.getConnectionState() != RobotService.CONN_STATE_CONNECTING){
            new AsyncTask<Void, Void, Exception>(){
                ProgressDialog progress = new ProgressDialog(RobotControl.this);
                boolean dismissed = false;

                @Override
                protected void onPreExecute() {
                    progress.setMessage("Connecting...");
                    progress.setTitle("Connecting to robot");
                    progress.setCancelable(false);
                    progress.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            dismissed = true;
                        }
                    });
                    progress.show();
                }

                @Override
                protected Exception doInBackground(Void... voids) {
                    Exception ex = null;
                    robot.connectToRobot(ROBOT_NAME);
                    while(robot.getConnectionState() != RobotService.CONN_STATE_CONNECTED){
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(dismissed) break;
                        if(robot.getConnectionState() == RobotService.CONN_STATE_DISCONNECTED){
                            robot.connectToRobot(ROBOT_NAME);
                        }
                    }
                    return ex;
                }

                @Override
                protected void onPostExecute(Exception e) {
                    progress.dismiss();
                }
            }.execute();



        }
    }

    public void playSound(final int soundId){
        sp.play(soundId, 1, 1, 0, 0, 1);
    }

    public void quit(View v){
        finish();
    }









}
