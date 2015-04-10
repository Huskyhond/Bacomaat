#include <KeyboardController.h>
#include <ctype.h>

// Initialize USB Controller
USBHost usb;

int counter = 0;

// Attach Keyboard controller to USB
KeyboardController keyboard(usb);

void setup(){
  Serial.begin(9600);
}

void loop(){
  usb.Task();
}



int numbers_only(const char *s)
{
  while (*s) {
      if (isdigit(*s++) == 0) return 0;
  }
  return 1;
}

void keyPressed() {
  Serial.print("Pressed:  ");
  Serial.print(keyboard.getKey());
  if(keyboard.getKey() == "s") { // Scan initialised
   
    Serial.println("01");
    Serial.println("MLBI0200000002"); // Bank id voor debugging
    Serial.println("21");
  }
  else if(numbers_only(keyboard.getKey())) { // 0 tot 10 gedrukt
    counter++;
    int getal = strtol(keyboard.getKey());

  }
  else if(keyboard.getKey() == "r") { // Reset input
    Serial.println("02");
    counter = 0;
  }
  else if(keyboard.getKey() == "g") { // Get balance
    Serial.println("03");
  }
  else if(keyboard.getKey() == "w") { // Withdraw
    Serial.println("04");
  }
  else if(keyboard.getKey() == "p") { // Print ticket
    Serial.println("05"); 
  }
  else if(keyboard.getKey() == "c") { // Cancel return to idle
    Serial.println("06");
  }
  else if(keyboard.getKey() == "i") { // Keycount opsturen
    Serial.println("07");
    Serial.println(counter); // Keycount
  }
  else if(keyboard.getKey() == "b") { // Back
    Serial.println("10");
  }
}