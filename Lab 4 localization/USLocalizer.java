import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.RConsole;

public class USLocalizer {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE };
	public static double ROTATION_SPEED = 30;
	private int EDGE_BOUNDRY = 50;//based on graphing the results of the us sensor 
	
	private int yD,xD,distance;
	private double yTheta,xTheta,wideAngle;
	
	private Odometer odo;
	private Navigation nav;
	private TwoWheeledRobot robot;
	private UltrasonicSensor us;
	private LocalizationType locType;
	
	
	//constructor
	public USLocalizer(Odometer odo, UltrasonicSensor us) 
	{
		this.odo = odo;
		this.robot = odo.getTwoWheeledRobot();
		this.us = us;
		this.nav= new Navigation(odo,Motor.A,Motor.B);
	}
	
	/*
	 * the method that does the localization
	 * the robot always start rotating clockwise but before it starts is uses the US to 
	 * determine if it is  closer to the near corner or the far corner
	 * ounce that has been determined the robot knows how to latch onto its angles.
	 * That is latching when the distance is falling or rising depending where it started
	 * at the end the method calculates the approximate heading of the robot as well as its approximate position in order to
	 * get close enough to the origin to be able to do the lightsensor localization   
	 */
	public void doLocalization() 
	{
		int SPEED = 150;//deffining speed so can be passed to motors
		
		int wall = getFilteredData();
		
		//farthest wall will ever be in the grid is about 130 so if the sensor senses something over 200 it obviously bogus here so wait until a valid measurement is recorded to start
		while(wall>200 ||wall == 0)
		{
			wall = getFilteredData();
		}
		
		//wall is close
		if(wall < EDGE_BOUNDRY)
		{
			locType = LocalizationType.RISING_EDGE;
		}
		//wall is far
		else
		{
			locType = LocalizationType.FALLING_EDGE;
		}
		
		
		if (locType == LocalizationType.RISING_EDGE) 
		{
			nav.setSpeed(SPEED);

			int distance =getFilteredData();
			while(distance<EDGE_BOUNDRY)//while the robot is closer to the wall then the angle latching boundary
			{
				xD = distance;//xD will be used later to calculate approximate x coordinate so update it until latch angle is reached
				nav.turnClockwise();
				distance = getFilteredData();
			}
						
			double thetaOne = odo.getTheta();//first latch angle
			nav.setSpeed(SPEED);
			while(distance>EDGE_BOUNDRY)//while the robot is far from the wall and has not reached the latching angle yet
			{
				nav.turnClockwise();
				distance = getFilteredData();
			}
			yD = distance;//will be used to calculate approximate y coordinate
			nav.stopMotors();
			
			double theataTwo = odo.getTheta();
		
			wideAngle = theataTwo-thetaOne;//angle between first latch and second latch (theta one will always be negative)
			double currentHeading = ((wideAngle)/2)+45;//use the above angle to calculate the current heading
			yTheta = 180 - currentHeading;//theta used to calculate approximate y position
			xTheta = yTheta;//idealy they should be equal if the latch angles where perfectly symmetrical and since this is just a rough estimate assume this to simplify geometry
			
			//*****note these x and y positions are in relation the the bottom corner
			odo.setTheta(currentHeading);//update the heading
			
		}
		else 
		{
			
			nav.setSpeed(SPEED);
			int distance =getFilteredData();
			//while the wall is still farther then latch angle distance
			while(distance>EDGE_BOUNDRY)
			{
				nav.turnClockwise();
				distance = getFilteredData();
			}
			
			yD = distance;//will be used to calculate approximate y coordinate
			double thetaOne = odo.getTheta();//get latch angle
			nav.stopMotors();
			nav.setSpeed(SPEED);
			
			while(distance <EDGE_BOUNDRY)//while still close to wall
			{
				nav.turnCounterClockwise();//in order to get back to falling edge territory
				distance = getFilteredData();
			}
			
			nav.setSpeed(SPEED);
			
			while(distance>EDGE_BOUNDRY)//while still farther then latch angle
			{
				nav.turnCounterClockwise();
				distance = getFilteredData();
				
			}
			
			xD = distance;//will be used to calculate approximate x coordinate
			nav.stopMotors();
			double thetaTwo = odo.getTheta();//get latch angle
			wideAngle = thetaOne+(360-thetaTwo);//calculate angle between latches
			double currentHeading = 360-((wideAngle/2)-45);//calculate current heading based on latching 
			xTheta = currentHeading-270;//using geometry to approximate x pos angle 
			yTheta = xTheta;//idealy used to simplyfy geometry 
			odo.setTheta(currentHeading);
			
		}
		
		//after both cases always do this 
		double x = xD*Math.cos(Math.toRadians(xTheta));//with relation to bottom corner 
		double y = yD*Math.cos(Math.toRadians(yTheta));
		odo.setX(x);
		odo.setY(y);
		double trackAngle =nav.calOptimalAngle(nav.calDestAngle(odo.getX(), odo.getY()+10)); 
		nav.turnTo(trackAngle);//turn to 0 
		nav.travelTo(24, 24);//in respect to bottom corner in order to get close enough to do light localization
		
	}
	
	/*
	 * the filter used to get info from the ultrasonic sensor 
	 */
	
	private int getFilteredData() 
	{
		int distance;
		
		// the period
		try { Thread.sleep(50); } catch (InterruptedException e) {}

		distance = us.getDistance();
		if(distance > 130)//farthest wall can possibly be 
		{
			return this.distance;//return same value as last time if value is bogus 	
		}
		else
		{
			//update distance 
			this.distance =	distance;	
			return distance;
		}
	}
	
}
