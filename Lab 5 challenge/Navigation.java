import lejos.*;
import lejos.nxt.LCD;
import lejos.nxt.Sound;
import lejos.nxt.comm.RConsole;
public class Navigation {
	
	private Odometer odo;
	private TwoWheeledRobot robo;
	private BlockRecognition blockDetector;
	
	private final double DISTANCE_THRESHOLD=.5;
	private final double ANGLE_THRESHOLD=.2;
	private double distance=0;
	public  boolean obsticleClose = false;
	
	
	public Navigation(Odometer odo, TwoWheeledRobot robot, BlockRecognition block)
	{
		this.odo=odo;
		this.robo= robot;
		this.blockDetector = block;
		robo.setForwardSpeed(200);
		robo.setRotationSpeed(100);
	}
	
	public void setObsticleClose(boolean answer)
	{
		obsticleClose = answer;
	}
	
	public void travelTo(double x , double y)
	{
		calDestAngle(x, y);
		turnBy(calOpitimalTurnAngle(calDestAngle(x, y)));
		while(keepGoing(odo.getX(), odo.getY(), x, y))
		{
			if(!obsticleClose)
			{
				robo.goForward();
			}
			else
			{
				robo.stopMotors();
				processObsticle();
				break;
			}
		}
		robo.stopMotors();
	}

	public void travelSetDistanceStraight(double d)
	{
		double startingX = odo.getX();
		double startingY = odo.getY();
		
		while ((Math.pow(odo.getX()-startingX, 2) + Math.pow(odo.getY()-startingY, 2)) < Math.pow(d, 2))
		{
			robo.goForward();
		}
		robo.stopMotors();
	}
	
	public boolean keepGoing(double xCurrent, double yCurrent, double xDest, double yDest)
	{
		double distanceFromPoint =Math.sqrt(((xDest-xCurrent)*(xDest-xCurrent))+((yDest-yCurrent)*(yDest-yCurrent)));
		
		if(distanceFromPoint<DISTANCE_THRESHOLD || this.distance<distanceFromPoint)//distance should keep getting smaller
		{
			this.distance=distanceFromPoint;
			return false;
		}
		else
		{
			this.distance=distanceFromPoint;
			return true;
		}
	}
	
	private void processObsticle()
	{
		int blockColor = blockDetector.getBlockColor(20);
		if(blockColor==1)//not blue 
		{
			//go around block
		}
		else//blue
		{
			//push block
		}
	}
	
	public void turnBy(double TurnAngle)
	{
		double startingAngle = odo.getTheta();
		double endAngle = odo.getTheta()+TurnAngle;
		double diff=calculateOpitimalDiff(Math.abs(endAngle-startingAngle));
	
		if(TurnAngle>0)
		{
			while(diff>ANGLE_THRESHOLD)
			{
				robo.rotateClockwise();
				diff=calculateOpitimalDiff(Math.abs(endAngle-odo.getTheta()));
			}
		}
		else
		{
			while(diff>ANGLE_THRESHOLD)
			{
				robo.rotateCounterClockwise();
				diff=calculateOpitimalDiff(Math.abs(endAngle-odo.getTheta()));
			}
		}
			robo.stopMotors();
	}
	
	private double calculateOpitimalDiff(double diff)
	{
		if (diff>180)
		{
			diff = 360-diff;
		}
		return diff;
	}

	public double calDestAngle(double xDest, double yDest)
	{
		double odomAngle = odo.getTheta();//current position
		double dAngle = 0 ;//angle to get to
		double x = odo.getX();
		double y = odo.getY();
		double changeY = yDest-y;
		double changeX = xDest-x;
		if(changeX==0)
		{
			if(changeY>0)
			{
				dAngle=0;
			}
			else
			{
				dAngle=Math.PI;
			}
		}
		else
		{
			if(changeY==0)
			{
				if(changeX>0)
				{
					dAngle=(Math.PI/2);
				}
				else
				{
					dAngle=-(Math.PI/2);
				}
			}
			else
			{
				if (changeY>0)
				{
					dAngle = Math.atan(( changeY/changeX));//math.atan returns radians
				}
				else
				{
					if (x<0)
					{
						dAngle = Math.atan(( changeY/changeX))-Math.PI;//math.atan returns radians
					}
					else
					{
						dAngle = Math.atan(( changeY/changeX))+Math.PI;
					}
				}
			}
		}
		
		return Math.toDegrees(dAngle)-odomAngle;
	}
	
	public double calOpitimalTurnAngle(double angle)
	{
		double op = angle;
		if(op <-180)
		{
			op += 360;
		}
		if (op > 180)
		{
			op -=360;
		}
		
		return op;
		
	}
}
