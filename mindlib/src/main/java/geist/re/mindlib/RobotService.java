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
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import geist.re.mindlib.events.Event;
import geist.re.mindlib.events.LightStateEvent;
import geist.re.mindlib.events.MotorStateEvent;
import geist.re.mindlib.events.SensorStateEvent;
import geist.re.mindlib.events.SoundStateEvent;
import geist.re.mindlib.events.TouchStateEvent;
import geist.re.mindlib.events.UltrasonicStateEvent;
import geist.re.mindlib.exceptions.TelegramTypeException;
import geist.re.mindlib.hardware.LightSensor;
import geist.re.mindlib.hardware.Motor;
import geist.re.mindlib.hardware.SoundSensor;
import geist.re.mindlib.hardware.TouchSensor;
import geist.re.mindlib.hardware.UltrasonicSensor;
import geist.re.mindlib.tasks.MotorTask;
import geist.re.mindlib.tasks.PlaySoundTask;
import geist.re.mindlib.tasks.RobotQueryTask;


/**
 * Created by sbk on 08.02.17.
 */

public class RobotService extends Service {

    public static final int CONN_STATE_DISCONNECTED = 0;
    public static final int CONN_STATE_CONNECTING = 1;
    public static final int CONN_STATE_CONNECTED = 2;
    public static final int CONN_STATE_LOST = 3;

    private static final String TAG = "RobotService";
    public static final String ROBOT_STATE_NOTIFICATION = "robotStateNotification";
    public static final String CONNECTION_STATE_CODE = "connectionCode";


    private BluetoothAdapter mAdapter;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mConnState;

    private LinkedBlockingQueue<RobotQueryTask> robotQueryQueue = new LinkedBlockingQueue<>();

    QueryExecutorThread queryExecutor = new QueryExecutorThread();



    public final Motor motorA,motorB,motorC;
    public final LightSensor lightSensor;
    public final TouchSensor touchSensor;
    public final SoundSensor soundSensor;
    public final UltrasonicSensor ultrasonicSensor;

    private Binder mRobotBinder = new RobotBinder();


    public RobotService(){
        motorA = new Motor(Motor.A,this);
        motorB = new Motor(Motor.B,this);
        motorC = new Motor(Motor.C,this);
        lightSensor = new LightSensor(this);
        touchSensor = new TouchSensor(this);
        soundSensor = new SoundSensor(this);
        ultrasonicSensor = new UltrasonicSensor(this);

        queryExecutor.start();


    }


    public synchronized void addToQueryQueue(RobotQueryTask rqt){
        robotQueryQueue.add(rqt);
        synchronized (queryExecutor) {
            queryExecutor.notify();
        }

    }

    public void executePlaySound(PlaySoundTask pst){
        addToQueryQueue(pst);
    }

    public void executeMotorTask(MotorTask motorTask){
        addToQueryQueue(motorTask);
    }

    public void executeSyncTwoMotorTask(MotorTask motorTask1, MotorTask motorTask2){
        motorTask1.sync();
        motorTask2.sync();
        addToQueryQueue(motorTask1);
        addToQueryQueue(motorTask2);
    }

    public void executeSyncThreeMotorTask(MotorTask motorTask1, MotorTask motorTask2, MotorTask motorTask3){
        motorTask1.sync();
        motorTask2.sync();
        motorTask3.sync();
        addToQueryQueue(motorTask1);
        addToQueryQueue(motorTask2);
        addToQueryQueue(motorTask3);
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
        }else {
            initiateConnection(result);
        }
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
            synchronized (queryExecutor) {
                queryExecutor.notify();
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
                    Event event = convertResponseIntoEvent(buffer);
                    if(event != null) {
                        Log.d(TAG, "Received telegram: " + event.toString());
                        notifyListeners(event);
                    }
                    //read bytes and notify listener, if any
                } catch (IOException e) {
                    e.printStackTrace();
                    connectionLost();
                    break;
                } catch (TelegramTypeException e) {
                    e.printStackTrace();
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
        public Event convertResponseIntoEvent(byte[] rawResponse) throws TelegramTypeException {
            if(rawResponse[Event.IDX_TELEGRAM_TYPE] != Event.TELEGRAM_RESPONSE){
                //not a replay telegram, return null;
                String msg = "";
                for(byte b: rawResponse){
                    msg+=":"+b+":";
                }
                Log.d(TAG, "Received not queried response: "+msg);
                return null;
            }
            switch(rawResponse[Event.IDX_RESPONSE_TYPE]){
                case Event.RESPONSE_TYPE_GETOUTPUTSTATE:
                    return new MotorStateEvent(rawResponse);
                case Event.RESPONSE_TYPE_GETINPUTVALUES:
                    if(lightSensor != null && lightSensor.getRawPort() == rawResponse[Event.IDX_INPUT_PORT]){
                        return new LightStateEvent(rawResponse);
                    }else if(touchSensor != null && touchSensor.getRawPort() == rawResponse[Event.IDX_INPUT_PORT]){
                        return new TouchStateEvent(rawResponse);
                    }else if(soundSensor != null && soundSensor.getRawPort() == rawResponse[Event.IDX_INPUT_PORT]){
                        return new SoundStateEvent(rawResponse);
                    }else if(ultrasonicSensor != null && ultrasonicSensor.getRawPort() == rawResponse[Event.IDX_INPUT_PORT]){
                        return new UltrasonicStateEvent(rawResponse);
                    }
                    break;
                default:
                    String msg = "";
                    for(byte b: rawResponse){
                        msg+=":"+b+":";
                    }
                    Log.d(TAG, "Unknown telegram: "+msg);

            }
            return null;
        }

        public void notifyListeners(Event event){
            //motor listeners and robot-user-listeners
            switch(event.getResponseType()){
                case Event.RESPONSE_TYPE_GETOUTPUTSTATE:
                    MotorStateEvent mse = (MotorStateEvent)event;
                    if(mse.getMotor() == Motor.A){
                        Log.d(TAG, "Notifying Motor A listener");
                        motorA.pushMotorStateEvent(event);
                    }else if(mse.getMotor() == Motor.B){
                        motorB.pushMotorStateEvent(event);
                        Log.d(TAG, "Notifying Motor B listener");
                    }else{
                        motorC.pushMotorStateEvent(event);
                        Log.d(TAG, "Notifying Motor C listener");
                    }
                    break;
                case Event.RESPONSE_TYPE_GETINPUTVALUES:
                    SensorStateEvent s = (SensorStateEvent)event;
                    if(lightSensor != null && lightSensor.getPort() == s.getPort()){
                        Log.d(TAG, "Notifying Light sensor listener");
                        lightSensor.pushSensorStateEvent(s);
                    }else if(touchSensor != null && touchSensor.getPort() ==  s.getPort()){
                        Log.d(TAG, "Notifying Touch sensor listener");
                        touchSensor.pushSensorStateEvent(s);
                    }else if(soundSensor != null && soundSensor.getPort() ==  s.getPort()){
                        Log.d(TAG, "Notifying Sound sensor listener");
                        soundSensor.pushSensorStateEvent(s);
                    }else if(ultrasonicSensor != null && ultrasonicSensor.getPort() ==  s.getPort()){
                        Log.d(TAG, "Notifying Ultrasonic sensor listener");
                        ultrasonicSensor.pushSensorStateEvent(s);
                    }
                    break;
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
                RobotQueryTask rqt = robotQueryQueue.poll();
                rqt.execute(RobotService.this);
            }

        }
    }

}
