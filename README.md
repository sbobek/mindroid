# Mindroid
Android Library for Lego Mindstorms Robots.
Main features:
  * Controlling NXT brick using Android mobile phone (all sensors and motors)
  * Integration with PocketSphinx for speech recognition for voice control of robot.
  * Quickstart skeleton for begginers in Java programming (suitable for grades 8 and up)
  * Robot service (thread safe) for advanced programmers.
  
## Bgginers guide
The simplest way to start programing is to clone the repository and open the RobotControl class that is located in the **app** module.
There are two functions: 
  * ```commandProgram()``` -- here you can write a regular program that will be executed when you hit the play button on your mobile phone.
  * ```onVoiceCommand()``` -- here you can place any code that should be executed when voice recognition system recognises given phrases.
  
The example below shows how to use ```commandProgram()``` method to send requests to NXT motors.

``` java
 @Override
    public void commandProgram(){
        super.commandProgram();
        /*************** START YOUR PROGRAM HERE ***************/
        //rotate motors A and B three times 
        robot.executeSyncTwoMotorTask(robot.motorA.run(10,360*3),robot.motorB.run(10,360*3));
        while(robot.motorA.getState() == Motor.STATE_RUNNING) {
            pause(500);
            Log.d(TAG, "Waiting....");
        }
        robot.executeSyncTwoMotorTask(robot.motorA.stop(), robot.motorB.stop());
  

}

```

Below, the example of a usage of voice commands is given.
Speach recognition will be enabled when you push the microphone button in the mobnile app.
It is worth noting, that both functions can be executed simultaneously, i.e. you can write a command program to tell robot to move around, and then in ```onVoiceCommand()``` stop robot when the **stop** phare was recognised.

For more information on voice recognition, see: [Advanced guide](#advanced-guide) and [CMU Sphinx website](http://cmusphinx.sourceforge.net/)

``` java
 @Override
    public void onVoiceCommand(String message) {
        super.onVoiceCommand(message);
        /*************** HANDLE VOICE MESSAGE HERE ***************/
        if(message.equals("run forward")){
            robot.executeSyncTwoMotorTask(robot.motorA.run(30),robot.motorB.run(30));
        }else if(message.equals("stop")){
            robot.executeSyncTwoMotorTask(robot.motorA.stop(), robot.motorB.stop());
        }else if(message.equals("run backward")) {
            robot.executeSyncTwoMotorTask(robot.motorA.run(-30), robot.motorB.run(-30));
        }else{
            Log.d(TAG, "Received wrong command: "+message);
        }
}
```

## Advanced guide
