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
		
		
		LCD.drawString("Start", 0, 0);
		Button.waitForAnyPress();
		tracker.start();
		odometryDisplay.start();
		
		tracker.setTheta(0);
		nav.start();
		//ob.start();
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
		
	}

}