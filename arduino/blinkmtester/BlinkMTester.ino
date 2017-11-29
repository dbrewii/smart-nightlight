/*
 * BlinkMTester -- Simple "command-line" tool to play with BlinkMs
 *
 *  Once you load this sketch on to your Arduino, open the Serial Monitor
 *  and you'll see a menu of things to do.
 *
 *
 * BlinkM connections to Arduino
 * PWR - -- gnd -- black -- Gnd
 * PWR + -- +5V -- red   -- 5V
 * I2C d -- SDA -- green -- Analog In 4
 * I2C c -- SCK -- blue  -- Analog In 5
 *
 * Note: This sketch DOES NOT reset the I2C address of the BlinkM.
 *       If you want to change the I2C address use the 'A<n>' command.
 *
 * 2007-11, Tod E. Kurt, ThingM, http://thingm.com/
 *
 */


#include "Wire.h".

#include "BlinkM_funcs.h"
#include <SoftwareSerial.h>

#include <avr/pgmspace.h>  // for progmem stuff

String x = "0"; //Variable for storing received data
int y;
SoftwareSerial BT(10, 11); // TX, RX





// set this if you're plugging a BlinkM directly into an Arduino,
// into the standard position on analog in pins 2,3,4,5
// otherwise you can set it to false or just leave it alone
const boolean BLINKM_ARDUINO_POWERED = false;

byte blinkm_addr = 0x09; // the default address of all BlinkMs

const int serStrLen = 30;
char serInStr[ serStrLen ];  // array that will hold the serial input string

const char helpstr[] PROGMEM = 
  "\nBlinkMTester!\n"
  "'c<rrggbb>'  fade to an rgb color\n"
  "'h<hhssbb>'  fade to an hsb color\n"
  "'C<rrggbb>'  fade to a random rgb color\n"
  "'H<hhssbb>'  fade to a random hsb color\n"
  "'p<n>'  play a script\n"
  "'o'  stop a script\n"
  "'f<nn>'  change fade speed\n"
  "'t<nn>'  set time adj\n"
  "'g'  get current color\n"
  "'a'  get I2C address\n"
  "'A<n>'  set I2C address\n"
  "'B'  set startup params to default\n"
  "'Z'  get BlinkM version\n"
  "'i'  get input values\n"
  "'s'/'S'  scan i2c bus for 1st BlinkM / search for devices\n"
  "'R'  return BlinkM to factory settings\n"
  "'?'  for this help msg\n\n"
  ;
//const char badAddrStr[] PROGMEM = 
//  "BlinkM not at expected address.  Reset address with 'A' command\n";

void help()
{
 for( int i=0; i<strlen(helpstr); i++ ) {
    BT.print( (char) pgm_read_byte(helpstr+i) );
  }
}

// called when address is found in BlinkM_scanI2CBus()
void scanfunc( byte addr, byte result ) {
  BT.print("addr: ");
  BT.print(addr,DEC);
  BT.print( (result==0) ? " found!":"       ");
  BT.print( (addr%4) ? "\t":"\n");
}

void lookForBlinkM() {
  BT.print("Looking for a BlinkM: ");
  int a = BlinkM_findFirstI2CDevice();
  if( a == -1 ) {
    BT.println("No I2C devices found");
  } else { 
    BT.print("Device found at addr ");
    BT.println( a, DEC);
    blinkm_addr = a;
  }
}

// arduino setup func
void setup()
{
    BT.begin(9600);                              

  if( BLINKM_ARDUINO_POWERED )
    BlinkM_beginWithPower();
  else
    BlinkM_begin();

  delay(100); // wait a bit for things to stabilize
  BlinkM_off(0);  // turn everyone off

  //BlinkM_setAddress( blinkm_addr );  // uncomment to set address
  delay(1000);

  help();
  
  lookForBlinkM();

  /*
  byte addr = BlinkM_getAddress(blinkm_addr);
  if( addr != blinkm_addr ) {
    if( addr == -1 ) 
      Serial.println("\r\nerror: no response");
    else if( addr != blinkm_addr ) {
      Serial.print("\r\nerror: addr mismatch, addr received: ");
      Serial.println(addr, HEX);
    }
  }
  */
  BT.print("cmd>");
}

// arduino loop func
void loop()
{


  
  int num;
  //read the serial port and create a string out of what you read
  if( readSerialString() ) {
    BT.println(serInStr);
    char cmd = serInStr[0];  // first char is command
    char* str = serInStr;
    while( *++str == ' ' );  // got past any intervening whitespace
    num = atoi(str);         // the rest is arguments (maybe)
    if( cmd == '?' ) {
      help();
    }
    else if( cmd == 'c' || cmd=='h' || cmd == 'C' || cmd == 'H' ) {
      byte a = toHex( str[0],str[1] );
      byte b = toHex( str[2],str[3] );
      byte c = toHex( str[4],str[5] );
      if( cmd == 'c' ) {
        BT.print("Fade to r,g,b:");
        BlinkM_fadeToRGB( blinkm_addr, a,b,c);
      } else if( cmd == 'h' ) {
        BT.print("Fade to h,s,b:");
        BlinkM_fadeToHSB( blinkm_addr, a,b,c);
      } else if( cmd == 'C' ) {
        BT.print("Random by r,g,b:");
        BlinkM_fadeToRandomRGB( blinkm_addr, a,b,c);
      } else if( cmd == 'H' ) {
        BT.print("Random by h,s,b:");
        BlinkM_fadeToRandomHSB( blinkm_addr, a,b,c);
      }
      BT.print(a,HEX); Serial.print(",");
      BT.print(b,HEX); Serial.print(",");
      BT.print(c,HEX); Serial.println();
    }
    else if( cmd == 'f' ) {
      BT.print("Set fade speed to:"); BT.println(num,DEC);
      BlinkM_setFadeSpeed( blinkm_addr, num);
    }
    else if( cmd == 't' ) {
      BT.print("Set time adj:"); BT.println(num,DEC);
      BlinkM_setTimeAdj( blinkm_addr, num);
    }
    else if( cmd == 'p' ) {
      BT.print("Play script #");
      BT.println(num,DEC);
      BlinkM_playScript( blinkm_addr, num,0,0 );
    }
    else if( cmd == 'o' ) {
      BT.println("Stop script");
      BlinkM_stopScript( blinkm_addr );
    }
    else if( cmd == 'g' ) {
      BT.print("Current color: ");
      byte r,g,b;
      BlinkM_getRGBColor( blinkm_addr, &r,&g,&b);
      BT.print("r,g,b:"); BT.print(r,HEX);
      BT.print(",");      BT.print(g,HEX);
      BT.print(",");      BT.println(b,HEX);
    }
    /*
      else if( cmd == 'W' ) { 
      Serial.println("Writing new eeprom script");
      for(int i=0; i<6; i++) {
      blinkm_script_line l = script_lines[i];
      BlinkM_writeScriptLine( blinkm_addr, 0, i, l.dur,
      l.cmd[0],l.cmd[1],l.cmd[2],l.cmd[3]);
      }
      }
    */
    else if( cmd == 'A' ) {
      if( num>0 && num<0xff ) {
        BT.print("Setting address to: ");
        BT.println(num,DEC);
        BlinkM_setAddress(num);
        blinkm_addr = num;
      } else if ( num == 0 ) {
        BT.println("Resetting address to default 9: ");
        blinkm_addr = 9;
        BlinkM_setAddress(blinkm_addr);
      }
    }
    else if( cmd == 'a' ) {
      BT.print("Address: ");
      num = BlinkM_getAddress(0); 
      Serial.println(num);
    }
    else if( cmd == '@' ) {
      BT.print("Will now talk on BlinkM address: ");
      BT.println(num,DEC);
      blinkm_addr = num;
    }
    else if( cmd == 'Z' ) { 
      BT.print("BlinkM version: ");
      num = BlinkM_getVersion(blinkm_addr);
      if( num == -1 )
        BT.println("couldn't get version");
      BT.print( (char)(num>>8) ); 
      BT.println( (char)(num&0xff) );
    }
    else if( cmd == 'B' ) {
      BT.print("Set startup mode:"); BT.println(num,DEC);
      BlinkM_setStartupParams( blinkm_addr, num, 0,0,1,0);
    }
    else if( cmd == 'i' ) {
      BT.print("get Inputs: ");
      byte inputs[4];
      BlinkM_getInputs(blinkm_addr, inputs); 
      for( byte i=0; i<4; i++ ) {
        BT.print(inputs[i],HEX);
        BT.print( (i<3)?',':'\n');
      }
    }
    else if( cmd == 's' ) { 
      lookForBlinkM();
    }
    else if( cmd == 'S' ) {
      BT.println("Scanning I2C bus from 1-100:");
      BlinkM_scanI2CBus(1,100, scanfunc);
      BT.println();
    }
    else if( cmd == 'R' ) {
      BT.println("Doing Factory Reset");
      blinkm_addr = 0x09;
      BlinkM_doFactoryReset();
    }
    else { 
      BT.println("Unrecognized cmd");
    }
    serInStr[0] = 0;  // say we've used the string
    BT.print("cmd>");
  } //if( readSerialString() )
  
}

//read a string from the serial and store it in an array
//you must supply the array variable
uint8_t readSerialString()
{
  if(!BT.available()) {
    return 0;
  }
  delay(10);  // wait a little for serial data

  memset( serInStr, 0, sizeof(serInStr) ); // set it all to zero
  int i = 0;
  while(BT.available() && i<serStrLen ) {
    serInStr[i] = BT.read();   // FIXME: doesn't check buffer overrun
    i++;
  }
  //serInStr[i] = 0;  // indicate end of read string
  return i;  // return number of chars read
}

// -----------------------------------------------------
// a really cheap strtol(s,NULL,16)
#include <ctype.h>
uint8_t toHex(char hi, char lo)
{
  uint8_t b;
  hi = toupper(hi);
  if( isxdigit(hi) ) {
    if( hi > '9' ) hi -= 7;      // software offset for A-F
    hi -= 0x30;                  // subtract ASCII offset
    b = hi<<4;
    lo = toupper(lo);
    if( isxdigit(lo) ) {
      if( lo > '9' ) lo -= 7;  // software offset for A-F
      lo -= 0x30;              // subtract ASCII offset
      b = b + lo;
      return b;
    } // else error
  }  // else error
  return 0;
}


