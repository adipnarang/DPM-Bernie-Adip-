import lejos.*;
import lejos.nxt.LCD;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;
import lejos.util.Timer;
import lejos.util.TimerListener;
public class ObsticleDetector extends Thread 
{
	private UltrasonicSensor us;
	private NXTRegulatedMotor usSpinner;
	private Navigator nav;
	private int tooClose = 20;
	private int filterControl = 0;
	private int distance;
	private int ORIGINPOSITION;
	private int ANGLEPOSITION;
	private int TIMEDELAY=500;
	
	private final int FILTER_OUT = 20;
	
	public ObsticleDetector(UltrasonicSensor Us, Navigator Nav, NXTRegulatedMotor m)
	{
		us=Us;
		this.nav = Nav;
		usSpinner = m;
		ORIGINPOSITION=m.getPosition();
		ANGLEPOSITION = m.getPosition()-45;
	}
	
	public void run()
	{
		while(true)
		{
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			
			//e.printStackTrace();
		}
		processUSData(us.getDistance());// TODO Auto-generated method stub
		}
	}
	
	private boolean isSensorStraight()
	{
		if(usSpinner.getPosition()==ORIGINPOSITION)
		return true;
		else
			return false;
	}
	
	private void turnSensorToOriginPosition()
	{
		usSpinner.rotateTo(ORIGINPOSITION, true);
	}
	
	private void turnSensorToAngeledPosition()
	{
		usSpinner.rotateTo(ANGLEPOSITION, true);
	}
	
	public void processUSData(int distance) 
	{
	
		LCD.drawInt(this.distance, 0, 5);
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
		
		if (this.distance > tooClose && this.distance<255)
		{
			if(!isSensorStraight())
			{
				turnSensorToOriginPosition();
				
			}
			else
			{
			//make sure sensor is pointing forward 
			//do nothing its fine
			}
		}
		else
		{
			//Sound.beepSequenceUp();
			if(isSensorStraight())
			{
				turnSensorToAngeledPosition();
			}
			
			if (nav.isNaviagating())//put if nav isAlive
			{
				//Sound.beepSequenceUp();
				nav.run=false;
				//go aroundwall
				
			}
			
			//turn sensor on an angle 
			//go around opsticle aka turn on bang bang until sensor reads 255 then turn nav back on 
		}
	
	}
	
	private void bangBangFollower()
	{
		
	}
	

}
