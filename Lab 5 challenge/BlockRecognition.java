import lejos.*;
import lejos.nxt.ColorSensor;
import lejos.nxt.comm.RConsole;
public class BlockRecognition {
	int colorValue;
	ColorSensor cs;
	
	public BlockRecognition(ColorSensor cs)
	{
		this.cs=cs;
		cs.setFloodlight(true);
		cs.setFloodlight(cs.BLUE);
	}
	
	public int getBlockColor(int amountOfData )
	{
		
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int i=0;
		double total=0;
		while (i<amountOfData)
		{
			RConsole.println(Integer.toString(cs.getNormalizedLightValue()));
			total+=cs.getNormalizedLightValue();
			i++;
		}
		//cs.setFloodlight(false);
		double avg = total/amountOfData;
		if(avg<343 )
		{return 1;}//not blue
		else
		{return 0;}//blue
		
	}

}
