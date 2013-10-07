import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.RConsole;

public class USLocalizer {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE };
	public static double ROTATION_SPEED = 30;
	private int EDGE_BOUNDRY = 70;
	
	private int yD,xD,distance;
	private double yTheta,xTheta,wideAngle;
	
	private Odometer odo;
	private Navigation nav;
	private TwoWheeledRobot robot;
	private UltrasonicSensor us;
	private LocalizationType locType;
	
	public USLocalizer(Odometer odo, UltrasonicSensor us) 
	{
		this.odo = odo;
		this.robot = odo.getTwoWheeledRobot();
		this.us = us;
		this.nav= new Navigation(odo,Motor.A,Motor.B);
		//this.locType = locType;	
		// switch off the ultrasonic sensor
		//us.off();
	}
	
	public void doLocalization() 
	{
		int wall = getFilteredData();
		while(wall>200 ||wall == 0)
		{
			wall = getFilteredData();
		}
		
		if(wall < EDGE_BOUNDRY)
		{
			locType = LocalizationType.RISING_EDGE;
		}
		else
		{
			locType = LocalizationType.FALLING_EDGE;
		}
		
		if (locType == LocalizationType.RISING_EDGE) 
		{
			Sound.beep();
			RConsole.println(Integer.toString(getFilteredData()));
			setSpeed(100);
			//robot.setSpeeds(10, 10);
			int distance =getFilteredData();
			while(distance<EDGE_BOUNDRY)
			{
				xD = distance;
				RConsole.println(Integer.toString(distance));
				turnClockwise();
				distance = getFilteredData();
			}
			stopMotors();
			RConsole.println("stopped-"+Integer.toString(distance));
			double thetaOne = odo.getTheta();
			setSpeed(100);
			while(distance>EDGE_BOUNDRY)
			{
				RConsole.println(Integer.toString(distance));
				turnClockwise();
				distance = getFilteredData();
			}
			yD = distance;
			stopMotors();
			RConsole.println("stopped-"+ Integer.toString(distance));
			double theataTwo = odo.getTheta();
			Sound.beep();
			
			wideAngle = theataTwo-thetaOne;
			double currentHeading = ((wideAngle)/2)+45;
			yTheta = 180 - currentHeading;
			xTheta = yTheta;//idealy
			odo.setTheta(currentHeading);
			
		}
		else 
		{
			
			Sound.buzz();
			RConsole.println(Integer.toString(getFilteredData()));
			setSpeed(150);
			int distance =getFilteredData();
			while(distance>EDGE_BOUNDRY)
			{
				RConsole.println(Integer.toString(distance));
				turnClockwise();
				
				distance = getFilteredData();
			}
			yD = distance;
			double thetaOne = odo.getTheta();
			RConsole.println("stoped-"+Integer.toString(distance));
			Sound.beep();
			stopMotors();
			setSpeed(100);
			while(distance <EDGE_BOUNDRY)
			{
				RConsole.println(Integer.toString(distance));
				turnCounterClockwise();
				distance = getFilteredData();
			}
			setSpeed(100);
			while(distance>EDGE_BOUNDRY)
			{
				RConsole.println(Integer.toString(distance));
				turnCounterClockwise();
				
				distance = getFilteredData();
				
			}
			xD = distance;
			stopMotors();
			double thetaTwo = odo.getTheta();//math.abs
			wideAngle = thetaOne+(360-thetaTwo);
			RConsole.println(Double.toString(wideAngle/2));
			double currentHeading = 360-((wideAngle/2)-45);
			xTheta = currentHeading-270;//math.abs
			yTheta = xTheta;//idealy
			odo.setTheta(currentHeading);
			
		}
		
		RConsole.println("distances x-"+Integer.toString(xD)+"***y-"+Integer.toString(yD));
		RConsole.println("angles x-"+Double.toString(xTheta)+"***y-"+Double.toString(yTheta));
		double x = xD*Math.cos(Math.toRadians(xTheta));
		double y = yD*Math.cos(Math.toRadians(yTheta));
		//this is in respect to bottom left corner position 
		odo.setX(x);
		odo.setY(y);
		RConsole.println("ended"+ Double.toString(y)+"****"+Double.toString(x));
		//nav.travelTo(15, 15);
		//nav.turnTo(nav.calOptimalAngle(nav.calDestAngle(60,60)));
		nav.travelTo(30, 30);//in respect to bottom corner
	}
	
	private int getFilteredData() 
	{
		int distance;
		// wait for the ping to complete
		try { Thread.sleep(10); } catch (InterruptedException e) {}
		
		// there will be a delay here
		
		distance = us.getDistance();
		if(distance > 180)
		{
			
		return this.distance;//return same value as last time	
		}
		else
		{
		this.distance =	distance;	
		return distance;
		}
	}
	
	public void stopMotors()
	{
		Motor.A.stop();
		Motor.B.stop();	
	}
	public void turnClockwise()
	{
		Motor.A.forward();
		Motor.B.backward();
	}
	public void turnCounterClockwise()
	{
		Motor.B.forward();
		Motor.A.backward();
	}
	public void setSpeed(int speed)
	{
		Motor.A.setSpeed(speed);
		Motor.B.setSpeed(speed);
	}

}
