import lejos.*;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;
public class main 
{
	
	private final static NXTRegulatedMotor sensorMotor = Motor.B;
	public static void main(String[] args) 
	{
		
		Odometer tracker = new Odometer();
		Navigator nav = new Navigator(tracker);
		OdometryDisplay odometryDisplay = new OdometryDisplay(tracker);
		//ObsticleDetector ob = new ObsticleDetector(usSensor, nav, sensorMotor);
		int buttonChoice;
		
		//LCD.drawString("cr", 0, 0);
		//LCD.drawString("or", 3, 0);
		
		do {
			// clear the display
			LCD.clear();

			// ask the user whether the motors should drive in a square or float
			LCD.drawString("< Left | Right >", 0, 0);
			LCD.drawString("       |        ", 0, 1);
			LCD.drawString(" comp | block  ", 0, 2);
			LCD.drawString("route |    ", 0, 3);
			LCD.drawString("       |  ", 0, 4);

			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT
				&& buttonChoice != Button.ID_RIGHT);

		if (buttonChoice == Button.ID_LEFT) 
		{
			
			nav.setComplex(true);
			
			tracker.start();
			odometryDisplay.start();
			nav.start();
		} else {
			
			nav.setBlock(true);
			tracker.start();
			odometryDisplay.start();
			nav.start();

		}
//		if (Button.waitForAnyPress()==Button.ID_LEFT)
//		{
//			//adding destination points for nav to go to these are foe part one
//			nav.addXDest(60);
//			nav.addYDest(30);
//			
//			nav.addXDest(30);
//			nav.addYDest(30);
//			
//			nav.addXDest(30);
//			nav.addYDest(60);
//			
//			nav.addXDest(60);
//			nav.addYDest(0);
//		}
//		
//
//		if (Button.waitForAnyPress()==Button.ID_RIGHT)
//		{
//			nav.addXDest(0);
//			nav.addYDest(60);
//			
//			nav.addXDest(60);
//			nav.addYDest(0);
//		}
//		
//		tracker.start();
//		odometryDisplay.start();
//		
//		tracker.setTheta(0);
//		nav.start();
//		//ob.start();
//		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
		
	}

}
