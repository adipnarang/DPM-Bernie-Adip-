import lejos.nxt.*;

public class BangBangController implements UltrasonicController{
	private final int bandCenter, bandwith;
	private final int motorLow, motorHigh;
	private final int motorStraight = 100;
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.C;
	private final int BangBangConstant=3;
	private final int wallDistance=30;
	
	private int distance;
	private int currentLeftSpeed;
	private char outerwheel;
	
	public BangBangController(int bandCenter, int bandwith, int motorLow, int motorHigh,char outerwheel) {
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
		this.outerwheel=outerwheel;
	}
	
	@Override
	public void processUSData(int distance) {
		this.distance = distance;
		int error = distance - wallDistance;
		int threshHold = BangBangConstant;
		if(Math.abs(error)<threshHold)
		{
			leftMotor.setSpeed(motorStraight);
			rightMotor.setSpeed(motorStraight);
			leftMotor.forward();
			rightMotor.forward();
			//do nothing its negligible
		}
		else
			if (error > 0 )
			{
				if (distance < 0)
				
				{
					//decrease rotation of outer wheel
					leftMotor.setSpeed(motorHigh);
					rightMotor.setSpeed(motorStraight);
					leftMotor.forward();
					rightMotor.forward();
				}
			}
			else
				if(error < 0)
				{
					
					{
						//increase rotation of outside wheel
						leftMotor.setSpeed(motorStraight);
						rightMotor.setSpeed(motorHigh);
						leftMotor.forward();
						rightMotor.forward();
					}
				}
				else
				{
					//error == 0 ..... shouldnt realy happen
				}
				//a
		
		// TODO: process a movement based on the us distance passed in (BANG-BANG style)
		
	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}
}