import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;

/* 
 * OdometryCorrection.java
 */

public class OdometryCorrection extends Thread {
	private static final long CORRECTION_PERIOD = 10;//10 was the original value 
	private Odometer odometer;
	private ColorSensor lightMan = new ColorSensor(SensorPort.S1);
	private final int minChangeToIndicateLine=12;//the change in line value that indicates a line
	private int lineCounter = 0 ;//to keep track of how many lines have been passed 
	private double sensorOffset = 4.5; // sensor offset as the sensor is not at the center of the wheels
	private double startOffset=0.0;
	
	// constructor
	public OdometryCorrection(Odometer odometer) 
	{
		this.odometer = odometer;
	}
	//getter to be able to return light value 
	public int getLightReading()
	{
		return lightMan.getNormalizedLightValue();
	}

	// run method (required for Thread)
	public void run() 
	{
		long correctionStart, correctionEnd;
		lightMan.setFloodlight(true);//helps sensor work better 
		int lightValue = lightMan.getNormalizedLightValue();//whe thread starts gets the light reading of floor when it starts so it can compare it to the next scanned light value
		int changeInLightValue;//will store the change in light value that will determine if a significant change has occured to indicate a line

		while (true) 
		{
			correctionStart = System.currentTimeMillis();
			changeInLightValue = lightValue - lightMan.getNormalizedLightValue();
			lightValue = lightMan.getNormalizedLightValue();
			if(Math.abs(changeInLightValue) > minChangeToIndicateLine)// this senses the change in colour on the floor . therefore for each line it senses two changes entering and exiting
			{
				lineCounter++;//increment the amount of times a change in color has been sensed 
				Sound.beep();//in order to hear if the sensor sensed the line
					if (lineCounter==2)//exiting first line
					{
						startOffset = odometer.getX();
						odometer.setY(0.0);
						odometer.setTheta(0.0);
						odometer.setX(15.24- sensorOffset);
					}
					if(lineCounter==4)//exiting first line
					{
						odometer.setY(0.0);
						odometer.setTheta(0.0);
						odometer.setX(45.72-sensorOffset);
					}
			
					if (lineCounter==6)//exiting second line
					{
						odometer.setX(60.96);
						odometer.setTheta(-Math.PI/2);
						odometer.setY(-15.24-sensorOffset);
					}
						
					if(lineCounter==8)//exiting third line
					{
						odometer.setX(60.96);
						odometer.setTheta(-Math.PI/2);
						odometer.setY(-45.72-sensorOffset);
					}
						
					if (lineCounter==10)//exiting fourth line
					{
						odometer.setTheta(-Math.PI);
						odometer.setY(-60.96);
						odometer.setX(45.72-sensorOffset);
					}
					if(lineCounter==12)//exiting fifth line
					{
						odometer.setTheta(-Math.PI);
						odometer.setY(-60.96);
						odometer.setX(60.96-sensorOffset);
					}
				
					if (lineCounter==14)//exiting sixth line
					{
						odometer.setX(0.0);
						odometer.setTheta(-3*Math.PI/2);
						odometer.setY(-45.72-sensorOffset);
					}
					if(lineCounter==15)//exiting seventh line
					{
						odometer.setX(startOffset);
						odometer.setTheta(-3*Math.PI/2);
						odometer.setY(-15.24-sensorOffset);
					}
					try
					{
						Thread.sleep(150);//sleep the thread when it detects a change in color or else the counter will increment an inconsistent amount of times per line. 
					}
					catch (Exception e)
					{
						//no reason why thread should not be able to sleep 
					}
					LCD.drawString(Integer.toString(lineCounter),0,3);// drawing line counter to assure that the incrementation of line counter is consistent 
				}//end of change in colourr if 
		
			// this ensure the odometry correction occurs only once every period
			correctionEnd = System.currentTimeMillis();
			if (correctionEnd - correctionStart < CORRECTION_PERIOD) 
			{
				try 
				{
					Thread.sleep(CORRECTION_PERIOD- (correctionEnd - correctionStart));
				} 
				catch (InterruptedException e) 
				{
					// there is nothing to be done here because it is not
					// expected that the odometry correction will be
					// interrupted by another thread
				}
			}
		}
	}
}