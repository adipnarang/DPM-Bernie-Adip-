import lejos.*;
import lejos.nxt.UltrasonicSensor;
public class ObsticleDetector extends Thread 
{
	private UltrasonicSensor us;
	private int filterControl = 0;
	private int distance;
	
	private final int FILTER_OUT = 20;
	
	public ObsticleDetector(UltrasonicSensor Us)
	{
		us=Us;
	}
	
	public void run()
	{
		
	}
	
	public void processUSData(int distance) 
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
		if (this.distance > 100)
		{
			//do nothing its fine
		}
		else
		{
			if (true)//put if nav isAlive
			{
				//stopnav
				
				
				//turn sensor on an angle 
				//go around opsticle
			}
		}
	}
	

}
