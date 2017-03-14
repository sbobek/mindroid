# Mindroid
Android Library for Lego Mindstorms Robots.
Main features:
  * Controlling NXT brick using Android mobile phone (all sensors and motors)
  * Integration with PocketSphinx for speech recognition for voice control of robot.
  * Quickstart skeleton for begginers in Java programming (suitable for grades 8 and up)
  * Robot service (thread safe) for advanced programmers.
  
To see the available robot commands and their documentations, see [Overview of robot commands](#overview-of-robot-commands)

  
## Bgginers guide
The simplest way to start programing is to clone the repository and open the [RobotControl](https://github.com/sbobek/mindroid/blob/master/app/src/main/java/geist/re/mindroid/RobotControl.java) class that is located in the **app** module.

To use it with your NXT brick, you have to pair your mobile device with LegoMindstorm using sytem Bluetooth manager.
After that, you have to change the  ```private static final String ROBOT_NAME = "YOUR_NXT_NAME";``` to point to the name of your NXT.

There are two functions in the [RobotControl](https://github.com/sbobek/mindroid/blob/master/app/src/main/java/geist/re/mindroid/RobotControl.java) class that allows you to control NXT brick. These are: 
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

## Overview of robot commands

### Motor

### Touch sensor

### Light sensor

### Sound sensor

### Ultrasonic sensor

