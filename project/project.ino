/*
 * Switch test program
 */

int switchPin1 = 2;              // Switch connected to digital pin 2
int switchPin2 = 3;
int switchPin3 = 4;
int doorLock   = 9;
int switchStatus1;
int switchStatus2;
int switchStatus3;
int passcode[] = {1,2,3};
int inputCode[2];
int buttonValue = 0;
int i= 0;
boolean verifyCode = true;
boolean lockVerify = false;
void setup()                    // run once, when the sketch starts
{
  Serial.begin(9600);           // set up Serial library at 9600 bps
  pinMode(switchPin1, INPUT);    // sets the digital pin as input to read switch
  pinMode(switchPin2, INPUT);
  pinMode(switchPin3, INPUT);
  pinMode(doorLock,OUTPUT);
  digitalWrite(doorLock,LOW);
}


void loop()                     // run over and over again
{
  switchStatus1 = digitalRead(switchPin1);
  switchStatus2 = digitalRead(switchPin2);
  switchStatus3 = digitalRead(switchPin3);
 Serial.println(digitalRead(doorLock));
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
    verify(&lockVerify,&verifyCode,&buttonValue,passcode,inputCode); 
    
  }
  delay(250);
}

void verify(boolean *lockVerify,boolean *verifyCode, int *buttonValue,int passcode[3],int inputCode[3]){
   Serial.println("verifying code");
    int i = 0;
   while(*verifyCode == true&&i<3){      
     if(passcode[i] != inputCode[i]){
       Serial.println("Wrong passcode");
       *verifyCode = false;
     }
       
     i++;
   }
   if(*verifyCode ==true){
     Serial.println("UnlockDoor");
     digitalWrite(doorLock,HIGH);
      
   }
   else 
     digitalWrite(doorLock,LOW);
   
   *verifyCode = true;
   *buttonValue = 0;
   lockVerify = false;
   i = 0;

}


