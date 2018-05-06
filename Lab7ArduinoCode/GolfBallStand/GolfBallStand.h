#ifndef GolfBallStand_h
#define GolfBallStand_h

#include "Arduino.h"

/**
 * IO pins on the Golf Ball Stand
 *	8 pin connector
 *	  blue   - 38 Led Location 1 Under
 *	  orange - 36 Led Location 1 Front
 *	  black  - 34 Led Location 3 Front
 *	  red    - 32 Blue
 *	  green  - 30 Led Location 3 Under
 *	  yellow - 28 Green
 *	  brown  - 26 Led Location 2 Under
 *	  white  - 24 Led Location 2 Front
 *	 
 *	6 pin connector
 *	  blue   - A2 56 Photo Location 1
 *	  yellow - A3 57 Photo External
 *	  green  - A4 58 Photo Location 3
 *	  orange - A5 59 Switch
 *	  black  - A6 60 Red
 *	  white  - A7 61 Photo Location 2
 */
#define PIN_LED_1_UNDER 38
#define PIN_LED_1_FRONT 36
#define PIN_LED_2_UNDER 26
#define PIN_LED_2_FRONT 24
#define PIN_LED_3_UNDER 30
#define PIN_LED_3_FRONT 34

// Analog pin numbers
#define PIN_PHOTO_1 2
#define PIN_PHOTO_2 7
#define PIN_PHOTO_3 4
#define PIN_PHOTO_EXTERNAL 3

#define PIN_RED 60
#define PIN_GREEN 28
#define PIN_BLUE 32

#define PIN_GOLF_BALL_STAND_SWITCH 59

// PNP transistors (colors) are on when the base is LOW.
// NPN transistors (led number) are on when the base is HIGH.
#define COLOR_TRANSISTOR_ON LOW
#define COLOR_TRANSISTOR_OFF HIGH
#define LED_TRANSISTOR_ON HIGH
#define LED_TRANSISTOR_OFF LOW

// LED colors
#define LED_OFF 0x00
#define LED_BLUE 0x01
#define LED_GREEN 0x02
#define LED_CYAN 0x03
#define LED_RED 0x04
#define LED_PURPLE 0x05
#define LED_YELLOW 0x06
#define LED_WHITE 0x07

// Ball colors.
#define BALL_NONE -1
#define BALL_BLACK 0
#define BALL_BLUE 1
#define BALL_GREEN 2
#define BALL_RED 3
#define BALL_YELLOW 4
#define BALL_WHITE 5

// Locations
#define LOCATION_EXTERNAL 0x00
#define LOCATION_1 0x01
#define LOCATION_2 0x02
#define LOCATION_3 0x04

// Front / Back location
#define LED_FRONT 0x01
#define LED_UNDER 0x02
#define LED_UNDER_AND_FRONT 0x03 // Optional constant

#define GBS_TIME_DELAY 20  

class GolfBallStand
{
  public:
    GolfBallStand();
    void setLedState(int ledColor, int location, int underOrFront);
    int getAnalogReading(int location);
    int determineBallColor(int location);

  protected:
    void _init(void);
};

#endif
