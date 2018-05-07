package edu.rosehulman.alumbajt.exam7coneseeking;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.Timer;
import java.util.TimerTask;

import edu.rosehulman.me435.AccessoryActivity;
import edu.rosehulman.me435.FieldGps;
import edu.rosehulman.me435.FieldGpsListener;
import edu.rosehulman.me435.FieldOrientation;
import edu.rosehulman.me435.FieldOrientationListener;
import edu.rosehulman.me435.NavUtils;


public class MainActivity extends AccessoryActivity implements FieldGpsListener, FieldOrientationListener {

    private TextView mHighLevelStateTextView;
    private TextView mGPSTextView;
    private TextView mTargetXYTextView;
    private TextView mTurnAmountTextView;
    private TextView mCommandTextView;
    private TextView mTargetHeadingTextView;

    private ViewFlipper mViewFlipper;

    private long mStateStartTime;
    private int mGPSCounter = 0;
    private double mCurrentGPSX;
    private double mCurrentGPSY;
    private double mCurrentHeading;
    private double mTargetX;
    private double mTargetY;

    private int DEFAULT_TURN = 180;
    private FieldGps mFieldGPS;
    private TextView mGoalDistanceTextView;

    private TextView mBall1Text, mBall2Text, mBall3Text;
    private double sensorHeading;
    private FieldOrientation mFieldOrientation;

    public void swapCallback(View view) {
        mViewFlipper.showNext();
    }

    @Override
    public void onSensorChanged(double fieldHeading, float[] orientationValues) {
        sensorHeading = fieldHeading;
    }

    public enum Color {White, Black, Blue, Red, Green, Yellow, None}

    public Color ball1Color = Color.None;
    public Color ball2Color = Color.None;
    public Color ball3Color = Color.None;

    private String HOME = "0 90 0 -90 90";
    private String BALL_OPEN = "32 134 -87 -180 13";
    private String BALL_FLICK = "55 134 -87 -180 13";

    @Override
    public void onLocationChanged(double x, double y, double heading, Location location) {
        mGPSCounter++;
        mGPSTextView.setText("( " + (int) x + ", " + (int) y + ") " + (int) sensorHeading + "° " + mGPSCounter);
        mCurrentGPSX = x;
        mCurrentGPSY = y;
        mCurrentHeading = sensorHeading;
    }


    enum State {
        READY_FOR_MISSION,
        INITIAL_STRAIGHT,
        GPS_SEEKING,
        BALL_REMOVAL_SCRIPT
    }


    private State mState = State.READY_FOR_MISSION;
    private Timer mTimer;
    public static final int LOOP_INTERVAL_MS = 100;
    private Handler mCommandHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setState(State.READY_FOR_MISSION);
        mBall1Text = (TextView) findViewById(R.id.ball1color);
        mBall2Text = (TextView) findViewById(R.id.ball2color);
        mBall3Text = (TextView) findViewById(R.id.ball3color);
        mHighLevelStateTextView = findViewById(R.id.highLevelSubstateTextView);
        mGPSTextView = findViewById(R.id.gPSTextView);
        mTargetXYTextView = findViewById(R.id.targetXYTextView);
        mTargetHeadingTextView = findViewById(R.id.targetHeadingTextView);
        mTurnAmountTextView = findViewById(R.id.turnAmountTextView);
        mCommandTextView = findViewById(R.id.commandTextView);
        mViewFlipper = findViewById(R.id.viewFlipper);
        mGoalDistanceTextView = findViewById(R.id.goalDistanceTextView);
        mFieldGPS = new FieldGps(this);
        mFieldOrientation = new FieldOrientation(this);
        sendCommand("ATTACH 111111");
    }

    @Override
    protected void onStart() {
        super.onStart();
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        loop();
                    }
                });
            }
        }, 0, LOOP_INTERVAL_MS);
        mFieldGPS.requestLocationUpdates(this);
        mFieldOrientation.registerListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mTimer.cancel();
        mTimer = null;
        mFieldGPS.removeUpdates();
        mFieldOrientation.unregisterListener();
    }

    protected void onCommandReceived(String receivedCommand) {
        super.onCommandReceived(receivedCommand);
        Toast.makeText(this, "Recieved", Toast.LENGTH_SHORT).show();
        if (receivedCommand.equals("1_W")) {
            mBall1Text.setText("        White");
            ball1Color = Color.White;
        } else if (receivedCommand.equals("1_K")) {
            mBall1Text.setText("       Black");
            ball1Color = Color.Black;
        } else if (receivedCommand.equals("1_Y")) {
            mBall1Text.setText("         Yellow");
            ball1Color = Color.Yellow;
        } else if (receivedCommand.equals("1_B")) {
            mBall1Text.setText("          Blue");
            ball1Color = Color.Blue;
        } else if (receivedCommand.equals("1_R")) {
            mBall1Text.setText("          Red");
            ball1Color = Color.Red;
        } else if (receivedCommand.equals("1_G")) {
            mBall1Text.setText("        Green");
            ball1Color = Color.Green;
        } else if (receivedCommand.equals("2_W")) {
            mBall2Text.setText("         White");
            ball2Color = Color.White;
        } else if (receivedCommand.equals("2_K")) {
            mBall2Text.setText("         Black");
            ball2Color = Color.Black;
        } else if (receivedCommand.equals("2_Y")) {
            mBall2Text.setText("          Yellow");
            ball2Color = Color.Yellow;
        } else if (receivedCommand.equals("2_B")) {
            mBall2Text.setText("          Blue");
            ball2Color = Color.Blue;
        } else if (receivedCommand.equals("2_R")) {
            mBall2Text.setText("          Red");
            ball2Color = Color.Red;
        } else if (receivedCommand.equals("2_G")) {
            mBall2Text.setText("         Green");
            ball2Color = Color.Green;
        } else if (receivedCommand.equals("2_B")) {
            mBall2Text.setText("          Blue");
            ball2Color = Color.Blue;
        } else if (receivedCommand.equals("2_R")) {
            mBall2Text.setText("          Red");
            ball2Color = Color.Red;
        } else if (receivedCommand.equals("2_G")) {
            mBall2Text.setText("         Green");
            ball2Color = Color.Green;
        } else if (receivedCommand.equals("3_W")) {
            mBall3Text.setText("White");
            ball3Color = Color.White;
        } else if (receivedCommand.equals("3_K")) {
            mBall3Text.setText("Black");
            ball3Color = Color.Black;
        } else if (receivedCommand.equals("3_Y")) {
            mBall3Text.setText("Yellow");
            ball3Color = Color.Yellow;
        } else if (receivedCommand.equals("3_B")) {
            mBall3Text.setText("Blue");
            ball3Color = Color.Blue;
        } else if (receivedCommand.equals("3_R")) {
            mBall3Text.setText("Red");
            ball3Color = Color.Red;
        } else if (receivedCommand.equals("3_G")) {
            mBall3Text.setText("Green");
            ball3Color = Color.Green;
        }
    }

    public void loop() {
        mHighLevelStateTextView.setText(mState.name() + " " + getStateTimeMS() / 1000);

        int leftSpeed;
        int rightSpeed;
        switch (mState) {
            case READY_FOR_MISSION:
                break;
            case INITIAL_STRAIGHT:
                break;
            case GPS_SEEKING:
                mGoalDistanceTextView.setText("" + (int) getDistanceToGoal());
                if (getDistanceToGoal() <= 20) {
                    setState(State.BALL_REMOVAL_SCRIPT);
                    sendCommand("WHEEL SPEED BRAKE 0 BRAKE 0");

                    mCommandTextView.setText("WHEEL SPEED BRAKE 0 BRAKE 0");
                } else {
                    mTargetHeadingTextView.setText(Math.round(NavUtils.getTargetHeading(mCurrentGPSX, mCurrentGPSY, mTargetX, mTargetY)) + "°");
                    double leftTurnAmount = Math.round(NavUtils.getLeftTurnHeadingDelta(mCurrentHeading, NavUtils.getTargetHeading(mCurrentGPSX, mCurrentGPSY, mTargetX, mTargetY)));
                    double rightTurnAmount = Math.round(NavUtils.getRightTurnHeadingDelta(mCurrentHeading, NavUtils.getTargetHeading(mCurrentGPSX, mCurrentGPSY, mTargetX, mTargetY)));

                    if (NavUtils.targetIsOnLeft(mCurrentGPSX, mCurrentGPSY, mCurrentHeading, mTargetX, mTargetY)) {
                        mTurnAmountTextView.setText("Left " + (int) leftTurnAmount + "°");
                        if (leftTurnAmount < 45) {
                            leftSpeed = DEFAULT_TURN;
                            rightSpeed = DEFAULT_TURN;
                        } else {
                            leftSpeed = DEFAULT_TURN - (int) leftTurnAmount * 2;
                            rightSpeed = DEFAULT_TURN;
                        }
                    } else {
                        mTurnAmountTextView.setText("Right " + (int) rightTurnAmount + "°");
                        if (rightTurnAmount < 45) {
                            leftSpeed = DEFAULT_TURN;
                            rightSpeed = DEFAULT_TURN;
                        } else {
                            leftSpeed = DEFAULT_TURN;
                            rightSpeed = DEFAULT_TURN - (int) rightTurnAmount * 2;
                        }
                    }
                    mCommandTextView.setText("WHEEL SPEED FORWARD " + leftSpeed + " FORWARD " + rightSpeed);
                    sendCommand("WHEEL SPEED FORWARD " + leftSpeed + " FORWARD " + rightSpeed);
                }
                break;
            case BALL_REMOVAL_SCRIPT:
                break;
        }
    }


    private double getDistanceToGoal() {
        return NavUtils.getDistance(mTargetX, mTargetY, mCurrentGPSX, mCurrentGPSY);
    }


    private long getStateTimeMS() {
        return System.currentTimeMillis() - mStateStartTime;
    }


    private void setState(State newState) {
        mStateStartTime = System.currentTimeMillis();
        switch (newState) {
            case READY_FOR_MISSION:
                break;
            case INITIAL_STRAIGHT:
                mCommandHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        setState(State.GPS_SEEKING);
                        //TODO: running straight stuff
                    }
                }, 4000);
                sendCommand("ATTACH 111111");
                mCommandTextView.setText("ATTACH 111111");
                break;
            case GPS_SEEKING:
                updateTargetXY(90, -50);
                //done in loop function
                break;
            case BALL_REMOVAL_SCRIPT:
                /** RECORD OF ARM SCRIPTS FROM APP
                 *
                 * Remove Ball 1
                 *  position: HOME
                 *  delay 2000
                 *  position: 32 134 -87 -180 13
                 *  delay 2000
                 *  position: 55 134 -87 -180 13
                 *  delay 1500
                 *  position home
                 *
                 */
                mCommandHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendCommand("ATTACH 111111");
                        mCommandTextView.setText("ATTACH 111111");
                    }
                }, 1000);
                mCommandHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendCommand("POSITION " + HOME);
                        mCommandTextView.setText("POSITION " + HOME);
                    }
                }, 2000);
                mCommandHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendCommand("POSITION " + BALL_OPEN);
                        mCommandTextView.setText("POSITION " + BALL_OPEN);
                    }
                }, 4000);
                mCommandHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendCommand("POSITION " + BALL_FLICK);
                        mCommandTextView.setText("POSITION " + BALL_FLICK);
                    }
                }, 6000);
                mCommandHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendCommand("POSITION " + HOME);
                        mCommandTextView.setText("POSITION " + HOME);
                    }
                }, 7500);
                setState(mState.READY_FOR_MISSION);
                mTargetXYTextView.setText("---");
                mGPSCounter = 0;
                mGPSTextView.setText("---");
                mTargetHeadingTextView.setText("---");
                mTurnAmountTextView.setText("---");
                mCommandTextView.setText("---");
                mGoalDistanceTextView.setText("---");
                break;
        }

        mState = newState;
    }


    public void handleReset(View view) {
//        Toast.makeText(this, "You pressed RESET", Toast.LENGTH_SHORT).show();
        setState(State.READY_FOR_MISSION);
        sendCommand("POSITION " + HOME);sendCommand("WHEEL SPEED BRAKE 0 BRAKE 0");
        mCommandTextView.setText("WHEEL SPEED BRAKE 0 BRAKE 0");
        mTargetXYTextView.setText("---");
        mGPSTextView.setText("---");
        mTargetHeadingTextView.setText("---");
        mTurnAmountTextView.setText("---");
        mCommandTextView.setText("---");
        mGoalDistanceTextView.setText("---");
    }


    public void handleGo(View view) {
//        Toast.makeText(this, "You pressed GO", Toast.LENGTH_SHORT).show();
        if (mState == State.READY_FOR_MISSION) {
            setState(State.INITIAL_STRAIGHT);
//            setState(State.BALL_REMOVAL_SCRIPT);
            updateTargetXY(90, -50);
        }
    }


    private void updateTargetXY(int x, int y) {
        mTargetX = x;
        mTargetY = y;
        mTargetXYTextView.setText("(" + x / 1 + ", " + y / 1 + ")");

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mFieldGPS.requestLocationUpdates(this);
    }

    public void handleSetOrigin(View view) {
        mFieldGPS.setCurrentLocationAsOrigin();
    }

    public void handleSetXAxis(View view) {
        mFieldGPS.setCurrentLocationAsLocationOnXAxis();
    }

    public void clearCallback(View view) {
        none1Callback(view);
        none2Callback(view);
        none3Callback(view);
    }

    public void testCallback(View view) {
        sendCommand("CUSTOM Perform ball test");
    }

    public void none1Callback(View view) {
        mBall1Text.setText("          ---");
        ball1Color = Color.None;
    }

    public void none2Callback(View view) {
        mBall2Text.setText("           ---");
        ball2Color = Color.None;
    }

    public void none3Callback(View view) {
        mBall3Text.setText("---");
        ball3Color = Color.None;
    }

    public void white1Callback(View view) {
        mBall1Text.setText("        White");
        ball1Color = Color.White;
    }

    public void white2Callback(View view) {
        mBall2Text.setText("         White");
        ball2Color = Color.White;
    }

    public void white3Callback(View view) {
        mBall3Text.setText("White");
        ball3Color = Color.White;
    }

    public void black1Callback(View view) {
        mBall1Text.setText("        Black");
        ball1Color = Color.Black;
    }

    public void black2Callback(View view) {
        mBall2Text.setText("         Black");
        ball3Color = Color.Black;
    }

    public void black3Callback(View view) {
        mBall3Text.setText("Black");
        ball3Color = Color.Black;
    }

    public void green1Callback(View view) {
        mBall1Text.setText("        Green");
        ball1Color = Color.Green;
    }

    public void green2Callback(View view) {
        mBall2Text.setText("         Green");
        ball2Color = Color.Green;
    }

    public void green3Callback(View view) {
        mBall3Text.setText("Green");
        ball3Color = Color.Green;
    }

    public void yellow1Callback(View view) {
        mBall1Text.setText("         Yellow");
        ball1Color = Color.Yellow;
    }

    public void yellow2Callback(View view) {
        mBall2Text.setText("          Yellow");
        ball2Color = Color.Yellow;
    }

    public void yellow3Callback(View view) {
        mBall3Text.setText("Yellow");
        ball3Color = Color.Yellow;
    }

    public void red1Callback(View view) {
        mBall1Text.setText("         Red");
        ball1Color = Color.Red;
    }

    public void red2Callback(View view) {
        mBall2Text.setText("          Red");
        ball2Color = Color.Red;
    }

    public void red3Callback(View view) {
        mBall3Text.setText("Red");
        ball3Color = Color.Red;
    }

    public void blue1Callback(View view) {
        mBall1Text.setText("         Blue");
        ball1Color = Color.Blue;
    }

    public void blue2Callback(View view) {
        mBall2Text.setText("          Blue");
        ball2Color = Color.Blue;
    }

    public void blue3Callback(View view) {
        mBall3Text.setText("Blue");
        ball3Color = Color.Blue;
    }

    public void goCallback(View view) {
        if (ball1Color == Color.Yellow || ball1Color == Color.Blue) {
            Toast.makeText(this, "Removing Ball 1...", Toast.LENGTH_SHORT).show();
            none1Callback(view);
        } else if (ball2Color == Color.Yellow || ball2Color == Color.Blue) {
            Toast.makeText(this, "Removing Ball 2...", Toast.LENGTH_SHORT).show();
            none2Callback(view);
        } else if (ball3Color == Color.Yellow || ball3Color == Color.Blue) {
            Toast.makeText(this, "Removing Ball 3...", Toast.LENGTH_SHORT).show();
            none3Callback(view);
        }
        //Drive forward for 1 second
        if (ball1Color == Color.White) {
            Toast.makeText(this, "Removing Ball 1...", Toast.LENGTH_SHORT).show();
            none1Callback(view);
        } else if (ball2Color == Color.White) {
            Toast.makeText(this, "Removing Ball 2...", Toast.LENGTH_SHORT).show();
            none2Callback(view);
        } else if (ball3Color == Color.White) {
            Toast.makeText(this, "Removing Ball 3...", Toast.LENGTH_SHORT).show();
            none3Callback(view);
        }
        //Drive forward for 1 second
        if (ball1Color == Color.Red || ball1Color == Color.Green) {
            Toast.makeText(this, "Removing Ball 1...", Toast.LENGTH_SHORT).show();
            none1Callback(view);
        } else if (ball2Color == Color.Red || ball2Color == Color.Green) {
            Toast.makeText(this, "Removing Ball 2...", Toast.LENGTH_SHORT).show();
            none2Callback(view);
        } else if (ball3Color == Color.Red || ball3Color == Color.Green) {
            Toast.makeText(this, "Removing Ball 3...", Toast.LENGTH_SHORT).show();
            none3Callback(view);
        }
        //Drive backward for 2 second
    }

    public void handleSetHeadingTo0(View view) {
        mFieldOrientation.setCurrentFieldHeading(0);
    }
}

