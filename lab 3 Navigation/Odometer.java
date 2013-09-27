import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.Tachometer;

/*
 * Odometer.java
 */

public class Odometer extends Thread {
	// robot position
	private double x, y, theta,rLastTachoCount,lLastTachoCount,changeInDistance,changeInAngle;
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.C;
	private final double wheelDistance = 15.1;//centre to centre
	private final double rWheelRadius = 2.15; 	
	private final double lWheelRadius = 2.15;
	
	// odometer update period, in ms
	private static final long ODOMETER_PERIOD = 25;

	// lock object for mutual exclusion
	private Object lock;

	// default constructor
	public Odometer() {
		//starts at origin therefore all variables set to 0
		x = 0.0; 
		y = 0.0;
		theta = 0.0;
		lLastTachoCount = 0.0;//always starts at origin always starts at 0 
		rLastTachoCount = 0.0;
		rightMotor.resetTachoCount();//resetting tachometer to make sure it starts at 0
		leftMotor.resetTachoCount();
		lock = new Object();
	}

	// run method (required for Thread)
	public void run() 
	{
		long updateStart, updateEnd;
		double converter = Math.PI/180.0;// used to comvert to radians

		while (true) 
		{
			updateStart = System.currentTimeMillis();
			// put (some of) your odometer code here
			double tachoR = (rightMotor.getTachoCount()*converter)-rLastTachoCount;//current count - last count in order to get change in wheel rotation 
			double tachoL= (leftMotor.getTachoCount()*converter)-lLastTachoCount;//current count - last count in order to get change in wheel rotation
			lLastTachoCount = (leftMotor.getTachoCount()*converter);//setting last count to the current count for next iteration 
			rLastTachoCount = (rightMotor.getTachoCount()*converter);//setting last count to the current count for next iteration 
			changeInDistance = ((tachoL*lWheelRadius)+(tachoR*rWheelRadius))/2;
			
			changeInAngle = ((tachoR*rWheelRadius)-(tachoL* lWheelRadius))/wheelDistance;
			
			
			synchronized (lock) 
			{
				//recursive relationship simply done with += and setting x,y and theta to 0 to start
				y+=changeInDistance*Math.cos(theta+(changeInAngle/2));
				x+=(changeInDistance*Math.sin(theta+(changeInAngle/2)));
				theta+=-(changeInAngle);
				if (theta > 2*Math.PI ||theta < -2*Math.PI )
				{
				theta = 0;	
				}
				// don't use the variables x, y, or theta anywhere but here!
			}
			// this ensures that the odometer only runs once every period
			updateEnd = System.currentTimeMillis();
			if (updateEnd - updateStart < ODOMETER_PERIOD) {
				try {
					Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometer will be interrupted by
					// another thread
				}
			}
		}
	}

	// accessors
	public void getPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				position[0] = x;
			if (update[1])
				position[1] = y;
			if (update[2])
				position[2] = theta;
		}
	}

	public double getX() {
		double result;

		synchronized (lock) {
			result = x;
		}

		return result;
	}

	public double getY() {
		double result;

		synchronized (lock) {
			result = y;
		}

		return result;
	}

	public double getTheta() {
		double result;

		synchronized (lock) {
			result = theta;
		}

		return result;
	}

	// mutators
	public void setPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				x = position[0];
			if (update[1])
				y = position[1];
			if (update[2])
				theta = position[2];
		}
	}

	public void setX(double x) {
		synchronized (lock) {
			this.x = x;
		}
	}

	public void setY(double y) {
		synchronized (lock) {
			this.y = y;
		}
	}

	public void setTheta(double theta) {
		synchronized (lock) {
			this.theta = theta;
		}
	}
}