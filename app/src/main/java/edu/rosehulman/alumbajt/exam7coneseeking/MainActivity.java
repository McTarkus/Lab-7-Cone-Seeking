package edu.rosehulman.alumbajt.exam7coneseeking;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import edu.rosehulman.me435.AccessoryActivity;
import edu.rosehulman.me435.FieldGps;
import edu.rosehulman.me435.FieldGpsListener;
import edu.rosehulman.me435.NavUtils;


public class MainActivity extends AccessoryActivity implements FieldGpsListener {

    private TextView mHighLevelStateTextView;
    private TextView mGPSTextView;
    private TextView mTargetXYTextView;
    private TextView mTurnAmountTextView;
    private TextView mCommandTextView;
    private TextView mTargetHeadingTextView;

    private long mStateStartTime;
    private int mGPSCounter = 0;
    private double mCurrentGPSX;
    private double mCurrentGPSY;
    private double mCurrentHeading;
    private double mTargetX;
    private double mTargetY;

    private int DEFAULT_TURN = 255;
    private FieldGps mFieldGPS;
    private TextView mGoalDistanceTextView;

    private String HOME = "0 90 0 -90 90";
    private String BALL_OPEN = "32 134 -87 -180 13";
    private String BALL_FLICK = "55 134 -87 -180 13";

    @Override
    public void onLocationChanged(double x, double y, double heading, Location location) {
        mGPSCounter++;
        mGPSTextView.setText("( " + (int) x + ", " + (int) y + ") " + (int) heading + "° " + mGPSCounter);
        mCurrentGPSX = x;
        mCurrentGPSY = y;
        mCurrentHeading = heading;
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

        mHighLevelStateTextView = findViewById(R.id.highLevelSubstateTextView);
        mGPSTextView = findViewById(R.id.gPSTextView);
        mTargetXYTextView = findViewById(R.id.targetXYTextView);
        mTargetHeadingTextView = findViewById(R.id.targetHeadingTextView);
        mTurnAmountTextView = findViewById(R.id.turnAmountTextView);
        mCommandTextView = findViewById(R.id.commandTextView);
        mGoalDistanceTextView = findViewById(R.id.goalDistanceTextView);
        mFieldGPS = new FieldGps(this);
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
    }

    @Override
    protected void onStop() {
        super.onStop();
        mTimer.cancel();
        mTimer = null;
        mFieldGPS.removeUpdates();
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
                } else {
                    mTargetHeadingTextView.setText(Math.round(NavUtils.getTargetHeading(mCurrentGPSX, mCurrentGPSY, mTargetX, mTargetY)) + "°");
                    double leftTurnAmount = Math.round(NavUtils.getLeftTurnHeadingDelta(mCurrentHeading, NavUtils.getTargetHeading(mCurrentGPSX, mCurrentGPSY, mTargetX, mTargetY)));
                    double rightTurnAmount = Math.round(NavUtils.getRightTurnHeadingDelta(mCurrentHeading, NavUtils.getTargetHeading(mCurrentGPSX, mCurrentGPSY, mTargetX, mTargetY)));
                    if (NavUtils.targetIsOnLeft(mCurrentGPSX, mCurrentGPSY, mCurrentHeading, mTargetX, mTargetY)) {
                        mTurnAmountTextView.setText("Left " + (int) leftTurnAmount + "°");
                        leftSpeed = DEFAULT_TURN - (int) leftTurnAmount;
                        rightSpeed = DEFAULT_TURN;
                    } else {
                        mTurnAmountTextView.setText("Right " + (int) rightTurnAmount + "°");
                        leftSpeed = DEFAULT_TURN;
                        rightSpeed = DEFAULT_TURN - (int) rightTurnAmount;
                    }
                    mCommandTextView.setText("WHEEL SPEED FORWARD " + leftSpeed + " FORWARD " + rightSpeed);
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

                sendCommand("ATTACH 111111");
                sendCommand("POSITION " + HOME);
                mCommandHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendCommand("POSITION" + BALL_OPEN);
                    }
                }, 2000);
                mCommandHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendCommand("POSITION" + BALL_FLICK);
                    }
                }, 2000);
                mCommandHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendCommand("POSITION " + HOME);
                    }
                }, 1500);
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


}
