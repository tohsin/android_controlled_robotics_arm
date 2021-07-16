#include <Servo.h>



Servo base;
Servo right;
Servo left;
Servo gripper;

     // start byte, begin reading input
int servo;           // which servo to pulse?
int pos;             // servo angle 0-180
String data;
int number_of_commands;
int number_of_repeats;
String curr_command;
String getValue(String data, char separator, int index)
{
    int found = 0;
    int strIndex[] = { 0, -1 };
    int maxIndex = data.length() - 1;

    for (int i = 0; i <= maxIndex && found <= index; i++) {
        if (data.charAt(i) == separator || i == maxIndex) {
            found++;
            strIndex[0] = strIndex[1] + 1;
            strIndex[1] = (i == maxIndex) ? i+1 : i;
        }
    }
    return found > index ? data.substring(strIndex[0], strIndex[1]) : "";
}

void grip(){
   gripper.write(15);
}
void ungrip(){
   gripper.write(0);
}

void default_state()
{
  
  base.write(90);
  ungrip();
  left.write(180);  // from 45 to 180 dont go lower or spoil  
  // note to self default value 180
  right.write(0); //0 to 180 fine    //note to self defaul value 0
}
void work(){
   
 }
void setup() {
  gripper.attach(6);
  left.attach(13);
  right.attach(9);
  base.attach(2);
  Serial.begin(115200);
  default_state();
  // put your setup code here, to run once:
}

void loop() { 
  while(Serial.available()){
   data=Serial.readString();
   String command_string=getValue(data,':',0);

   number_of_commands = getValue(command_string, ';', 0).toInt();
   number_of_repeats=getValue(command_string, ';', 1).toInt();
  
   for(int i=0;i<number_of_repeats;i++){
    default_state();
    for(int j=0;j<number_of_commands;j++){
       
        String block=getValue(data, ':', j+1);    
        servo=getValue(block, ';', 0).toInt();
        pos=getValue(block, ';', 1).toInt();
        
        switch(servo){
          case 1:
            base.write(pos);
            delay(1000);
            break;
          case 2:
            left.write(180-pos);
            delay(1000);
            break;
          case 3:
            right.write(pos);
            delay(1000);
            break;
          case 4:
            grip();
            delay(1000);
            break; 
          case 5:
            ungrip();
            delay(1000);
            break; 
          case 6:
            delay(pos);
            break;  
        }    
    }
   }   
  
  }
}
