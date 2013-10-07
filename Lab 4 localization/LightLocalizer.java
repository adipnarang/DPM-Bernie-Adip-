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
	private int lightValue;
	private int lineCounter=0; 
	private boolean done = false;
	private double xTheta1,yTheta1,xTheta2,yTheta2;
	private final double LIGHT_SENSOR_DISTANCE_FROM_CENTER = 14;
	private final int LIGHT_CONSTANT=40,LINE_CONSTANT=515;
	
	
	public LightLocalizer(Odometer odo, ColorSensor ls) {
		this.odo = odo;
		this.robot = odo.getTwoWheeledRobot();
		this.ls = ls;
		this.nav = new Navigation(odo,Motor.A,Motor.B);
		// turn on the light
		ls.setFloodlight(true);
	}
	
	public void doLocalization() 
	{
		lightValue = ls.getNormalizedLightValue();
		Motor.A.setSpeed(100);
		Motor.B.setSpeed(100);
		while (!done)
		{
			try { Thread.sleep(50); } catch (InterruptedException e) {}
			Motor.A.forward();
			Motor.B.backward();
			int l = ls.getNormalizedLightValue();
			RConsole.println(Integer.toString(ls.getNormalizedLightValue()));
			if(lightValue-l>(LIGHT_CONSTANT)&& l<LINE_CONSTANT)
			{
				double theTheta = odo.getTheta();
				++lineCounter;
				Sound.beep();
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
					done = true;
				}
			}
			
		lightValue=l;	
		}
		Motor.A.stop();
		Motor.B.stop();
		double xArc = xTheta2-xTheta1;
		double yArc = yTheta2- yTheta1;
		double x = -LIGHT_SENSOR_DISTANCE_FROM_CENTER*Math.cos(Math.toRadians(yArc)/2);
		double y = -LIGHT_SENSOR_DISTANCE_FROM_CENTER*Math.cos(Math.toRadians(xArc)/2);
		double correctionAngle = 360-Math.toDegrees(Math.asin(-x/LIGHT_SENSOR_DISTANCE_FROM_CENTER));
		odo.setTheta(correctionAngle);
		odo.setX(x);
		odo.setY(y);
		nav.turnTo(nav.calOptimalAngle(nav.calDestAngle(0,0)));
		
		double distanceToTravel =  Math.sqrt((x*x)+(y*y));
		nav.travelSetDistanceStraight(distanceToTravel);
		nav.turnTo(-Math.toRadians(odo.getTheta()));//nav.calDestAngle(odo.getX(), odo.getY()+10))-Math.PI/2);
		
		// drive to location listed in tutorial
		// start rotating and clock all 4 gridlines
		// do trig to compute (0,0) and 0 degrees
		// when done travel to (0,0) and turn to 0 degrees
	}


}
