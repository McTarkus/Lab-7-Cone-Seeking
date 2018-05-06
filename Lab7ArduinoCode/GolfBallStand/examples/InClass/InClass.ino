#include <GolfBallStand.h>

GolfBallStand stand;
int ballColor_1, ballColor_2, ballColor_3;

void setup() {
  Serial.begin(9600);
  stand.setLedState(LED_WHITE, LOCATION_1 | LOCATION_2 | LOCATION_3, LED_UNDER | LED_FRONT);
}

 void loop() {
  while(digitalRead(PIN_GOLF_BALL_STAND_SWITCH)) {
    // Do nothing until the switch is pressed.
  }
  Serial.print("---------------------------------");
  ballColor_1 = stand.determineBallColor(LOCATION_1);
  Serial.print("  Location 1 ball   = ");
  printBallColor(ballColor_1);
  delay(1000);
  ballColor_2 = stand.determineBallColor(LOCATION_2);
  Serial.print("  Location 2 ball   = ");
  printBallColor(ballColor_2);
  delay(1000);
  ballColor_3 = stand.determineBallColor(LOCATION_3);
  Serial.print("  Location 3 ball   = ");
  printBallColor(ballColor_3);
  delay(1000);
  stand.setLedState(LED_GREEN, LOCATION_3, LED_FRONT);
  Serial.print("---------------------------------\n\n");

  // Optional external reading (just as a reference).
  int externalPhotoCellReading = stand.getAnalogReading(LOCATION_EXTERNAL);
  Serial.print("External photo cell reading = ");
  Serial.println(externalPhotoCellReading);
}

void printBallColor(int ballColor) {
  switch (ballColor) {
    case BALL_NONE:
      Serial.println("No ball");
      break;
    case BALL_BLACK:
      Serial.println("Black ball");
      break;
    case BALL_BLUE:
      Serial.println("Blue ball");
      break;
    case BALL_GREEN:
      Serial.println("Green ball");
      break;
    case BALL_RED:
      Serial.println("Red ball");
      break;
    case BALL_YELLOW:
      Serial.println("Yellow ball");
      break;
    case BALL_WHITE:
      Serial.println("White ball");
      break;
  }
}



