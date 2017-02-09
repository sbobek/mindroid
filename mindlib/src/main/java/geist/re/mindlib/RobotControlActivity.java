package geist.re.mindlib;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class RobotControlActivity extends AppCompatActivity {
    private static final String TAG = "ROBOT";
    private static final int REQUEST_ENABLE_BT = 1;

    protected RobotService robot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Log.d(TAG,"Bluetooth not available on this device");
        }else if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }else {
            createRobotService();
        }
    }

    @Override
    protected void onDestroy() {
        unbindService(robotServiceConnection);
        super.onDestroy();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    createRobotService();
                } else {
                    Toast.makeText(this, "Bluetooth not enabled, exiting.", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void createRobotService() {
        Intent intent = new Intent(this, RobotService.class);
        Log.d(TAG, "Creating robot service");
        bindService(intent, robotServiceConnection,
                Context.BIND_AUTO_CREATE);
    }




    private ServiceConnection robotServiceConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className,
                                       IBinder binder) {
            RobotService.RobotBinder rb = (RobotService.RobotBinder) binder;
            robot = rb.getService();
            Log.d(TAG, "Connected to RobotService");
        }

        public void onServiceDisconnected(ComponentName className) {
            robot = null;
        }
    };
}
