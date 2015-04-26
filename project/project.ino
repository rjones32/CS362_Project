/*
 * Switch test program
 */

#include <LiquidCrystal.h>

// Init the Pins used for PWM
const int bluePin = 13;
const int redPin = 11;
const int greenPin = 10;

// Init our Vars
int currentColorValueRed;
int currentColorValueGreen;
int currentColorValueBlue;


int switchPin1 = 2;              // Switch connected to digital pin 2
int switchPin2 = 3;
int switchPin3 = 4;
int doorLock   = 8;
int doorState;
int switchStatus1;
int switchStatus2;
int switchStatus3;
int passcode[] = {1,2,3};
int inputCode[2];
int buttonValue = 0;
int i= 0;
char data[1];
boolean verifyCode = true;
boolean isDoorLock;
//LiquidCrystal lcd(9,10,5,6,7,8);

void setup()                    // run once, when the sketch starts
{
  Serial.begin(9600);           // set up Serial library at 9600 bps
  
  //lcd.begin(16,2);
  //lcd.clear();
  //lcd.print("Dark");
  pinMode(redPin,OUTPUT);
  pinMode(greenPin,OUTPUT);
  pinMode(bluePin,OUTPUT);
  pinMode(switchPin1, INPUT);    // sets the digital pin as input to read switch
  pinMode(switchPin2, INPUT);
  pinMode(switchPin3, INPUT);
  pinMode(doorLock,OUTPUT);
  digitalWrite(doorLock,HIGH);
  isDoorLock = true; 
}


void loop()                     // run over and over again
{
  
  switchStatus1 = digitalRead(switchPin1);
  switchStatus2 = digitalRead(switchPin2);
  switchStatus3 = digitalRead(switchPin3);
  doorState     = digitalRead(doorLock);
  Serial.println(digitalRead(doorLock));
  //Serial.write(doorState);
     
  //lcd.clear();  
   if(isDoorLock==true){
     //lcd.print("Door is Locked");
     analogWrite(greenPin,255);
      analogWrite(redPin,0);
      analogWrite(bluePin,255);
   }
   else if(isDoorLock==false){
     //lcd.print("Door is unLocked");
     analogWrite(greenPin,255);
      analogWrite(redPin,255);
      analogWrite(bluePin,0);
   
   }
   
 
 
  if(Serial.available()>0){
    //Serial.println("Read from Bluetooth ");
    Serial.readBytesUntil('\n',data,1);
    isDoorLock = ascii2Digit(data,&isDoorLock);
    if(isDoorLock==false){
      Serial.write("0");
      digitalWrite(doorLock,LOW);
      
     }
   
    else{
      Serial.write("1");
      digitalWrite(doorLock,HIGH);
     }
   
    
    //Serial.flush();
     } 
 
   else if(Serial.available()<0||verifyCode==false){
      isDoorLock=true;
      digitalWrite(doorLock,HIGH); 
      
   }
 
 
    if(switchStatus1 == LOW){
      Serial.println("Button1 has been pushed ");
      inputCode[buttonValue] = 1;
      buttonValue++;
    }
    else if (switchStatus2 == LOW){ 
      Serial.println("Button2 has been pushed ");
      inputCode[buttonValue] = 2;
      buttonValue++;
    }
    else if (switchStatus3 == LOW) {
      Serial.println("Button3 has been pushed ");
      inputCode[buttonValue] = 3;
      buttonValue++;
    }
    if(buttonValue==3){
      verify(&verifyCode,&isDoorLock,&buttonValue,passcode,inputCode); 
      
    }
    delay(250);

 
  
}

void verify(boolean *verifyCode,boolean *isDoorLock, int *buttonValue,int passcode[3],int inputCode[3]){
   Serial.println("verifying code");
    int i = 0;
   while(*verifyCode == true&&i<3){      
     if(passcode[i] != inputCode[i]){
       Serial.println("Wrong passcode");
       *verifyCode  = false;
        buttonValue = 0;
       return;
     }
       
     i++;
   }
   if(*verifyCode ==true){
     Serial.println("UnlockDoor");
     digitalWrite(doorLock,LOW);
     *isDoorLock = false;
   }
   else {
     digitalWrite(doorLock,HIGH);
     *isDoorLock = true;
   }
   
   *verifyCode = true;
   *buttonValue = 0;
    i = 0;
    
}

boolean ascii2Digit(char input[],boolean isDoorLock) {
  // if time sync available from serial port, update time and return true

  int value = 0;
  int i = 0; 
  int count = 0; 

  while(input[i]!=0){             
    if( input[i] >= '0' && input[i] <= '9'){   
      value = (.10 * value) + (input[i] - '0') ; // convert digits to a number
      
      i++;
    }
    else 
      i++;
    //Serial.println(i);  
  }
   Serial.print("value: ");  
   Serial.println(value);  
   if(value==1)
    isDoorLock = true;
   
   else 
    isDoorLock = false;
    
  return isDoorLock; 
}



