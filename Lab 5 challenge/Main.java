import lejos.*;
import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.comm.RConsole;
public class Main {

	
	
	public static void main(String[] args) {
		ColorSensor cs = new ColorSensor(SensorPort.S1);
		RConsole.openBluetooth(10000);
		RConsole.println("connected");
		BlockRecognition br = new BlockRecognition(cs);
		Button.waitForAnyPress();
		
		
		while (Button.readButtons()!=Button.ID_ESCAPE)
		{
			LCD.drawString("Ready!", 0, 0);
			Button.waitForAnyPress();
			LCD.clearDisplay();
			//RConsole.println(Integer.toString(cs.getNormalizedLightValue()));
			int color = br.getBlockColor(10);
			if (color == 0)
			{LCD.drawString("Blue!", 0, 3);}
			else
			{LCD.drawString("Not Blue!", 0, 3);}
		}
		

	}

}
