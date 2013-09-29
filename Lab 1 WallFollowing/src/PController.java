import lejos.nxt.*;
import lejos.nxt.addon.LMotor;

public class PController implements UltrasonicController {
	//all constants set here 
	private final int bandCenter, bandwith;
	private final int motorStraight = 200, FILTER_OUT = 20;
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.C;
	//desired wall Distance 
	private final int wallDistance=37;
	//the speed at which robot will be going straight 
	private final int sweetspeed=300;
	//top speed for wheel that will make turns possible . used to calculate slope 
	private final double topspeed=500;
	
	//intercept will always be the sweetspeed as the two linear equation intercept at the sweetspeed when error =0 aka y inetercept
	private final int Yinter= sweetspeed;
	//the max error for the left wheel linear function is the wall distance, when the robot is touching the wall 
	private final double LerrorTop= wallDistance;
	// top error for the right wheel linear function is the max distance the sensor can sense 
	private final double RerrorTop = -255;
	//common variables that need to be access 
	private int distance;
	private int currentLeftSpeed;//not used
	private int filterControl;
	
	//Pcontroller constructor 
	public PController(int bandCenter, int bandwith) {
		//default values 
		this.bandCenter = bandCenter;
		this.bandwith = bandwith;
		leftMotor.setSpeed(motorStraight);
		rightMotor.setSpeed(motorStraight);
		leftMotor.forward();
		rightMotor.forward();
		currentLeftSpeed = 0;
		filterControl = 0;
	}
	
	@Override
	
	public void processUSData(int distance) 
	{
		// rudimentary filter
		if (distance == 255 && filterControl < FILTER_OUT) {
			// bad value, do not set the distance var, however do increment the filter value
			filterControl ++;
		} else if (distance == 255){
			// true 255, therefore set distance to 255
			this.distance = distance;
		} else {
			// distance went below 255, therefore reset everything.
			filterControl = 0;
			this.distance = distance;
		}
		
		//updated as it needs to go through filter
		double error = wallDistance- this.distance;
		
		
		//slope of linear function based on error related to the left wheel
		double La= (topspeed-sweetspeed)/LerrorTop;
		//Execution of linear function
		int Lspeed= (int)Math.round((La*error)+Yinter-20);
		//if speed is negative set it to a low number(smoother turns) as it would seem that negative values get ignored when they are passed to the motorregulator setspeed method
		if(Lspeed<0)
		{
			Lspeed=20;
		
		}
		//slope of linear function based on error related to the right wheel
		double Ra = (topspeed-sweetspeed)/RerrorTop;
		//Execution of linear function
		int Rspeed = (int)Math.round((Ra*error)+Yinter);
		//if speed is negative set it to zero (sharper turns) as it would seem that negative values get ignored when they are passed to the motorregulator setspeed method
		if(Rspeed<0)
		{
			Rspeed=0;
		}
		//setting the speeds
		else
		{
			leftMotor.setSpeed(Lspeed);
			rightMotor.setSpeed(Rspeed);
		}
		//Override to forward speed if error is negligible .
		if (error>(-10)&& error<0)//this means in the robot is within a certain threshold of the wall it will go straight. 
		{
			leftMotor.setSpeed(sweetspeed);
			rightMotor.setSpeed(sweetspeed);
		}
		
		//Special case. if robot is very close to wall make an emergency maneuver that turns the robot rapidly
		if (distance<18)
		{
			rightMotor.rotate(-30);
			leftMotor.setSpeed(25);
			rightMotor.setSpeed(0);
			leftMotor.forward();
			rightMotor.forward();
			
		}
		//move motors forward 	
		leftMotor.forward();
		rightMotor.forward();
		
	}

	
	@Override
	public int readUSDistance() {
		return this.distance;
	}

}
