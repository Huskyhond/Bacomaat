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
  int trailerBlock=largestModulo4Number+3;
  byte status = mfrc522.PCD_Authenticate(MFRC522::PICC_CMD_MF_AUTH_KEY_A, trailerBlock, &key, &(mfrc522.uid));
  
  if(status != MFRC522::STATUS_OK)
  {
   Serial.print("PCD_Authenticate() failed (read): ");
   Serial.println(mfrc522.GetStatusCodeName(status));
   return 3;
  }
}

void setup()
{
 Serial.begin(9600);
 SPI.begin();
 mfrc522.PCD_Init();
 for (byte i = 0; i < 6; i++)
 {
  key.keyByte[i] = 0xFF;
 }
}

void loop()
{
  if(!mfrc522.PICC_IsNewCardPresent())
  {
    return;
  }
  if(!mfrc522.PICC_ReadCardSerial())
  {
    return;
  }
  
  Serial.println("New card found"); //tell java to wake from idle with code: 1.
  //read the accountnumber. 
  readBlock(block2, readblockBuffer);
  byte accountNumber[16];
  for(int c = 0; c < 16; c++)
  {
    accountNumber[c]=readblockBuffer[c];
  }
  //Serial.println(""); 
  
  //read the pin number.
  readBlock(block3, readblockBuffer);
  byte pin[4];
  for(int c = 0; c < 5; c++)
  {
    pin[c]=readblockBuffer[c];
  }
  //Serial.println(""); 
  
  //setup variables, and start pin verification loop.
  byte input[4];
  int keyCounter = 0;
  int retryChances = 3;
  
  run=1;
  while(run)
  {
    if(retryChances <= 0)
    {
      retryChances = 3;
      Serial.write("Block ");
      for(int x=0; x<16; x++)
      {
        Serial.write(accountNumber[x]);
      }
      return;
    }
    char keypress = keyPad.getKey();
    switch(keypress)
    {
      case 'A':
        if(keyCounter >= 4)
        {
          for(int x=0; x<4; x++)
          {
            if(pin[x] != input[x])
            {
              keyCounter=0;
              retryChances--;
              for(int x=0; x<4; x++)
              {
                pin[x] = 0;
                input[x] = 0;
              }
              break;
            } 
          }
          Serial.println("Pin verified");
        }
        //Start the authorized loop.
        runAuth=1;
        while(runAuth)
        {
          char keypress = keyPad.getKey();
          switch(keypress)
          {
            case 'A':
              Serial.println("Give balance");
            break;
            
            case 'B':
              byte amount[3];
              keyCounter3=0;
              runWithdraw=1;
              while(runWithdraw)
              {
                char keypress = keyPad.getKey();
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
                      int runTicket=1;
                      while(runTicket)
                      {
                        //put ticket option code here.
                        runTicket=0;
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
                    runWithdraw=0;
                  break;
                }
              }
              break;
              
              case 'C':
                 runAuth=0;
              break;
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
         run = 0; 
      break;      
      
      case '1':
        if(keyCounter<4)
        {
          input[keyCounter]=keypress;
          keyCounter++;
        }
        else
        {
          break;
        }
      break;
      
      case '2':
        if(keyCounter<4)
        {
          input[keyCounter]=keypress;
          keyCounter++;
        }
        else
        {
          break;
        }
      break;
      
      case '3':
        if(keyCounter<4)
        {
          input[keyCounter]=keypress;
          keyCounter++;
        }
        else
        {
          break;
        }
      break;
      
      case '4':
        if(keyCounter<4)
        {
          input[keyCounter]=keypress;
          keyCounter++;
        }
        else
        {
          break;
        }
      break;
   
      case '5':
        if(keyCounter<4)
        {
          input[keyCounter]=keypress;
          keyCounter++;
        }
        else
        {
          break;
        }
      break;      
      
      case '6':
        if(keyCounter<4)
        {
          input[keyCounter]=keypress;
          keyCounter++;
        }
        else
        {
          break;
        }
      break;
      
      case '7':
        if(keyCounter<4)
        {
          input[keyCounter]=keypress;
          keyCounter++;
        }
        else
        {
          break;
        }
      break; 
      
      case '8':
        if(keyCounter<4)
        {
          input[keyCounter]=keypress;
          keyCounter++;
        }
        else
        {
          break;
        }
      break;
      
      case '9':
        if(keyCounter<4)
        {
          input[keyCounter]=keypress;
          keyCounter++;
        }
        else
        {
          break;
        }
      break;
       
      default:
         break;     
    }    
  }
}