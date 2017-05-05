import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.sensor.NXTUltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;



public class Main extends Motors{
public Main(String side) {
		super(side);
		// TODO Auto-generated constructor stub
	}

	//	static final int speed=600;
	
	static float DISTANCE_TRESSHOLD=(float) 0.7;
	
	public static void main(String[] args) {
		
		Motors LeftMotor = new Motors("Left");
		Motors RightMotor= new Motors("Right");
		NXTUltrasonicSensor nxtUltrasonicSensorLeft = new NXTUltrasonicSensor(LocalEV3.get().getPort("S3"));
		SampleProvider LeftUSSensor = nxtUltrasonicSensorLeft.getDistanceMode();
		NXTUltrasonicSensor nxtUltrasonicSensorRight = new NXTUltrasonicSensor(LocalEV3.get().getPort("S2"));
		SampleProvider RightUSSensor = nxtUltrasonicSensorRight.getDistanceMode();
		float[] leftDistance = new float[1];
		float[] rightDistance = new float[1];
		Sound.beepSequenceUp();
		while(Button.ENTER.isUp());
		Sound.twoBeeps();
		while(Button.ESCAPE.isUp())
		{
			LeftUSSensor.fetchSample(leftDistance, 0);
			RightUSSensor.fetchSample(rightDistance, 0);
			if (leftDistance[0]<DISTANCE_TRESSHOLD)RightMotor.forward();else if (rightDistance[0]<DISTANCE_TRESSHOLD)RightMotor.stop();
			if (rightDistance[0]<DISTANCE_TRESSHOLD)LeftMotor.forward();else if (leftDistance[0]<DISTANCE_TRESSHOLD)LeftMotor.stop();
			if (leftDistance[0]>1&&rightDistance[0]>DISTANCE_TRESSHOLD)
			{
				LeftMotor.forward();
				RightMotor.backward();
			}
		}
		LeftMotor.stop(true);
		RightMotor.stop();
		

		LeftMotor.close();
		RightMotor.close();
		
		nxtUltrasonicSensorLeft.close();
		nxtUltrasonicSensorRight.close();
		
		Sound.beepSequence();
	}
}
