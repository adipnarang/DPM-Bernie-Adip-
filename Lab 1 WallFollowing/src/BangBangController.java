import lejos.nxt.*;

public class BangBangController implements UltrasonicController{
	
	//constants
	private final int bandCenter, bandwith;
	private final int motorLow, motorHigh;
	private final int motorStraight = 300;
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.C;
	//constant used that dictates how much should be dictated as negligible 
	private final int BangBangConstant=5;
	private final int wallDistance=30;
	
	private int distance;
	private int currentLeftSpeed;
	
	
	public BangBangController(int bandCenter, int bandwith, int motorLow, int motorHigh) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwith = bandwith;
		this.motorLow = motorLow;
		this.motorHigh = motorHigh;
		leftMotor.setSpeed(motorStraight);
		rightMotor.setSpeed(motorStraight);
		leftMotor.forward();
		rightMotor.forward();
		currentLeftSpeed = 0;
		
	}
	
	@Override
	public void processUSData(int distance) {
		//setting variables that will be used
		this.distance = distance;
		int error = distance - wallDistance;
		int threshHold = BangBangConstant;
		
		//if the error is negligible go forward
		if(Math.abs(error)<threshHold)
		{
			leftMotor.setSpeed(motorStraight);
			rightMotor.setSpeed(motorStraight);
			leftMotor.forward();
			rightMotor.forward();
			
		}
		else
			if (error > 0 )//slow inside wheel to move farther from wall
			{
				
					//decrease rotation of outer wheel
					rightMotor.setSpeed(motorHigh+75);
					leftMotor.setSpeed(motorStraight);
					rightMotor.forward();
					leftMotor.forward();
				
			}
			else
				if(error < 0)//increase rotation of outside wheel to move closer to wall
				{
					
					//else//normal turn
					{
						rightMotor.setSpeed(motorStraight);
						leftMotor.setSpeed(motorHigh);
						rightMotor.forward();
						leftMotor.forward();
					}
				}
				else
				{
					//error == 0 ..... shouldnt realy happen... within threshold
				}
				//a
		if (distance < 10)//very close take drastic measures
		{
			rightMotor.rotate(-45);
			leftMotor.setSpeed(50);
			rightMotor.setSpeed(0);
			leftMotor.forward();
			rightMotor.forward();
		}
		// TODO: process a movement based on the us distance passed in (BANG-BANG style)
		
	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}
}
