package MLB;

public interface NXT
{	
	//COM poort Constructor.
	
	public void turnServo(Long t, int n);

	public int readSensor(int n);

	public boolean upload();
}