package geist.re.mindlib;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import geist.re.mindlib.hardware.Motor;
import geist.re.mindlib.listeners.MotorStateListener;
import geist.re.mindlib.tasks.MotorTask;
import geist.re.mindlib.tasks.RobotQueryTask;
import geist.re.mindlib.tasks.RobotTask;


/**
 * Created by sbk on 08.02.17.
 */

public class RobotService extends Service {

    public static final int CONN_STATE_DISCONNECTED = 0;
    public static final int CONN_STATE_CONNECTING = 1;
    public static final int CONN_STATE_CONNECTED = 2;
    public static final int CONN_STATE_LOST = 3;

    public static final int OPERATION_STATE_READY = 1;
    public static final int OPERATION_STATE_DEAF = 2;
    public static final int OPERATION_STATE_BUSY = 3;

    private static final String TAG = "RobotService";
    public static final String ROBOT_STATE_NOTIFICATION = "robotStateNotification";
    public static final String CONNECTION_STATE_CODE = "connectionCode";


    private BluetoothAdapter mAdapter;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mConnState;
    private int mOperState;

    private LinkedList<RobotTask> robotTaskQueue = new LinkedList<>();
    private LinkedList<RobotQueryTask> robotQueryQueue = new LinkedList<>();
    private LinkedList<RobotTask> pendingTasks = new LinkedList<>();
    TaskExecutorThread taskExecutor = new TaskExecutorThread();
    QueryExecutorThread queryExecutor = new QueryExecutorThread();



    public Motor motorA,motorB,motorC;

    private Binder mRobotBinder = new RobotBinder();


    public RobotService(){
        motorA = new Motor(Motor.A,this);
        motorB = new Motor(Motor.B,this);
        motorC = new Motor(Motor.C,this);

        taskExecutor.start();


    }




    public synchronized void addToQueueTask(RobotTask rt){
        robotTaskQueue.add(rt);
        synchronized (taskExecutor) {
            taskExecutor.notify();
        }

    }

    public synchronized void addToQueryQueue(RobotQueryTask rqt){
        robotQueryQueue.add(rqt);
        synchronized (queryExecutor) {
            queryExecutor.notify();
        }

    }

    public synchronized void addToPendingTasks(RobotTask rt){
        pendingTasks.add(rt);
    }

    public synchronized  void removeFromPendingTasks(RobotTask rt){
        pendingTasks.remove(rt);
    }

    public void executeMotorTask(MotorTask motorTask){
        addToQueueTask(motorTask);
    }

    public void executeSyncTwoMotorTask(MotorTask motorTask1, MotorTask motorTask2){
        RobotTask finalTask =  motorTask1.syncWith(motorTask2);
        addToQueueTask(finalTask);
    }

    public void executeSyncThreeMotorTask(MotorTask motorTask1, MotorTask motorTask2, MotorTask motorTask3){
        RobotTask finalTask = motorTask1.syncWith(motorTask2).syncWith(motorTask3);
        addToQueueTask(finalTask);
    }


    synchronized public boolean writeToNXTSocket(byte [] command){
        if(getConnectionState() == CONN_STATE_CONNECTED) {
            mConnectedThread.write(command);
            return true;
        }
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Robot service created");
    }

    public int getOperationState() {
        return mOperState;
    }

    public void setOperationState(int mOperState) {
        this.mOperState = mOperState;
    }


    public class RobotBinder extends Binder {
        RobotService getService() {
            return RobotService.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mRobotBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        disconnectDevice();
        return super.onUnbind(intent);
    }

    public synchronized void connectToRobot(String robotName){
        BluetoothDevice result = null;
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mAdapter == null){
            Log.d(TAG, "Bluetooth not available, connection failed");
            connectionFailed();
        }else if(!mAdapter.isEnabled()){
            Log.d(TAG, "Bluetooth not enabled, connection failed");
            connectionFailed();
        }
        Set<BluetoothDevice> devices = mAdapter.getBondedDevices();
        Log.d(TAG, "Looking for bonded devices.");
        if (devices != null) {
            for (BluetoothDevice device : devices) {
                if ((device.getBluetoothClass() != null) && (device.getBluetoothClass().getDeviceClass() == BluetoothClass.Device.TOY_ROBOT)) {
                    if (robotName.equals(device.getName())) {
                        result = device;
                        break;
                    }
                }
            }
        }else{
            Log.d(TAG, "No bonded devices");
        }

        if(result == null){
            Log.d(TAG,"No device of a given name is available to connect.");
        }
        initiateConnection(result);
    }


    private synchronized void initiateConnection(BluetoothDevice device) {
        Log.d(TAG,"Initiating connection to "+device.getName());

        if (mConnState == CONN_STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setConnectionState(CONN_STATE_CONNECTING);
        Log.d(TAG,"Connecting in progress...");
    }

    private synchronized void deviceConnected(BluetoothSocket socket, BluetoothDevice device) {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        setConnectionState(CONN_STATE_CONNECTED);
        Log.d(TAG,"Connection succeeded...");
    }

    private synchronized void disconnectDevice() {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        setConnectionState(CONN_STATE_DISCONNECTED);
        Log.d(TAG,"Connection stopped...");
    }

    private void connectionFailed() {
        setConnectionState(CONN_STATE_DISCONNECTED);
        Log.d(TAG,"Connection failed...");
    }

    private void connectionLost() {
        setConnectionState(CONN_STATE_LOST);
        setConnectionState(CONN_STATE_DISCONNECTED);
        Log.d(TAG,"Connection lost...");
    }


    private synchronized void setConnectionState(int state) {
        mConnState = state;
        if(mConnState == CONN_STATE_CONNECTED){
            synchronized (taskExecutor) {
                taskExecutor.notify();
            }
        }
        Intent intent = new Intent(ROBOT_STATE_NOTIFICATION);
        intent.putExtra(CONNECTION_STATE_CODE, mConnState);
        sendBroadcast(intent);

    }

    public synchronized int getConnectionState(){
        return mConnState;
    }

    private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
        }

        public void run() {
            setName("ConnectThread");
            mAdapter.cancelDiscovery();

            try {
                mmSocket = mmDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                mmSocket.connect();
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    // This is a workaround that reportedly helps on some older devices like HTC Desire, where using
                    // the standard createRfcommSocketToServiceRecord() method always causes initiateConnection() to fail.
                    Method method = mmDevice.getClass().getMethod("createRfcommSocket", new Class[] { int.class });
                    mmSocket = (BluetoothSocket) method.invoke(mmDevice, Integer.valueOf(1));
                    mmSocket.connect();
                } catch (Exception e1) {
                    e1.printStackTrace();
                    connectionFailed();
                    try {
                        mmSocket.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                    return;
                }
            }

            synchronized (this) {
                mConnectThread = null;
            }

            deviceConnected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                if (mmSocket != null) {
                    mmSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    String msg = "";
                    for(byte b: buffer){
                        msg+=Byte.toString(b);
                    }
                    Log.d(TAG,msg);
                    //read bytes and notify listener, if any
                } catch (IOException e) {
                    e.printStackTrace();
                    connectionLost();
                    break;
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
                connectionLost();
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected class TaskExecutorThread extends Thread{
            @Override
            public void run() {
                while(true) {
                    while (robotTaskQueue.isEmpty() ||
                            RobotService.this.getOperationState() != OPERATION_STATE_READY ||
                            RobotService.this.getConnectionState() != CONN_STATE_CONNECTED) {
                        synchronized (this) {
                            try {
                                wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    synchronized (this) {
                        RobotTask rt = robotTaskQueue.poll();
                        rt.execute(RobotService.this);
                    }
                }

            }
    }

    protected class QueryExecutorThread extends Thread{
        @Override
        public void run() {
            while(true) {
                while (robotQueryQueue.isEmpty() || RobotService.this.getConnectionState() != CONN_STATE_CONNECTED) {
                    synchronized (this) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                synchronized (this) {
                    RobotQueryTask rqt = robotQueryQueue.poll();
                    writeToNXTSocket(rqt.getRawQuery());
                }
            }

        }
    }

}
