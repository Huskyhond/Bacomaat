/*
include neccesary headers:
The SPI and MiFare header are strictly required.
There's no other way of normally (with limited time) implementing MiFare's RFID Implementation without the MFRC522 header.
Keypad.h can be found at http://playground.arduino.cc/uploads/Code/keypad.zip
SPI.h is packaged with the Arduino IDE 
MFRC522.h can be found at https://github.com/miguelbalboa/rfid

Case explantion
You can except the following cases:x
01 = card found, break idle.
02 = clear input.
20 = wait for accountnumber and pin 19 bytes.
03 = return balance. 
04 = withdraw cash, wait for amount, 3 bytes.
05 = print ticket, with accountnumber, withdrawn amount, date and time.
06 = cancel return to idle.
07 = input length, either 0 to 4 or 0 to 3.
09 = withdraw digit which was typed.
10 = back to the select withdraw or balance loop.
14 = balance to withdraw (a confirmation).
*/

#include <Keypad.h>
#include <SPI.h>
#include <MFRC522.h>

//define the number of rows and columns
const byte rows = 4;
const byte columns = 4;

//Define the slave select and reset pins
#define SS 10 // 10 GREY -> SDA on RFID-RC522
#define MOSI 11 // 11 BLUE -> MOSI on RFID-RC522
#define MISO 12 //12 GREEN -> MISO on RFID-RC522
#define SCK 13 // 13 PURPLE -> SCK on RFID-RC522
#define RST 5 //5 RED -> RST on RFID-RC522

#define block2 2
#define block3 3
int keyCounter3=0;

int run = 0;
int runAuth = 0;
int runWithdraw = 0;
int runTicket = 0;

MFRC522 mfrc522(SS, RST);
MFRC522::MIFARE_Key key;

char keys[rows][columns] = 
{
  {'1','2','3','A'},
  {'4','5','6','B'},
  {'7','8','9','C'},
  {'*','0','#','D'}
};
//Wiring scheme specifies front facing keypad.
byte rowPins[rows] = {A0, A1, A2, A3}; //A0 = BLACK -> 1, A1 = WHITE -> 2, A2 = PURPLE -> 3, A3 = GREY -> 4.
byte colPins[columns] = {9, 8, 7, 6}; //9 = ORANGE -> 5, 8 = YELLOW -> 6, 7 = GREEN -> 7,  6 = BLUE -> 8.

//A readbuffer is required to read data from a MiFare card.
//MIFARE_Read requires a minimum buffersize of 18 bytes to read 16 bytes.
byte readblockBuffer[18];

Keypad keyPad = Keypad(makeKeymap(keys), rowPins, colPins, rows, columns);

int readBlock(int blockNumber, byte arrayAddress[]) 
{
  int largestModulo4Number=blockNumber/4*4;
  int trailerBlock=largestModulo4Number+3;//determine trailer block for the sector
  byte status = mfrc522.PCD_Authenticate(MFRC522::PICC_CMD_MF_AUTH_KEY_A, trailerBlock, &key, &(mfrc522.uid));

  if (status != MFRC522::STATUS_OK) 
  {
         Serial.print("PCD_Authenticate() failed (read): ");
         Serial.println(mfrc522.GetStatusCodeName(status));
         return 3;//return "3" as error message
  }
        
  byte buffersize = 18;
  status = mfrc522.MIFARE_Read(blockNumber, arrayAddress, &buffersize);
  if (status != MFRC522::STATUS_OK) {
          Serial.print("MIFARE_read() failed: ");
          Serial.println(mfrc522.GetStatusCodeName(status));
          return 4;//return "4" as error message
  }
}

int finish()
{
  mfrc522.PCD_Init();
}

void setup()
{
 Serial.begin(2000000, SERIAL_8E1); //Baud rate 115200 bits per second, 8 data bits, Parity E = even, 1 stop bit.
 SPI.begin();
 finish();
 for (byte i = 0; i < 6; i++)
 {
  key.keyByte[i] = 0xFF;
 }
}

void loop()
{ 
  byte* reply[16];
  byte* atqa[2];
  mfrc522.PICC_RequestA(atqa[2], reply[16]);
  
//  if(!mfrc522.PICC_IsNewCardPresent())
//  {
//    return;
//  }

  if(!mfrc522.PICC_ReadCardSerial())
  {
    return;
  }
  
  Serial.print("01"); //tell java to wake from idle with code: 1.
  //read the accountnumber from block 2. 
  readBlock(2, readblockBuffer);
  byte accountNumber[16];
  for(int c = 0; c < 14; c++)
  {
    accountNumber[c]=readblockBuffer[c];
    //Serial.write(readblockBuffer[c]);
  }
  
  //setup variables, and start pin verification loop.
  loop:
  byte input[4];
  int keyCounter = 0;
  int failedAttempts = 0;
  int failCheck = 0;
  int stateFailCount = 0;
  int verifyAmount = 0;
  int run = 1;

  while(run)
  {
    char keypress = keyPad.getKey();
    switch(keypress)
    {
      //pin has been chosen check if the length is correct and return if it's to short.
      case 'A':
        if(keyCounter < 4)
        {
          run = 0;
          goto loop;
        }

        //if the keycounter is 4 we can let the client verify the pin and account. Since the whole crap was merged in 1 API call /login.
        if(keyCounter >= 4)
        { 
          keyCounter=0;
          Serial.write("20");
          for(int c = 0; c < 14; c++)
          {
            Serial.write(accountNumber[c]);
          }
          for(int x = 0; x <= 3; x++)
          {
            Serial.write(input[x]);
          }
        }
        
        //wait for verification of pin and accountnumber.
        stateFailCount = 1;
        failCheck = 0;
        while(stateFailCount) // wait for serial data
        {
          if(Serial.available() > 0) // check for serial data
          {
            failCheck = Serial.read(); // fill the byte
            stateFailCount = 0; //reset state we've got what we need
          }
        }
        
        switch(failCheck)
        {
          case 49: //pin rejected.
            failCheck = 0; // clear the identifier.
            goto loop; //return to pin input. remember pin.
          break;

          case 50: //pin verified.
            //do nothing break the switch.
            //runAuth will be set to 1 below.
          break;

          case 51: //pin rejected fail count >= 3.
            Serial.println("06");
            finish();//clear everything, requires user to rescan the card.
            return;
          break;

          default: //Read error.
            Serial.println("06");
            finish();
            return;
          break;
        }
        
        runAuth=1;
        
        //Start the authorized loop.
        while(runAuth)
        {
          char keypress = keyPad.getKey();
          //Serial.println(keypress);
          switch(keypress)
          {
            case 'A':
              Serial.println("03");//Give balance
            break;
            
            case 'B':
              Serial.print("04");//withdraw
              byte amount[3];
              keyCounter3=0;
              runWithdraw=1;
              withdraw:
              while(runWithdraw)
              {
                char keypress = keyPad.getKey();
                //Serial.println(keypress);
                switch(keypress)
                {
                  case 'A':
                    if(keyCounter3 >= 3 || keyCounter3 >= 2)
                    {
                      keyCounter3=0;
                      Serial.print("14");//balance to withdraw
                      //Serial.print("");

                      //wait for user verification of rounded amount.
                      //A is yes withdraw, B is no, don't withdraw return to the withdraw screen/fase/loop.
                      delay(1000);
                      verifyAmount = 1;

                      while(verifyAmount)
                      {
                        keypress = keyPad.getKey();
                        switch(keypress)
                        {
                          case 'A':
                            Serial.print("2");
                            verifyAmount = 0;
                          break;

                          case 'B':
                            Serial.print("1");
                            verifyAmount = 0;
                            keyCounter3 = 0;
                            goto withdraw;
                          break;

                          default:
                           //do nothing.
                          break;
                        }
                      }

                      int state = 1;
                      int enoughBalance = 0;
                      while(state) // wait for serial data
                      {
                        if(Serial.available() > 0) // check for serial data
                        {
                          enoughBalance = Serial.read(); // fill the byte
                          state = 0; //reset state we've got what we need
                        }
                      }
                      if(enoughBalance != 50) // check if we havent been returned a 2 exactly.
                      {
                        //Serial.print("04"); // print fase withdraw.
                        enoughBalance = 0; // clear the identifying variable.
                        runWithdraw = 1;
                        goto withdraw;
                        return; // return to the withdraw screen.
                      }
                      
                      int runTicket=1;
                      while(runTicket)
                      {
                        char keypress = keyPad.getKey();
                        switch(keypress)
                        {
                          case 'A':
                            Serial.println("05");//Print ticket
                            for(int i = 0; i < 16; i++)
                            {
                              Serial.write(accountNumber[i]);
                            }
                            Serial.println("");
                            Serial.println("06");
                            runTicket = 0;
                            runWithdraw = 0;
                            runAuth = 0;
                            run = 0;
                            run=1;
                            finish();
                            return;
                         break;

                         case 'B':
                           Serial.println("06");
                           runTicket = 0;       
                           runWithdraw = 0;
                           runAuth = 0;
                           run = 0;
                           finish();
                           return;
                         break;
                        }
                      }
                    }
                  break;

                  case 'B':
                    Serial.println("02"); //case 8 reset input
                    for (int i=0; i<3; ++i)
                    {
                      amount[i]=0;
                    }
                    keyCounter3 = 0;
                  break;

                  case 'C':
                    Serial.println("06");
                    keyCounter3=0;
                    for (int i=0; i<3; ++i)
                    {
                      amount[i]=0;
                    }
                    for(int x=0; x<4; x++)
                    {
                    input[x] = 0;
                    }
                    for(int x=0; x<16; x++)
                    {
                     accountNumber[x]=0;
                    }
                    runWithdraw = 0;
                    runAuth = 0;
                    run = 0;
                    finish();
                    break;
                  
                    case 'D':
                      Serial.println("10"); // go back to withdraw or 
                      runWithdraw = 0;
                      keyCounter3=0;
                      for (int i=0; i<3; ++i)
                      {
                        amount[i]=0;
                      }
                    break;

                  case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8': case '9': case '0':
                    if(keyCounter3 < 3 || keyCounter3 < 2 || keyCounter3 < 1)
                    {
                      amount[keyCounter3]=keypress;
                      Serial.print("09");
                      Serial.write(amount[keyCounter3]);
                      keyCounter3++;
                    }
                  break;
                    
                }
              }
              break;
              
              case 'C':
                Serial.println("06");
                    runAuth = 0;
                    run = 0;
                    finish();
              return;
          }
        }
        break;
      
      case 'B':
        Serial.println("02"); //case 8 reset input
        keyCounter=0;
        for(int x=0; x<4; x++)
        {
        input[x] = 0;
        }
      break;
      
      case 'C':
         keyCounter=0;
         for(int x=0; x<4; x++)
         {
          input[x] = 0;
         }
         for(int x=0; x<16; x++)
         {
           accountNumber[x]=0;
         }
         Serial.println("06");
         run = 0;
         finish();
      break; 
      
      case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8': case '9': case '0':
        if(keyCounter < 4)
        {
          input[keyCounter]=keypress;
          keyCounter++;
          Serial.print("07");//case 7 for keycount 
          Serial.print(keyCounter);
        }
      break;  
    }    
  }
}