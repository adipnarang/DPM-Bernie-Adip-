import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;
//only here because odometer uses it 
public class TwoWheeledRobot {
	public static final double DEFAULT_LEFT_RADIUS = 2.1;
	public static final double DEFAULT_RIGHT_RADIUS = 2.1;
	public static final double DEFAULT_WIDTH = 16.5;
	private NXTRegulatedMotor leftMotor, rightMotor;
	private double leftRadius, rightRadius, width;
	private int forwardSpeed, rotationSpeed;
	
	public TwoWheeledRobot(NXTRegulatedMotor leftMotor,
						   NXTRegulatedMotor rightMotor,
						   double width,
						   double leftRadius,
						   double rightRadius) {
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.leftRadius = leftRadius;
		this.rightRadius = rightRadius;
		this.width = width;
	}
	
	public TwoWheeledRobot(NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor) {
		this(leftMotor, rightMotor, DEFAULT_WIDTH, DEFAULT_LEFT_RADIUS, DEFAULT_RIGHT_RADIUS);
	}
	
	public TwoWheeledRobot(NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor, double width) {
		this(leftMotor, rightMotor, width, DEFAULT_LEFT_RADIUS, DEFAULT_RIGHT_RADIUS);
	}
	
	// accessors
	public double getDisplacement() {
		return (leftMotor.getTachoCount() * leftRadius +
				rightMotor.getTachoCount() * rightRadius) *
				Math.PI / 360.0;
	}
	
	public double getHeading() {
		return (leftMotor.getTachoCount() * leftRadius -
				rightMotor.getTachoCount() * rightRadius) / width;
	}
	
	public void getDisplacementAndHeading(double [] data) {
		int leftTacho, rightTacho;
		leftTacho = leftMotor.getTachoCount();
		rightTacho = rightMotor.getTachoCount();
		
		data[0] = (leftTacho * leftRadius + rightTacho * rightRadius) *	Math.PI / 360.0;
		data[1] = (leftTacho * leftRadius - rightTacho * rightRadius) / width;
	}
	
	public void stopMotors()
	{
		leftMotor.stop();
		rightMotor.stop();
	}
	
	public void goForward()
	{
		leftMotor.setSpeed(forwardSpeed);
		rightMotor.setSpeed(forwardSpeed);
		
		leftMotor.forward();
		rightMotor.forward();
	}
	
	public void rotateCounterClockwise()
	{
		leftMotor.setSpeed(rotationSpeed);
		rightMotor.setSpeed(rotationSpeed);
		
		leftMotor.backward();
		rightMotor.forward();

	}

	public void rotateClockwise()
	{
		leftMotor.setSpeed(rotationSpeed);
		rightMotor.setSpeed(rotationSpeed);
		
		leftMotor.forward();
		rightMotor.backward();
	}
	
	// mutators
	public void setForwardSpeed(int speed)
	{
		forwardSpeed = speed;
	}
	
	public void setRotationSpeed(int speed)
	{
		rotationSpeed = speed;
	}

}
