import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.NXTLightSensor;
import lejos.hardware.sensor.NXTUltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

public class Main2 {
	void arching_left(){
		
	}
	static boolean turnRight;				//All objects initializations that are necessary for whole program
	final static float LEFT_LINE_THRESHOLD=(float) 0.28;
	final static float RIGHT_LINE_THRESHOLD=(float)0.5;
	final static float DISTANCE_THRESHOLD=1;
	final static float RAGE_THRESHOLD=(float) 0.1;
	final static float DIFFERENCE_THRESHOLD=(float) 0.1;
	final static int START_DELAY=5000;
	public static void main(String[] args) {
		LCD.drawString("Initalizing.", 0, 4);
		Motors leftMotor = new Motors("Left");
		Motors rightMotor= new Motors("Right");		
		LCD.drawChar('.', 12, 4);
		@SuppressWarnings("resource")		//Sensors initialization (Supposed to give resource leak exception -- Suppressed)
		SampleProvider leftUSSensor = new NXTUltrasonicSensor(LocalEV3.get().getPort("S2")).getDistanceMode();
		@SuppressWarnings("resource")
		SampleProvider rightUSSensor= new NXTUltrasonicSensor(LocalEV3.get().getPort("S3")).getDistanceMode();
		@SuppressWarnings("resource")
		SampleProvider leftLineSensor = new EV3ColorSensor(LocalEV3.get().getPort("S1")).getRedMode();
		@SuppressWarnings("resource")
		SampleProvider rightLineSensor= new NXTLightSensor(LocalEV3.get().getPort("S4")).getRedMode();
		LCD.drawChar('.', 13, 4);
		boolean goOn=true;
		boolean running=false;
		float[] leftDistance={Float.POSITIVE_INFINITY};
		float[] rightDistance={Float.POSITIVE_INFINITY};
		float[] leftBrightness={0};
		float[] rightBrightness={0};
		Sound.beepSequenceUp();
		LCD.clearDisplay();
		while (goOn)		//Main infinite loop of the program
		{
			while(Button.ENTER.isUp())	//Pick direction of turning after being deployed and waiting for 'STARt_DELAY'
			{
				if(Button.LEFT.isDown())
				{
					turnRight=false;
				}
				else if(Button.RIGHT.isDown())
				{
					turnRight=true;
				}else if(Button.ESCAPE.isDown())
				{
					goOn=false;
					break;
				}
				if(turnRight) LCD.drawString("Turning Right", 0, 4);else LCD.drawString("TurningLeft ", 0, 4);
				Delay.msDelay(20);
			}
			LCD.clearDisplay();
			if(goOn)		//checks if it should its wait for start or go straight to the end
				{
				Sound.beep();
				Delay.msDelay(START_DELAY);
				running=true;
				}
			while(Button.DOWN.isUp()&&goOn)
			{
				if(Button.ESCAPE.isDown())goOn=false;			//Checks if program should go to the end sequence or is it only supposed to stop motors and go to direction picker
				leftUSSensor.fetchSample(leftDistance, 0);		//Provides samples of data for use in this evaluation cycle (ensures consistent value during evaluation				
				rightUSSensor.fetchSample(rightDistance, 0);	//also reduces amount of external IO 
				leftLineSensor.fetchSample(leftBrightness, 0);
				rightLineSensor.fetchSample(rightBrightness, 0);
			
				if(leftDistance[0]<RAGE_THRESHOLD && rightDistance[0]<RAGE_THRESHOLD)	//Rage- goes  straight as long as it sees the enemy robot in front of itself within 'RAGE_THRESHOLD'
				{
					LCD.drawString("RAGE!!!       ", 0, 4);
					leftMotor.forward();
					rightMotor.forward();
				}else if(leftBrightness[0]>LEFT_LINE_THRESHOLD ||rightBrightness[0]>RIGHT_LINE_THRESHOLD)	//Line avoidance mechanism (tends to turn left when in doubt)
				{																							//CAUTION!!! LEFT_LINE_THRESHOLD and RIGHT_LINE_THRESHOLD might and probably will be different 
					LCD.drawString("LINE          ", 0, 4);
					if(leftBrightness[0]>LEFT_LINE_THRESHOLD)turnRight=true;else turnRight=false;
					if(turnRight)
					{
						leftMotor.stop();
						rightMotor.backward();
					}else
					{
						leftMotor.backward();
						rightMotor.stop();
					}
				}else if(leftDistance[0]<DISTANCE_THRESHOLD && rightDistance[0]<DISTANCE_THRESHOLD && Math.abs(leftDistance[0]-rightDistance[0])<DIFFERENCE_THRESHOLD)
				{
					LCD.drawString("GOING STRAIGHT", 0, 4);		//Identic to Rage except priority and threshold which is defined by 'DISTANCE_THRESHOLD'
					leftMotor.forward();
					rightMotor.forward();
				}else if(rightDistance[0]<DISTANCE_THRESHOLD && rightDistance[0]<leftDistance[0])	//"Gentle" turn toward an opponent
				{
					//float turnRadius=leftDistance[0]*500;
					LCD.drawString("ARCHING LEFT  ", 0, 4);
					turnRight=false;
					leftMotor.stop(true);
					rightMotor.forward();
				}else if(leftDistance[0]<DISTANCE_THRESHOLD && leftDistance[0]<rightDistance[0])
				{
					//float turnRadius=leftDistance[0]*500;
					LCD.drawString("ARCHING RIGHT ", 0, 4);
					turnRight=true;
					leftMotor.forward();
					rightMotor.stop(true);
				}else
				{
					LCD.drawString("SCANNING      ", 0, 4);		//Runs scanning sequence, direction based on 'turnRight'
					if(turnRight)
					{
						leftMotor.forward();
						rightMotor.backward();
					}else
					{	
						leftMotor.backward();
						rightMotor.forward();
					}
				}
				LCD.drawString(Float.toString(leftBrightness[0]), 0, 0);  	//Displays useful debug info
				LCD.drawString(Float.toString(rightBrightness[0]), 0, 1);
				LCD.drawString(Float.toString(leftDistance[0]), 0, 2);
				LCD.drawString(Float.toString(rightDistance[0]), 0, 3);
				LCD.drawString(Float.toString(Math.abs(leftDistance[0]-rightDistance[0])), 0, 5);
				Delay.msDelay(25);
			}
			if(running) //Stops motors after leaving navigation loop
			{
				LCD.clearDisplay();
				LCD.drawString("Stopping", 0, 4);
				leftMotor.stop(true);
				rightMotor.stop();
				running=false;
				if(goOn)Sound.buzz();	//Checks if it's going to make "Good bye!" sound and if not plays "Engines stopped" sound
			}
		}
		LCD.clearDisplay();
		LCD.drawString("Bye!", 0, 4);
		Sound.beepSequence();
	}
}