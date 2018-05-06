#include "Arduino.h"
#include "GolfBallStand.h"

GolfBallStand::GolfBallStand()
{
    _init();
}

void GolfBallStand::_init()
{
    pinMode(PIN_LED_1_UNDER, OUTPUT);
    pinMode(PIN_LED_1_FRONT, OUTPUT);
    pinMode(PIN_LED_2_UNDER, OUTPUT);
    pinMode(PIN_LED_2_FRONT, OUTPUT);
    pinMode(PIN_LED_3_UNDER, OUTPUT);
    pinMode(PIN_LED_3_FRONT, OUTPUT);
    pinMode(PIN_RED, OUTPUT);
    pinMode(PIN_GREEN, OUTPUT);
    pinMode(PIN_BLUE, OUTPUT);
    pinMode(PIN_GOLF_BALL_STAND_SWITCH, INPUT_PULLUP);
    digitalWrite(PIN_LED_1_UNDER, LED_TRANSISTOR_OFF);
    digitalWrite(PIN_LED_1_FRONT, LED_TRANSISTOR_OFF);
    digitalWrite(PIN_LED_2_UNDER, LED_TRANSISTOR_OFF);
    digitalWrite(PIN_LED_2_FRONT, LED_TRANSISTOR_OFF);
    digitalWrite(PIN_LED_3_UNDER, LED_TRANSISTOR_OFF);
    digitalWrite(PIN_LED_3_FRONT, LED_TRANSISTOR_OFF);
    digitalWrite(PIN_RED, COLOR_TRANSISTOR_OFF);
    digitalWrite(PIN_GREEN, COLOR_TRANSISTOR_OFF);
    digitalWrite(PIN_BLUE, COLOR_TRANSISTOR_OFF);
}

void GolfBallStand::setLedState(int ledColor, int location, int underOrFront)
{
    // Start by clearing off all LEDs and colors.
    digitalWrite(PIN_RED, COLOR_TRANSISTOR_OFF);
    digitalWrite(PIN_GREEN, COLOR_TRANSISTOR_OFF);
    digitalWrite(PIN_BLUE, COLOR_TRANSISTOR_OFF);
    digitalWrite(PIN_LED_1_UNDER, LED_TRANSISTOR_OFF);
    digitalWrite(PIN_LED_2_UNDER, LED_TRANSISTOR_OFF);
    digitalWrite(PIN_LED_3_UNDER, LED_TRANSISTOR_OFF);
    digitalWrite(PIN_LED_1_FRONT, LED_TRANSISTOR_OFF);
    digitalWrite(PIN_LED_2_FRONT, LED_TRANSISTOR_OFF);
    digitalWrite(PIN_LED_3_FRONT, LED_TRANSISTOR_OFF);

    // Decide which of the six LEDs to turn on.
    if ((location & LOCATION_1) && (underOrFront & LED_UNDER))
    {
        digitalWrite(PIN_LED_1_UNDER, LED_TRANSISTOR_ON);
    }
    if ((location & LOCATION_1) && (underOrFront & LED_FRONT))
    {
        digitalWrite(PIN_LED_1_FRONT, LED_TRANSISTOR_ON);
    }
    if ((location & LOCATION_2) && (underOrFront & LED_UNDER))
    {
        digitalWrite(PIN_LED_2_UNDER, LED_TRANSISTOR_ON);
    }
    if ((location & LOCATION_2) && (underOrFront & LED_FRONT))
    {
        digitalWrite(PIN_LED_2_FRONT, LED_TRANSISTOR_ON);
    }
    if ((location & LOCATION_3) && (underOrFront & LED_UNDER))
    {
        digitalWrite(PIN_LED_3_UNDER, LED_TRANSISTOR_ON);
    }
    if ((location & LOCATION_3) && (underOrFront & LED_FRONT))
    {
        digitalWrite(PIN_LED_3_FRONT, LED_TRANSISTOR_ON);
    }

    // Set the color.
    if (ledColor & LED_BLUE)
    {
        digitalWrite(PIN_BLUE, COLOR_TRANSISTOR_ON);
    }
    if (ledColor & LED_GREEN)
    {
        digitalWrite(PIN_GREEN, COLOR_TRANSISTOR_ON);
    }
    if (ledColor & LED_RED)
    {
        digitalWrite(PIN_RED, COLOR_TRANSISTOR_ON);
    }
}

int GolfBallStand::getAnalogReading(int location)
{
    int photoReading = -1;
    switch (location)
    {
    case LOCATION_1:
        photoReading = analogRead(PIN_PHOTO_1);
        break;
    case LOCATION_2:
        photoReading = analogRead(PIN_PHOTO_2);
        break;
    case LOCATION_3:
        photoReading = analogRead(PIN_PHOTO_3);
        break;
    case LOCATION_EXTERNAL:
        photoReading = analogRead(PIN_PHOTO_EXTERNAL);
        break;
    }
    return photoReading;
}

int GolfBallStand::determineBallColor(int location)
{
    int offReading = 0;
    int redReading = 0;
    int greenReading = 0;
    int blueReading = 0;
    int whiteReading = 0;
    int returnBallType = BALL_NONE;

    for (int i = 0; i < 30; i++)
    {

        setLedState(LED_OFF, location, LED_UNDER_AND_FRONT);
        delay(GBS_TIME_DELAY);
        offReading += getAnalogReading(location);

        setLedState(LED_RED, location, LED_UNDER_AND_FRONT);
        delay(GBS_TIME_DELAY);
        redReading += getAnalogReading(location);

        setLedState(LED_GREEN, location, LED_UNDER_AND_FRONT);
        delay(GBS_TIME_DELAY);
        greenReading += getAnalogReading(location);

        setLedState(LED_BLUE, location, LED_UNDER_AND_FRONT);
        delay(GBS_TIME_DELAY);
        blueReading += getAnalogReading(location);

        setLedState(LED_WHITE, location, LED_UNDER_AND_FRONT);
        delay(GBS_TIME_DELAY);
        whiteReading += getAnalogReading(location);
    }

    offReading = offReading/30;
    redReading = redReading/30;
    greenReading = greenReading/30;
    blueReading = blueReading/30;
    whiteReading = whiteReading/30;

    setLedState(LED_OFF, location, LED_UNDER_AND_FRONT);

    // Example of an overly simple solution.
    if (offReading < 500)
    {
        // Could use the external sensor instead of a hardcode 950.
        returnBallType = BALL_NONE;
    }
    else
    {
        if (location == LOCATION_1)
        {
            if (whiteReading < 300)
            {
                if (blueReading < 700)
                {
                    returnBallType = BALL_WHITE;
                }
                else
                {
                    returnBallType = BALL_YELLOW;
                }
            }
            else if (redReading < 700)
            {
                returnBallType = BALL_RED;
            }
            else if (greenReading < 700)
            {
                if (blueReading < 850)
                {
                    returnBallType = BALL_BLUE;
                }
                else
                {
                    returnBallType = BALL_GREEN;
                }
            }
            else
            {
                returnBallType = BALL_BLACK;
            }
        }
        else if (location == LOCATION_2)
        {
            if (whiteReading < 400)
            {
                if (blueReading < 800)
                {
                    returnBallType = BALL_WHITE;
                }
                else
                {
                    returnBallType = BALL_YELLOW;
                }
            }
            else if (redReading < 800)
            {
                returnBallType = BALL_RED;
            }
            else if (greenReading < 990)
            {
                if (blueReading < 970)
                {
                    returnBallType = BALL_BLUE;
                }
                else
                {
                    returnBallType = BALL_GREEN;
                }
            }
            else
            {
                returnBallType = BALL_BLACK;
            }
        }
        else if (location == LOCATION_3)
        {
            if (whiteReading < 300)
            {
                if (blueReading < 500)
                {
                    returnBallType = BALL_WHITE;
                }
                else
                {
                    returnBallType = BALL_YELLOW;
                }
            }
            else if (redReading < 700)
            {
                returnBallType = BALL_RED;
            }
            else if (greenReading < 950)
            {
                if (blueReading < 750)
                {
                    returnBallType = BALL_BLUE;
                }
                else
                {
                    returnBallType = BALL_GREEN;
                }
            }
            else
            {
                returnBallType = BALL_BLACK;
            }
        }
    }

    Serial.println();
    Serial.print("Readings for location ");
    Serial.println(location == LOCATION_3 ? 3 : location);
    Serial.print("  LED off reading   = ");
    Serial.println(offReading);
    Serial.print("  LED red reading   = ");
    Serial.println(redReading);
    Serial.print("  LED green reading = ");
    Serial.println(greenReading);
    Serial.print("  LED blue reading  = ");
    Serial.println(blueReading);
    Serial.print("  LED white reading = ");
    Serial.println(whiteReading);

    return returnBallType;
}
