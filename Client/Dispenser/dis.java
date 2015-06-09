package dispenser;
import lejos.nxt.*;
public class dis 
{

	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
		public void turnServo(long t,int n)
		{
			// 
		}
		public int readSensor(int n)
		{
			int sonic1dist = sonic1.getDistance();
			int sonic2dist = sonic2.getDistance();
			int sonic3dist = sonic3.getDistance();
		}
		public void dispense(int[] moneyOptions)
		{
			int tien = moneyOptions[0];
			int twintig = moneyOptions[1];
			int vijftig = moneyOptions[2];
			for (int i = 0;i < tien;)
			{
				{
					System.out.println(sonic1.getDistance() + "");
					int sonic1Dist = sonic1.getDistance();
					if(sonic1Dist < 11 || sonic1Dist > 17) 
					{
						Motor.A.stop();
						Motor.A.setSpeed(400);
						Motor.A.forward();
						try { Thread.sleep(500); }catch(Exception e) {}
						Motor.A.stop();
						Motor.A.setSpeed(400);
						Motor.A.backward();
						noMoney = 0;
					}
					/*noMoney++;
					if(noMoney > 100) 
					{
						Motor.A.stop();
						Motor.B.stop();
						Motor.C.stop();
					} */
				}
			}
			for (int i = 0;i < twintig;)
			{
				{
					System.out.println(sonic2.getDistance() + "");
					int sonic2Dist = sonic2.getDistance();
					if(sonic2Dist < 11 || sonic2Dist > 17) 
					{
						Motor.B.stop();
						Motor.B.setSpeed(400);
						Motor.B.forward();
						try { Thread.sleep(500); }catch(Exception e) {}
						Motor.B.stop();
						Motor.B.setSpeed(400);
						Motor.B.backward();
						noMoney = 0;
					}
					/*noMoney++;
					if(noMoney > 100) 
					{
						Motor.A.stop();
						Motor.B.stop();
						Motor.C.stop();
					} */
				}
			}
			for (int i = 0;i < vijftig;)
			{
				{
					System.out.println(sonic3.getDistance() + "");
					int sonic3Dist = sonic3.getDistance();
					if(sonic3Dist < 11 || sonic3Dist > 17) 
					{
						Motor.C.stop();
						Motor.C.setSpeed(400);
						Motor.C.forward();
						try { Thread.sleep(500); }catch(Exception e) {}
						Motor.C.stop();
						Motor.C.setSpeed(400);
						Motor.C.backward();
						noMoney = 0;
					}
					/*noMoney++;
					if(noMoney > 100) 
					{
						Motor.A.stop();
						Motor.B.stop();
						Motor.C.stop();
					} */
				}
			}
	}

}
