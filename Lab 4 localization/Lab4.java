import lejos.nxt.*;

import lejos.nxt.comm.RConsole;

public class Lab4 {

	public static void main(String[] args) {
		// setup the odometer, display, and ultrasonic and light sensors
		
		//Initializing 
		TwoWheeledRobot patBot = new TwoWheeledRobot(Motor.A, Motor.B);
		Odometer odo = new Odometer(patBot, true);
		LCDInfo lcd = new LCDInfo(odo);
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S2);
		ColorSensor ls = new ColorSensor(SensorPort.S1);
		
		
		USLocalizer usl = new USLocalizer(odo, us);
		
		RConsole.println("waiting for button press");
		RConsole.println(Integer.toString(us.getDistance()));
		Button.waitForAnyPress();
		// perform the ultrasonic localization
		usl.doLocalization();
		
		// perform the light sensor localization
		LightLocalizer lsl = new LightLocalizer(odo, ls);
		lsl.doLocalization();
		
		Button.waitForAnyPress();
		
	}

}
