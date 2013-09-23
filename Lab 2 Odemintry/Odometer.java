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
	private final double rWheelRadius = 2.155;
	private final double lWheelRadius = 2.155;
	
	// odometer update period, in ms
	private static final long ODOMETER_PERIOD = 25;

	// lock object for mutual exclusion
	private Object lock;

	// default constructor
	public Odometer() {
		x = 0.0;
		y = 0.0;
		theta = 0.0;
		lLastTachoCount = 0.0;
		rLastTachoCount = 0.0;
		rightMotor.resetTachoCount();
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
			double tachoR = (rightMotor.getTachoCount()*converter)-rLastTachoCount;
			double tachoL= (leftMotor.getTachoCount()*converter)-lLastTachoCount;
			lLastTachoCount = (leftMotor.getTachoCount()*converter);
			rLastTachoCount = (rightMotor.getTachoCount()*converter);
			changeInDistance = ((tachoL*lWheelRadius)+(tachoR*rWheelRadius))/2;
			
			changeInAngle = ((tachoR*rWheelRadius)-(tachoL* lWheelRadius))/wheelDistance;
			
			
			synchronized (lock) 
			{
				
				x+=changeInDistance*Math.cos(theta+(changeInAngle/2));
				y+=changeInDistance*Math.sin(theta+(changeInAngle/2));
				theta+=changeInAngle;
				
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