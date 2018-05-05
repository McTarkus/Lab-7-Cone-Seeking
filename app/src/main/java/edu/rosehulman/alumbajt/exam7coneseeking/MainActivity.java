package edu.rosehulman.alumbajt.exam7coneseeking;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import edu.rosehulman.me435.AccessoryActivity;
import edu.rosehulman.me435.FieldGpsListener;
import edu.rosehulman.me435.NavUtils;


public class MainActivity extends AccessoryActivity implements FieldGpsListener {

    private TextView mHighLevelStateTextView;
    private TextView mMissionSubStateTextView;
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
    }

    @Override
    protected void onStop() {
        super.onStop();
        mTimer.cancel();
        mTimer = null;
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
                break;
            case BALL_REMOVAL_SCRIPT:
                //TODO: Figure out ball removal script
                break;
        }


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
                        onLocationChanged(60, -25, -30, null);
                    }
                }, 4000);
                break;
            case GPS_SEEKING:
                break;
            case BALL_REMOVAL_SCRIPT:
                break;
        }

        mState = newState;
    }


    public void handleReset(View view) {
//        Toast.makeText(this, "You pressed RESET", Toast.LENGTH_SHORT).show();
        setState(State.READY_FOR_MISSION);
        mTargetXYTextView.setText("---");
        mGPSCounter = 0;
        mGPSTextView.setText("---");
        mTargetHeadingTextView.setText("---");
        mTurnAmountTextView.setText("---");
        mCommandTextView.setText("---");
    }


    public void handleGo(View view) {
//        Toast.makeText(this, "You pressed GO", Toast.LENGTH_SHORT).show();
        if (mState == State.READY_FOR_MISSION) {
            setState(State.INITIAL_STRAIGHT);
            updateTargetXY(90, -50);
        }
    }


    public void handleMissionComplete(View view) {
//        Toast.makeText(this, "You pressed MISSION COMPLETE", Toast.LENGTH_SHORT).show();
        if (mState == State.GPS_SEEKING) {
            setState(mState.READY_FOR_MISSION);
            mTargetXYTextView.setText("---");
            mGPSCounter = 0;
            mGPSTextView.setText("---");
            mTargetHeadingTextView.setText("---");
            mTurnAmountTextView.setText("---");
            mCommandTextView.setText("---");
        }
    }

    private void updateTargetXY(int x, int y) {
        mTargetX = x;
        mTargetY = y;
        mTargetXYTextView.setText("(" + x / 1 + ", " + y / 1 + ")");

    }

}
