import lejos.nxt.*;
import lejos.util.*;
import java.lang.*;

public class Lejos extends Thread {

  public static void printBillA() {
    UltrasonicSensor s = new UltrasonicSensor(SensorPort.S1);
    boolean stopped = false;
    Motor.A.backward();
    while(!stopped) {
        int dist = s.getDistance();
        if(dist < 11 || dist > 17) {
            Motor.A.stop();
            Motor.A.forward();
            Delay.msDelay(500);
            Motor.A.stop();
            stopped = true;
        }
    }
  }

   public static void printBillB() {
    UltrasonicSensor s = new UltrasonicSensor(SensorPort.S2);
    boolean stopped = false;
    Motor.B.backward();
    while(!stopped) {
        int dist = s.getDistance();
        System.out.println(dist);
        if(dist < 11 || dist > 17) {
            Motor.B.stop();
            Motor.B.forward();
            Delay.msDelay(500);
            Motor.B.stop();
            stopped = true;
        }
    }
  }

   public static void printBillC() {
    UltrasonicSensor s = new UltrasonicSensor(SensorPort.S3);
    boolean stopped = false;
    Motor.C.backward();
    while(!stopped) {
        int dist = s.getDistance();
        if(dist < 11 || dist > 17) {
            Motor.C.stop();
            Motor.C.forward();
            Delay.msDelay(500);
            Motor.C.stop();
            stopped = true;
        }
    }
  }

  public static void main (String[] args) {

int dispenserA = 1;
int dispenserB = 1;
int dispenserC = 1;

    System.out.println("Hallo kees!?");
    // Default engine speeds.
    Motor.A.setSpeed(250); 
    Motor.B.setSpeed(250);
    Motor.C.setSpeed(250);

    while(dispenserA > 0) {
        printBillA();
        dispenserA--;
    }

    while(dispenserB > 0) {
        printBillB();
        dispenserB--;
    }

    while(dispenserC > 0) {
        printBillC();
        dispenserC--;
    }

  }
}
