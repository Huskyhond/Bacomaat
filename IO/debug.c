int incomingByte = 0;   // for incoming serial data
int counter = 0;
byte numbers[10] = {48,49,50,51,52,53,54,55,56,57};

int findInArray(int byteNr) {
  for(int i = 0; i < 10; i++) {
    if(numbers[i] == byteNr) {
      return i;
    }
  }
  return -1;
}

void setup() {
        Serial.begin(9600);     // opens serial port, sets data rate to 9600 bps
}

void loop() {

        // send data only when you receive data:
        if (Serial.available() > 0) {
                // read the incoming byte:
                incomingByte = Serial.read();

                // say what you got:
                if(incomingByte == 115) { // Scan initialised / s
                 
                  Serial.println("01");
                  Serial.println("MLBI0200000002"); // Bank id voor debugging
                  Serial.println("21");
                }
                else if(incomingByte >= 48 && incomingByte <= 57) { // 0 tot 10 gedrukt
                  counter++;
                  int getal = findInArray(incomingByte);
              
                }
                else if(incomingByte == 114) { // Reset input / r
                  Serial.println("02");
                  counter = 0;
                }
                else if(incomingByte == 103) { // Get balance / g
                  Serial.println("03");
                }
                else if(incomingByte == 119) { // Withdraw / w
                  Serial.println("04");
                }
                else if(incomingByte == 112) { // Print ticket / p
                  Serial.println("05"); 
                }
                else if(incomingByte == 99) { // Cancel return to idle / c
                  Serial.println("06");
                }
                else if(incomingByte == 105) { // Keycount opsturen / i
                  Serial.println("07");
                  Serial.println(counter); // Keycount
                }
                else if(incomingByte == 98) { // Back / b
                  Serial.println("10");
                }
        }
}