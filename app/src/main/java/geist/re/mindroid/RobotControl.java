package geist.re.mindroid;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import geist.re.mindlib.RobotControlActivity;
import geist.re.mindlib.RobotService;
import geist.re.mindlib.events.TouchStateEvent;
import geist.re.mindlib.exceptions.SensorDisconnectedException;
import geist.re.mindlib.hardware.Sensor;
import geist.re.mindlib.listeners.TouchSensorListener;

public class RobotControl extends RobotControlActivity {
    private static final String TAG = "ControlApp";
    private static final String ROBOT_NAME = "02Bolek";

    FloatingActionButton start;
    FloatingActionButton stop;
    FloatingActionButton voice;
    FloatingActionButton connect;
    FloatingActionButton orientation;

    TextView xText;
    TextView yText;
    TextView zText;






    /**************************************************************/
    /**************************************************************/
    /**************************************************************/
    /**************************************************************/

    @Override
    public void commandProgram() throws SensorDisconnectedException {
        super.commandProgram();
        /*************** START YOUR PROGRAM HERE ***************/
        robot.executeMotorTask(robot.motorA.run(-10,180));
        robot.touchSensor.connect(Sensor.Port.ONE);

        robot.touchSensor.registerListener(new TouchSensorListener() {
            @Override
            public void onEventOccurred(TouchStateEvent e) {
                Log.d(TAG, "Pressure "+e.getPressure());
                if(e.isPressed()){
                    robot.executeMotorTask(robot.motorA.stop());
                    robot.executeMotorTask(robot.motorA.run(10,360));
                    robot.touchSensor.unregisterListener();
                }
            }
        });

    }

    @Override
    public void onVoiceCommand(String message) {
        super.onVoiceCommand(message);
        /*************** HANDLE VOICE MESSAGE HERE ***************/


        if(message.equals("run forward")){
            speakBack("No problem");
            robot.executeSyncTwoMotorTask(robot.motorA.run(30),robot.motorB.run(30));
        }else if(message.equals("stop")){
            speakBack("It was a pleasure");
            robot.executeSyncTwoMotorTask(robot.motorA.stop(), robot.motorB.stop());
        }else if(message.equals("run backward")) {
            speakBack("I'm executing");
            robot.executeSyncTwoMotorTask(robot.motorA.run(-30), robot.motorB.run(-30));
        }else{
            Log.d(TAG, "Received wrong command: "+message);
            //error();
        }
    }



    @Override
    protected synchronized void onGestureCommand(double x, double y, double z) {
        displayValues(x,y,z);
        /*************** HANDLE GESTURES HERE ***************/
    }


    /**************************************************************/
    /**************************************************************/
    /**************************************************************/
    /**************************************************************/

    private void displayValues(double x, double y, double z){
        xText.setText(Double.toString(x));
        yText.setText(Double.toString(y));
        zText.setText(Double.toString(z));
    }
    private void pause(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onStartListeningForVoiceCommands() {

    }

    @Override
    protected void onStartListeningForVoiceWakeup() {

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
        orientation = (FloatingActionButton) findViewById(R.id.orientationButton);


        xText = (TextView) findViewById(R.id.xText);
        yText = (TextView) findViewById(R.id.yText);
        zText = (TextView) findViewById(R.id.zText);


        start.setVisibility(FloatingActionButton.INVISIBLE);
        stop.setVisibility(FloatingActionButton.INVISIBLE);
        voice.setVisibility(FloatingActionButton.INVISIBLE);
        connect.setVisibility(FloatingActionButton.INVISIBLE);
        orientation.setVisibility(FloatingActionButton.INVISIBLE);

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
        orientation.setVisibility(FloatingActionButton.VISIBLE);

    }



    @Override
    protected void onRobotDisconnected() {
        //disable buttons and try connect again
        start.setVisibility(FloatingActionButton.INVISIBLE);
        stop.setVisibility(FloatingActionButton.INVISIBLE);
        voice.setVisibility(FloatingActionButton.INVISIBLE);
        connect.setVisibility(FloatingActionButton.VISIBLE);
        orientation.setVisibility(FloatingActionButton.INVISIBLE);



    }

    public void gestures(View v){
        startOrientationScanning();
    }

    public void start(View v){
        if(robot == null || robot.getConnectionState() != RobotService.CONN_STATE_CONNECTED){
            Toast.makeText(this, "Waiting for robot to connect",Toast.LENGTH_LONG).show();
            if(robot.getConnectionState() != RobotService.CONN_STATE_CONNECTING){
                robot.connectToRobot(ROBOT_NAME);
            }
            return;
        }
        new AsyncTask<Void, Void, Exception>(){
            @Override
            protected Exception doInBackground(Void... voids) {
                try {
                    commandProgram();
                } catch (SensorDisconnectedException e) {
                    e.printStackTrace();
                }
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
        stopOrientationScanning();
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

    public void quit(View v){
        finish();
    }









}
