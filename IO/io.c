//include neccesary headers:
//The SPI and MiFare header are strictly required.
//There's no other way of normally (with limited time) implementing MiFare's RFID Implementation without the MFRC522 header.

#include <Keypad.h>
#include <SPI.h>
#include <MFRC522.h>

//define the number of rows and columns
const byte rows = 4;
const byte columns = 4;

//Define the slave select and reset pins
#define SS 10
#define MOSI 11
#define MISO 12
#define SCK 13
#define RST 5 

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

byte rowPins[rows] = {A0, A1, A2, A3};
byte colPins[columns] = {9, 8, 7, 6};

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
 Serial.begin(9600);
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
  
  Serial.println("1"); //tell java to wake from idle with code: 1.
  //read the accountnumber. 
  readBlock(2, readblockBuffer);
  byte accountNumber[16];
  for(int c = 0; c < 16; c++)
  {
    accountNumber[c]=readblockBuffer[c];
    Serial.write(readblockBuffer[c]);
  }
  Serial.println(""); 
  
  //read the pin number.
  readBlock(6, readblockBuffer);
  byte pin[4];
  for(int c = 0; c < 4; c++)
  {
    pin[c]=readblockBuffer[c];
    //Serial.write(pin[c]);
  }
  //Serial.println(""); 
  
  //setup variables, and start pin verification loop.
  loop:
  byte input[4];
  int keyCounter = 0;
  int failedAttempts = 0;
  run=1;
  while(run)
  {
    char keypress = keyPad.getKey();
    switch(keypress)
    {
      case 'A':
        if(keyCounter < 4)
        {
          run = 0;
          goto loop;
        }
        if(keyCounter >= 4)
        {
          keyCounter=0;
          for(int x=0; x<=3; x++)
          {
            if(pin[x] != input[x])
            {
              Serial.println("2");//fase 2, verification
              Serial.println("False");//means the verification failed
              for(int x=0; x<16; x++)
              {
                Serial.write(accountNumber[x]);
              }
              Serial.println("");
              run = 0;
              goto loop;
            }
          }
        }
        Serial.println("2");//fase 2, verification
        Serial.println("True");//means the verification succeeded
        runAuth=1;
        
        //Start the authorized loop.
        while(runAuth)
        {
          char keypress = keyPad.getKey();
          //Serial.println(keypress);
          switch(keypress)
          {
            case 'A':
              Serial.println("3");//Give balance
            break;
            
            case 'B':
              Serial.println("4");//withdraw
              byte amount[3];
              keyCounter3=0;
              runWithdraw=1;
              while(runWithdraw)
              {
                char keypress = keyPad.getKey();
                //Serial.println(keypress);
                switch(keypress)
                {
                  case 'A':
                    if(keyCounter3>=3)
                    {
                      keyCounter3=0;
                      for(int x=0; x<3; x++)
                      {
                        Serial.write(amount[x]);
                      }
                      Serial.println("");
                      int runTicket=1;
                      while(runTicket)
                      {
                        char keypress = keyPad.getKey();
                        switch(keypress)
                        {
                          case 'A':
                            Serial.println("5");//Print ticket
                            for(int i = 0; i < 16; i++)
                            {
                              Serial.write(accountNumber[i]);
                            }
                            Serial.println("");
                            Serial.println("6");
                            runTicket = 0;
                            runWithdraw = 0;
                            runAuth = 0;
                            run = 0;
                            finish();
                            return;
                         break;
                         
                         case 'B':
                           Serial.println("6");
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
                    for (int i=0; i<3; ++i)
                    {
                      amount[i]=0;
                    }
                  break;

                  case 'C':
                    Serial.println("6");
                    keyCounter3=0;
                    for (int i=0; i<3; ++i)
                    {
                      amount[i]=0;
                    }
                    for(int x=0; x<4; x++)
                    {
                    pin[x] = 0;
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
                  
                  case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8': case '9': case '0':
                    if(keyCounter3 < 3)
                    {
                      amount[keyCounter3]=keypress;
                      keyCounter3++;
                    }
                  break;
                    
                }
              }
              break;
              
              case 'C':
                Serial.println("6");
                    runAuth = 0;
                    run = 0;
                    finish();
              return;
          }
        }
        break;
      
      case 'B':
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
          pin[x] = 0;
          input[x] = 0;
         }
         for(int x=0; x<16; x++)
         {
           accountNumber[x]=0;
         }
         Serial.println("6");
         run = 0;
         finish();
      break; 
      
      case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8': case '9': case '0':
        if(keyCounter < 4)
        {
          input[keyCounter]=keypress;
          keyCounter++;
        }
      break;  
    }    
  }
}