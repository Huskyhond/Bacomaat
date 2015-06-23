import lejos.nxt.*;
import lejos.util.*;
import java.lang.*;

public class Lejos extends Thread {

  public static void printBillA() {
    Motor.A.backward();

    Delay.msDelay(2200);
    Motor.A.stop();
    Motor.A.forward();
    Delay.msDelay(1000);
    Motor.A.stop();
    
  }

   public static void printBillB() {
    Motor.B.backward();

    Delay.msDelay(2200);
    Motor.B.stop();
    Motor.B.forward();
    Delay.msDelay(1000);
    Motor.B.stop();
  }

   public static void printBillC() {
    Motor.C.backward();

    Delay.msDelay(2200);
    Motor.C.stop();
    Motor.C.forward();
    Delay.msDelay(1000);
    Motor.C.stop();
  }

  public static void main (String[] args) {

int dispenserA = 1;
int dispenserB = 1;
int dispenserC = 1;

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

    //Debugging
    Button.waitForAnyPress();
  }
}
