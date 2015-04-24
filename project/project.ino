/*
 * Switch test program
 */


int switchPin1 = 2;              // Switch connected to digital pin 2
int switchPin2 = 3;
int switchPin3 = 4;
int doorLock   = 9;
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

void setup()                    // run once, when the sketch starts
{
  Serial.begin(9600);           // set up Serial library at 9600 bps
  
  
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
      verify(&verifyCode,&buttonValue,passcode,inputCode); 
    }
    delay(250);

 
  
}

void verify(boolean *verifyCode, int *buttonValue,int passcode[3],int inputCode[3]){
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
   }
   else {
     digitalWrite(doorLock,HIGH);
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



