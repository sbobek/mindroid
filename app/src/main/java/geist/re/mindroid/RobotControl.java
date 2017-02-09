package geist.re.mindroid;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import geist.re.mindlib.RobotControlActivity;

public class RobotControl extends RobotControlActivity {
    private static final String ROBOT_NAME = "02Bolek";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot_control);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


    public void start(View v){
        if(robot != null){
            robot.connectToRobot(ROBOT_NAME);
            robot.executeMotorTask(robot.motorA.run(80));
            //robot.continueOperation(2000);
            robot.executeMotorTask(robot.motorA.stop());


        }else{
            Toast.makeText(this, "Service not ready...",Toast.LENGTH_LONG);
        }
    }

    public void quit(View v){
        finish();
    }

    public void pause(long time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
