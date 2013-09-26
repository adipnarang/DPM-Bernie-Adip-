import lejos.*;
import lejos.addon.gps.RMCSentence;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
public class Navigator 
{
	private Odometer odom= null ;
	
	private final int TURNSPEED=100;
	private final int FORWARDSPEED=200;
	private final double RWHEELRADIUS = 2.155;
	private final double LWHEELRADIUS = 2.155;
	private final double WHEELDISTANCE = 15;
	private final double ANGLETHRESHOLD = .1;
	private final int POSITIONTHRESHOLD = 5;
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.C;
	
	//default constructor
	public Navigator()
	{
	}
	
	public Navigator(Odometer o)
	{
		odom = o;
	}
	
	
	public void travelTo(int x, int y)
	{	 
		LCD.drawString(Double.toString(calOptimalAngle(calDestAngle(x, y))), 0, 3);
		Button.waitForAnyPress();
		turnTo(calOptimalAngle(calDestAngle(x, y))); 
		while(Math.abs(x-odom.getX())>POSITIONTHRESHOLD && Math.abs(y-odom.getY())>POSITIONTHRESHOLD)
		{
			goStraight();
		}
		stop();
	}
	//method simply tells robot to go straight
	public void goStraight()
	{
		rightMotor.setSpeed(FORWARDSPEED);
		leftMotor.setSpeed(FORWARDSPEED);
		leftMotor.forward();
		rightMotor.forward();
	}
	//method that makes wheels stop
	public void stop()
	{
		leftMotor.stop();
		rightMotor.stop();
	}
	// the turning method
	//the method takes as input the optimal angle that the robot needs to turn and exicutes the turn
	//so method should really be called "turnBy" 
	public void turnTo(double theta)
	{
		//keeping track of original orientation so we know when the robot has reached the desired angle which = original + optimal 
		double originalAngle = odom.getTheta();
		if (theta > 0)//if optimal is positive this means it is turning clockwise
		{
			rightMotor.setSpeed(TURNSPEED);
			leftMotor.setSpeed(TURNSPEED);
			//as long as the angle does not equal close to the original angle plus the optimal turning angle keep turning
			while(Math.abs(odom.getTheta() - (originalAngle + theta))>ANGLETHRESHOLD)
			{
				leftMotor.forward();
				rightMotor.backward();
				
			}
			stop();
		}
		if (theta < 0)//if optimal is negative this means it is turning counterclockwise
		{
			rightMotor.setSpeed(TURNSPEED);
			leftMotor.setSpeed(TURNSPEED);
			//as  long as the angle does not equal close to the original angle plus the optimal turning angle keep turning
			while(Math.abs(odom.getTheta() - (originalAngle + theta))>ANGLETHRESHOLD)
			{
				rightMotor.forward();
				leftMotor.backward();
			}
			stop();
		}
	}
	//methods that calculates the optimal angle as explained in lab tutorial notes exept converted to radians
	public double calOptimalAngle(double thetaDestination)
	{
		double op = thetaDestination-odom.getTheta();
		if(op <-3.14)
		{
			op += 6.28;
		}
		if (op > 3.14 )
		{
			op += -6.28;
		}
		return op;
		
	}
	// calculating the destination angle as specified in tutorial notes
	public double calDestAngle(int xDest,int yDest)
	{
		double x=odom.getX();
		double y = odom.getY();
		double dAngle = ( Math.atan((yDest-y)/(xDest-x)));//math.atan returns radians
		if(x<0 && y>0)
		{
			dAngle+= Math.PI;
		}
		if(x<0 && y<0)
		{
			dAngle-= Math.PI;
		}
		return dAngle;
	}
	
	
	//used to calculate linear velocity of robot
	private double calculateLinearVelocity()
	{
		return ((rightMotor.getSpeed()*RWHEELRADIUS)+(leftMotor.getSpeed()*LWHEELRADIUS)/2);
	}
		
}
