import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;

/* 
 * OdometryCorrection.java
 */

public class OdometryCorrection extends Thread {
	private static final long CORRECTION_PERIOD = 10;//10
	private Odometer odometer;
	private ColorSensor lightMan = new ColorSensor(SensorPort.S1);
	private final int minChangeToIndicateLine=15;
	private int lineCounter = 0 ;
	private double sensorOffset = 4.5; // sensor offset
	//private int a=0;
	// constructor
	public OdometryCorrection(Odometer odometer) 
	{
		this.odometer = odometer;
	}
	public int getLightReading()
	{
		return lightMan.getNormalizedLightValue();
	}

	// run method (required for Thread)
	public void run() 
	{
		long correctionStart, correctionEnd;
		lightMan.setFloodlight(true);
		int lightValue = lightMan.getNormalizedLightValue();
		int changeInLightValue;

		while (true) 
		{
			correctionStart = System.currentTimeMillis();
			//LCD.drawString( Integer.toString( lightMan.getNormalizedLightValue()) ,0,3);
			// put your correction code here
			changeInLightValue = lightValue - lightMan.getNormalizedLightValue();
			//LCD.drawString( Integer.toString(changeInLightValue ) ,0,4);
			lightValue = lightMan.getNormalizedLightValue();
			if(Math.abs(changeInLightValue) > minChangeToIndicateLine)
			{
				
				lineCounter++;
				
				
				Sound.beep();
				
				
					//first two lines adjust x
					if (lineCounter==2)
					{
						odometer.setY(0.0);
					odometer.setTheta(0.0);
						odometer.setX(15.24- sensorOffset);
						
					}
					if(lineCounter==4)
					{
						odometer.setY(0.0);
						odometer.setTheta(0.0);
						odometer.setX(45.72-sensorOffset);
						
					}
			
					
					if (lineCounter==6)
					{
						odometer.setX(60.96);
						odometer.setTheta(-Math.PI/2);
						odometer.setY(-15.24-sensorOffset);
					}
						
					if(lineCounter==8)
					{
						odometer.setX(60.96);
						odometer.setTheta(-Math.PI/2);
						odometer.setY(-45.72-sensorOffset);
						
					}
					//next two lines adjust y
				
				
				
					//coming back two lines adjust x
					
					if (lineCounter==10)
					{
						odometer.setTheta(-Math.PI);
						odometer.setY(-60.96);
						odometer.setX(45.72-sensorOffset);
						
					}
					if(lineCounter==12)
					{
						odometer.setTheta(-Math.PI);
						odometer.setY(-60.96);
						odometer.setX(60.96-sensorOffset);
						
					}
				
				
					
					//coming back two lines adjust y
					if (lineCounter==14)
					{
						odometer.setX(0.0);
						odometer.setTheta(-3*Math.PI/2);
						odometer.setY(-45.72-sensorOffset);
						
						
					}
					if(lineCounter==15)
					{
						odometer.setX(0.0);
						odometer.setTheta(-3*Math.PI/2);
						odometer.setY(-15.24-sensorOffset);
					}
					try
					{
						Thread.sleep(150);//300
					}
					catch (Exception e)
					{
						
					}
					LCD.drawString(Integer.toString(lineCounter),0,3);
					
				
			
			}
		
			
			
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