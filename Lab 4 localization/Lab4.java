import lejos.nxt.*;

import lejos.nxt.comm.RConsole;

public class Lab4 {

	public static void main(String[] args) {
		// setup the odometer, display, and ultrasonic and light sensors
		RConsole.openBluetooth(20000);
		RConsole.println("connected");
		TwoWheeledRobot patBot = new TwoWheeledRobot(Motor.A, Motor.B);
		Odometer odo = new Odometer(patBot, true);
		//Navigation nav = new Navigation(odo);
		LCDInfo lcd = new LCDInfo(odo);
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S2);
		
		ColorSensor ls = new ColorSensor(SensorPort.S1);
		// perform the ultrasonic localization
		USLocalizer usl = new USLocalizer(odo, us);
		
		RConsole.println("waiting for button press");
		RConsole.println(Integer.toString(us.getDistance()));
		Button.waitForAnyPress();
		usl.doLocalization();
		
		// perform the light sensor localization
		LightLocalizer lsl = new LightLocalizer(odo, ls);
		lsl.doLocalization();
		Button.waitForAnyPress();
		
		/*patBot.setSpeeds(10, 10);
		int b =0;
		while(b!=Button.ID_ESCAPE)
		{
			Motor.A.forward();
			Motor.B.backward();
			double[] data  = new double[3]; 
			odo.getPosition(data);
			RConsole.println(Integer.toString(us.getDistance())+"\t");//+ Double.toString(data[2]));
			b=Button.readButtons();
		}*/
		
	}

}
