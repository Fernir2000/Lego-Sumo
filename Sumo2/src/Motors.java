
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.RegulatedMotor;

public class Motors {
	private RegulatedMotor motor1;
	private RegulatedMotor motor2;
	
	public Motors(String side)
	{
		if (side=="Left")
		{
			motor1 = new EV3LargeRegulatedMotor(MotorPort.B);
			motor2 = new NXTRegulatedMotor(MotorPort.A);
		}else if (side=="Right")
		{
			motor1 = new EV3LargeRegulatedMotor(MotorPort.C);
			motor2 = new NXTRegulatedMotor(MotorPort.D);
		}
		motor1.synchronizeWith(new RegulatedMotor[]{motor2});
		int speed =(int) (Math.min(motor1.getMaxSpeed(), motor2.getMaxSpeed())*0.95);
		motor1.setSpeed(speed);
		motor2.setSpeed(speed);
		motor1.setAcceleration(speed*4);
		motor2.setAcceleration(speed*4);
	}
	public void rotate(int angle, boolean immediateReturn)
	{
		motor1.rotate(angle, true);
		motor2.rotate(-angle, immediateReturn);
	}
	public void rotate(int angle)
	{
		rotate(angle, false);
	}
	public void rotateTo(int limitAngle, boolean immediateReturn)
	{
		motor1.rotateTo(limitAngle, true);
		motor2.rotateTo(-limitAngle, immediateReturn);
	}
	public void rotateTo(int limitAngele)
	{
		rotateTo(limitAngele, false);
	}
	public void forward()
	{
		motor1.forward();
		motor2.backward();
	}
	public void backward()
	{
		motor1.backward();
		motor2.forward();
	}
	public void setSpeed(int speed)
	{
		motor1.setSpeed(speed);
		motor2.setSpeed(speed);
	}
	public void setAcceleration(int acceleration)
	{
		motor1.setAcceleration(acceleration);
		motor2.setAcceleration(acceleration);
	}
	public void setStallTreshold(int error, int time)
	{
		motor1.setStallThreshold(error, time);
		motor2.setStallThreshold(error, time);
	}
	public void flt(boolean immediateReturn)
	{
		motor1.flt(true);
		motor2.flt(immediateReturn);
	}
	public void flt()
	{
		flt(false);
	}
	public void stop(boolean immediateReturn)
	{
		motor1.stop(true);
		motor2.stop(immediateReturn);
	}
	public void stop()
	{
		stop(false);
	}
	public void close()
	{
		motor1.close();
		motor2.close();
	}
}
	
	

