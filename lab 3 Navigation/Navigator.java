import lejos.*;
import lejos.addon.gps.RMCSentence;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;
public class Navigator extends Thread 
{
	private static final SensorPort usPort = SensorPort.S1;
	private static final UltrasonicSensor usSensor = new UltrasonicSensor(usPort);
	
	private Odometer odom= null ;
	private double []xCurrentDest= {60,30,30,60};
	private double []yCurrentDest={30,30,60,0};
	private int distance;
	private int tooClose = 15;
	private final int FILTER_OUT = 20;
	private int filterControl = 0;
	
	private boolean isNavigating= false;
	private final int TURNSPEED=100;
	private final int FORWARDSPEED=200;
	private final double RWHEELRADIUS = 2.155;
	private final double LWHEELRADIUS = 2.155;
	private final double WHEELDISTANCE = 2;
	private final double ANGLETHRESHOLD = .03;
	private final double POSITIONTHRESHOLD = 2.8;
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.C, sensorMotor = Motor.B;
	
	Object lock;
	//default constructor
	public Navigator()
	{
	}
	
	public Navigator(Odometer o)
	{
		odom = o;
	}
	
	public double[]  getXDest()
	{
		synchronized (lock) 
		{
			return this.xCurrentDest;
		}
		
	}
	
	public double[]  getYDest()
	{
		synchronized (lock) 
		{
			return this.yCurrentDest;
		}
		
	}
	
	public void run()
	{
			
			travelTo(60,60);
			travelTo(60,0);
			//travelTo(30,60);
			//travelTo(60,0);
			
	}
	
	// this method is the method that is called in the run of this thread it turns to the correct angle the starts moving forward. while moving forward it 
	// continuosly check the angle it is at compare to the the angle it is supposed to be at 
	// the method ends ounce the robot reaches a destination 
	public void travelTo(int x, int y)
	{	 
		LCD.drawString(Double.toString(calOptimalAngle(calDestAngle(x, y))), 0, 3);
		
		double trackAngle =calOptimalAngle(calDestAngle(x, y));
		turnTo(trackAngle);
		boolean hitwall=false;
		while((Math.abs(x-odom.getX())>POSITIONTHRESHOLD || Math.abs(y-odom.getY())>POSITIONTHRESHOLD))
		{
			
			isNavigating = true;
			if((usSensor.getDistance())<tooClose)
			{
				goAroundWall(usSensor.getDistance());
				hitwall= true;
			}
			else
			{
				if(hitwall)
				{
					turnTo(calOptimalAngle(calDestAngle(x, y)));
					hitwall = false;
				}
				
				goStraight();
				if(!isAngleOk(trackAngle));
				{
					//Sound.beepSequence();
					ajust(trackAngle);
				}
			
			}
		}
		
		stopMotors();
	}
	
	public boolean isNaviagating()
	{
		return isNavigating;
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
	public void stopMotors()
	{
		leftMotor.stop();
		rightMotor.stop();
	}
	
	// the turning method
	//the method takes as input the optimal angle that the robot needs to turn and exicutes the turn
	//so method should really be called "turnBy" **** note origin is considered where robot is facing when it begins its journey 
	public void turnTo(double theta)
	{
		//keeping track of original orientation so we know when the robot has reached the desired angle which = original + optimal 
		double originalAngle = odom.getTheta();
		
		if (theta > 0)//if optimal is positive this means it is turning clockwise
		{
			rightMotor.setSpeed(TURNSPEED);
			leftMotor.setSpeed(TURNSPEED);
			//as long as the angle does not equal close to the original angle plus the optimal turning angle keep turning
			while(Math.abs(odom.getTheta() - (originalAngle + theta))>.025)
			{
				leftMotor.forward();
				rightMotor.backward();	
			}
			stopMotors();
		}
		
		if (theta < 0)//if optimal is negative this means it is turning counterclockwise
		{
			Sound.buzz();
			rightMotor.setSpeed(TURNSPEED);
			leftMotor.setSpeed(TURNSPEED);
			//as  long as the angle does not equal close to the original angle plus the optimal turning angle keep turning
			while(Math.abs(odom.getTheta() - (originalAngle + theta))>.025)
			{
				rightMotor.forward();
				leftMotor.backward();
			}
			stopMotors();
		}
		
	}
	//methods that calculates the optimal angle as explained in lab tutorial notes exept converted to radians
	public double calOptimalAngle(double thetaDestination)
	{
		double op = thetaDestination;
		if(op <-Math.PI)
		{
			op += 2*Math.PI;
		}
		if (op > Math.PI)
		{
			op += -2*Math.PI;
		}
		return op;
		
	}
	// calculating the destination angle as specified in tutorial notes but with a slight twist in the math
	//**** note origin is considered where robot is facing when it begins its journey
	public double calDestAngle(int xDest,int yDest)
	{
		double odomAngle = odom.getTheta();
		double dAngle = 0 ;
		double x = odom.getX();
		double y = odom.getY();
		double changeY = (yDest-y);
		double changeX = (xDest-x);
		if (changeY>0)
		{
			dAngle = Math.atan(( changeX/changeY));//math.atan returns radians
		}
		else
		{
			if (x<0)
			{
				dAngle = Math.atan(( changeX/changeY))-Math.PI;//math.atan returns radians
			}
			else
			{
				dAngle = Math.atan(( changeX/changeY))+Math.PI;
			}
		}
		Sound.beep();
		LCD.drawString(Double.toString(dAngle), 0, 4);
		return dAngle-odomAngle;
	}
	
	//Adjusting methods that continuously adjusts the speed of the wheels while traveling to a point 
	private void ajust(double trackangle)
	{
		//turning on the inside abit if angle is too large
		if(odom.getTheta()>trackangle)
		{
			rightMotor.setSpeed(rightMotor.getSpeed()+5);
			rightMotor.forward();
		}
		//turning on the outside abit if angle is too large
		else
		{
			leftMotor.setSpeed(leftMotor.getSpeed()+5);
			leftMotor.forward();
		}
	}
	
	//checks if angle is within threshold
	private boolean isAngleOk(double goodAngle)
	{
		if (Math.abs(odom.getTheta()-goodAngle)<ANGLETHRESHOLD)
		{
			return true;
		}
		else 
		{
			return false;
		}
	}	
	
	
	
	
	
	
	
	
	
	
	public int returnUSData(int distance) 
	{
	
		
		// rudimentary filter
		if (distance == 255 && filterControl < FILTER_OUT) 
		{
			// bad value, do not set the distance var, however do increment the filter value
			filterControl ++;
		} 
			else if (distance == 255)
			{
				// true 255, therefore set distance to 255
				this.distance = distance;
			} 
				else 
				{
					// distance went below 255, therefore reset everything.
					filterControl = 0;
					this.distance = distance;
				}
		return this.distance;
		
	
	}
	
	public void goAroundWall(int distance)
	{
		if (this.distance > tooClose)
		{
			
		}
		else
		{
			LCD.drawInt(this.distance, 0, 5);
			stopMotors();
			turnTo(Math.PI/2);
			sensorMotor.rotate(-90);
			//sensorMotor.
			distance = (usSensor.getDistance());
			while (distance<200)
			{
				LCD.drawInt(this.distance, 0, 5);
				distance = (usSensor.getDistance());
				goStraight();
				
			}
			int i=0;
			while (i < 50) 
			{
				goStraight();
				i++;
				
			}
			//Sound.beep();
			stopMotors();
			turnTo(-Math.PI/2);
			sensorMotor.rotate(70);
			while (distance<200)
			{
				LCD.drawInt(this.distance, 0, 5);
				distance = (usSensor.getDistance());
				goStraight();
				
			}
			stopMotors();
			//turn sensor on an angle 
			//go around opsticle aka turn on bang bang until sensor reads 255 then turn nav back on 
		}
	}
	
	
	
	
	
	
	
	
	
}
