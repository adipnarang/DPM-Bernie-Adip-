import lejos.*;
import lejos.nxt.UltrasonicSensor;
import lejos.util.Timer;
import lejos.util.TimerListener;
public class UltrasonicScanner implements TimerListener{
	
	UltrasonicSensor us;
	private Timer usTimer;
	public Navigation nav;
	
	private int period = 50;
	private final int TOO_CLOSE=7;
	
	
	public UltrasonicScanner(UltrasonicSensor us, Navigation nav)
	{
		this.nav=nav;
		this.us=us;
		usTimer = new Timer(period, this);
		usTimer.start();
	}

	private int getFilteredDistance()
	{
		int distance = us.getDistance();
		return distance; 
	}

	public void timedOut() 
	{
		if(getFilteredDistance()<TOO_CLOSE)
		{
			nav.setObsticleClose(true);
		}
	}
}
