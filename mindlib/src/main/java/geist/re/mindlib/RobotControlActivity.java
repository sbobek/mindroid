package geist.re.mindlib;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;
import geist.re.mindlib.exceptions.SensorDisconnectedException;


public abstract class RobotControlActivity extends AppCompatActivity implements
        RecognitionListener {
    private static final String TAG = "ROBOT";
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 2;

    /* Named searches allow to quickly reconfigure the decoder */
    private static final String KWS_SEARCH = "wakeup";
    private static final String ROBOT_COMMANDS = "commands";
    private static final String ON_HOLD = "hold";

    /* Keyword we are looking for to activate menu */
    private static final String KEYPHRASE = "robot";


    protected RobotService robot;
    protected SpeechRecognizer recognizer;


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
        //setup recognizer service
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            Log.d(TAG, "Recognition will not be possible, permissions not granted");
        }else {
            runRecognizerSetup();
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(
                RobotService.ROBOT_STATE_NOTIFICATION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    private void runRecognizerSetup() {
        // Recognizer initialization is a time-consuming and it involves IO,
        // so we execute it in async task
        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(RobotControlActivity.this);
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {
                    Log.d(TAG,"Initialization fo recognizer failed.");
                } else {
                    switchSearch(ON_HOLD);
                }
            }
        }.execute();
    }

    @Override
    protected void onDestroy() {
        unbindService(robotServiceConnection);
        if (recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }
        super.onDestroy();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    createRobotService();
                } else {
                    Toast.makeText(this, "Bluetooth not enabled, exiting.", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            case PERMISSIONS_REQUEST_RECORD_AUDIO:
                if (resultCode == Activity.RESULT_OK) {
                    runRecognizerSetup();
                } else {
                    Toast.makeText(this, "Audio recording not enabled, voice commands not available.", Toast.LENGTH_LONG).show();
                }
            break;
        }

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
            robot = rb.getService();;
            onRobotServiceConnected();
            Log.d(TAG, "Connected to RobotService");
        }

        public void onServiceDisconnected(ComponentName className) {
            robot = null;
        }
    };

    protected abstract void onRobotServiceConnected();


    /**
     * In partial result we get quick updates about current hypothesis. In
     * keyword spotting mode we can react here, in other modes we need to wait
     * for final result in onResult.
     */
    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null)
            return;

        String text = hypothesis.getHypstr();
        Log.d(TAG, "Partial: "+text);
        if (text.equals(KEYPHRASE)) {
            switchSearch(ROBOT_COMMANDS);
        }
    }

    /**
     * This callback is called when we stop the recognizer.
     */
    @Override
    public void onResult(Hypothesis hypothesis) {
        if (hypothesis == null){
            return;
        }

        final String text = hypothesis.getHypstr();
        Toast.makeText(RobotControlActivity.this, text, Toast.LENGTH_LONG).show();
        if(text.equals(KEYPHRASE)){
            return;
        }

        onVoiceCommand(text);
    }

    protected void startRecognizer(){
        switchSearch(KWS_SEARCH);
    }

    protected  void stopRecognizer(){
        switchSearch(ON_HOLD);
    }

    public void commandProgram() throws SensorDisconnectedException {
        if(robot == null) return;
    }
    public void onVoiceCommand(String message){
        if(robot == null) return;
    }

    @Override
    public void onBeginningOfSpeech() {
    }

    /**
     * We stop recognizer here to get a final result
     */
    @Override
    public void onEndOfSpeech() {
        if (!recognizer.getSearchName().equals(KWS_SEARCH))
            switchSearch(KWS_SEARCH);
    }

    private void switchSearch(String searchName) {
        recognizer.stop();
        if(searchName.equals(ON_HOLD)) {
            return;
        }
        // If we are not spotting, start listening with timeout (10000 ms or 10 seconds).
        if (searchName.equals(KWS_SEARCH)) {
            onStartListeningForVoiceWakeup();
            recognizer.startListening(searchName);
        }else {
            onStartListeningForVoiceCommands();
            recognizer.startListening(searchName, 10000);
        }
    }

    protected abstract void onStartListeningForVoiceCommands();

    protected abstract void onStartListeningForVoiceWakeup();
    

    private void setupRecognizer(File assetsDir) throws IOException {
        // The recognizer can be configured to perform multiple searches
        // of different kind and switch between them

        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))

                .setRawLogDir(assetsDir) // To disable logging of raw audio comment out this call (takes a lot of space on the device)

                .getRecognizer();
        recognizer.addListener(this);

        /** In your application you might not need to add all those searches.
         * They are added here for demonstration. You can leave just one.
         */

        // Create keyword-activation search.
        recognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE);

        // Create grammar-based search for selection between demos
        File robotGrammar = new File(assetsDir, "robot-commands.gram");
        recognizer.addGrammarSearch(ROBOT_COMMANDS, robotGrammar);
    }

    @Override
    public void onError(Exception error) {
        Log.d(TAG, "Error during voice recognition.");
    }

    @Override
    public void onTimeout() {
        switchSearch(KWS_SEARCH);
    }


    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                int resultCode = bundle.getInt(RobotService.CONNECTION_STATE_CODE);
                switch(resultCode){
                    case RobotService.CONN_STATE_CONNECTED:
                        Log.d(TAG, "Broadcast received, robot connected");
                        onRobotConnected();
                        break;
                    case RobotService.CONN_STATE_LOST:
                        Log.d(TAG, "Broadcast received, robot disconnected");
                        onRobotDisconnected();

                }
            }
        }
    };


    protected abstract void onRobotDisconnected();

    protected abstract void onRobotConnected();
}
