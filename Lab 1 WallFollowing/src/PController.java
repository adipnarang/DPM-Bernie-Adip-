import lejos.nxt.*;

public class PController implements UltrasonicController {
	
	private final int bandCenter, bandwith;
	private final int motorStraight = 200, FILTER_OUT = 20;
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.C;
	private final int BangBangConstant=5;
	private final int wallDistance=50;
	private final int sweetspeed=300;
	
	private int distance;
	private int currentLeftSpeed;
	private int filterControl;
	
	public PController(int bandCenter, int bandwith) {
		//Default Constructor
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
		
		
		double topspeed=600;
		double error = wallDistance- this.distance;
		int Yinter= sweetspeed;
		double LerrorTop= wallDistance;
		double RerrorTop = -255;
		
		
		
		double La= (topspeed-sweetspeed)/LerrorTop;
		int Lspeed= (int)Math.round((La*error)+Yinter);
		if(Lspeed<0)
		{
			Lspeed=0;
		}
		
		double Ra = (topspeed-sweetspeed)/RerrorTop;
		int Rspeed = (int)Math.round((Ra*error)+Yinter);
		if(Rspeed<0)
		{
			Rspeed=0;
		}
		
		else
		{
			leftMotor.setSpeed(Lspeed);
			rightMotor.setSpeed(Rspeed);
		}
		if (error>(-15)&& error<0)
		{
			leftMotor.setSpeed(sweetspeed);
			rightMotor.setSpeed(sweetspeed);
		}
		leftMotor.forward();
		rightMotor.forward();
		
		
		
		
		
		
		// TODO: process a movement based on the us distance passed in (P style)
		
	}

	
	@Override
	public int readUSDistance() {
		return this.distance;
	}

}
