Public interface NXT;

{
	//Deze string geeft de COM poort aan die gebruikt wordt.
	private String comport;
	
	//Default lege Constructor.
	public NXT()
	{

	}

	//COM poort Constructor.
	public NXT(String s)
	{
		comport = s;
	}

	public void turnServo(Long t, int n)
	{
		//long t wordt gebruikt voor de tijd dat iets draait.
		//int n geeft het nummer van de servo aan.
		//deze methode laat servo n draaien voor t milisconden.
		//vergeet de COM poort niet te gebruiken.
	}

	public int readSensor(int n)
	{
		//reads the input from sensor with number n.
		//Geeft een int terug, 1 betekent gezien. 0 betekent niets gezien.
		//houdt rekening met de tijd dat de sensor aanstaat en houd rekening met de afstand die de sensor kan meten.
		//vergeet de COM poort niet te gebruiken.
	}

	public boolean upload()
	{
		//uploads a program to the NXT brick
	}
}