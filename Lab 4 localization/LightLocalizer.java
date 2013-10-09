import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.Sound;
import lejos.nxt.comm.RConsole;

public class LightLocalizer {
	private Odometer odo;
	private TwoWheeledRobot robot;
	private ColorSensor ls;
	private Navigation nav;
	private int lightValue;//original light value that will be used for initial comparison
	private int lineCounter=0; 
	private boolean done = false;
	private double xTheta1,yTheta1,xTheta2,yTheta2;
	private final double LIGHT_SENSOR_DISTANCE_FROM_CENTER = 12.6;
	private final int LIGHT_DIFFERENCE_CONSTANT=45,LINE_VALUE_CONSTANT=525;// the light constant is a constant for difference the line constant is an absolute filter based on data taken 
	
	
	public LightLocalizer(Odometer odo, ColorSensor ls) {
		this.odo = odo;
		this.robot = odo.getTwoWheeledRobot();
		this.ls = ls;
		this.nav = new Navigation(odo,Motor.A,Motor.B);
		// turn on the light
		ls.setFloodlight(true);
	}
	
	
	/*
	 *the localization method turns the robot in a circle hitting four grid lines **robot must be close enough to origin
	 *when the robot hits the gridlines it latches the ange at each gridline
	 *uses these angles to calculate the x and y position
	 *then uses the x position to correct angle as it was very acurate   
	 */
	
	public void doLocalization() 
	{
		lightValue = ls.getNormalizedLightValue();
		nav.setSpeed(100);
		while (!done)
		{
			//period
			try { Thread.sleep(50); } catch (InterruptedException e) {}
			
			nav.turnClockwise();
			
			int light = ls.getNormalizedLightValue();
			//based on data taken lines are below the above line constant and have a minimum negative difference of above light constant 
			if(lightValue-light>(LIGHT_DIFFERENCE_CONSTANT)&& light<LINE_VALUE_CONSTANT)
			{
				double theTheta = odo.getTheta();
				++lineCounter;
				Sound.beep();
				//latching the four angles
				if(lineCounter == 1 )
				{
					xTheta1 = theTheta;
				}
				if(lineCounter ==2)
				{
					yTheta1 = theTheta;
				}
				if(lineCounter ==3)
				{
					xTheta2 = theTheta;
				}
				if(lineCounter ==4)
				{
					yTheta2= theTheta;
					done = true;//on the forth line the robot is done doing its dance 
				}
				try { Thread.sleep(50); } catch (InterruptedException e) {}//in if statement***sleep for a short time to make sure a line is only red once 
			}
			
		lightValue=light;	//update
		}
		nav.stopMotors();
		//calculation based on tutorial 
		double xArc = xTheta2-xTheta1;
		double yArc = yTheta2- yTheta1;
		double x = -LIGHT_SENSOR_DISTANCE_FROM_CENTER*Math.cos(Math.toRadians(yArc)/2);
		double y = -LIGHT_SENSOR_DISTANCE_FROM_CENTER*Math.cos(Math.toRadians(xArc)/2);
		double correctionAngle = 360-Math.toDegrees(Math.asin(-x/LIGHT_SENSOR_DISTANCE_FROM_CENTER));
		odo.setTheta(correctionAngle);
		odo.setX(x);
		odo.setY(y);
		//turn to 0,0
		nav.turnTo(nav.calOptimalAngle(nav.calDestAngle(0,0)));
		
		nav.setSpeed(50);//set speed slower to increase precision 
		//****note not using the navigation class as it was not accurate enough for the parameters of the lab
		//while the robot is not very close go forward
		while(Math.abs(odo.getX())>(.1) ||  Math.abs(odo.getY())>(.1))
		{
			nav.goStraight();
			if(odo.getX()>(1) || odo.getY()>(1))//if robot went way past just stop 
			{
				break;
			}
		}
		nav.stopMotors();//not at or verry close to (0,0) but theta is off
		
		boolean atZero=false;//atzero in terms of theta
		lightValue = ls.getNormalizedLightValue();;
		double light;//value that will be updates
		nav.setSpeed(50);//slow the speed for accuracy 
		//turn until the light sensor picks up the y axis 
		while(!atZero)
		{
			light = ls.getNormalizedLightValue();
			nav.turnCounterClockwise();
			if(lightValue-light>(LIGHT_DIFFERENCE_CONSTANT)&& light<LINE_VALUE_CONSTANT)//same light filter as before
			{
				nav.stopMotors();
				atZero= true;
			}
		
		}
		//now at (0,0,0) stop the robot 
		nav.stopMotors();
	}


}
