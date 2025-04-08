package pedroPathing.examples;

import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.BezierPoint;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;
import com.pedropathing.util.Constants;
import com.pedropathing.util.Timer;
import com.qualcomm.hardware.ams.AMSColorSensor;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;

@Autonomous(name = "AutoRedBlueFast", group = "Examples")
public class AutoRedBlueFast extends basic {
    private Timer pathTimer, actionTimer, opmodeTimer;

    private int pathState;

    private final Pose startPose = new Pose(6.5, 64, Math.toRadians(0));
    private final Pose hang1Control = new Pose(50, 62, Math.toRadians(0));
    private final Pose hang1Pose = new Pose(40, 64, Math.toRadians(0));
    private final Pose leave1Control = new Pose(24, 50, Math.toRadians(0));
    private final Pose leave1Pose = new Pose(40, 36, Math.toRadians(0));
    private final Pose readypushControl = new Pose(60, 36, Math.toRadians(0));
    private final Pose readypushPose = new Pose(60, 24, Math.toRadians(270));

    // private final Pose push1Control = new Pose(90, 25, Math.toRadians(0));
    private final Pose push1stop = new Pose(17, 24, Math.toRadians(270));
    private final Pose ready2Control = new Pose(60, 29, Math.toRadians(270));
    private final Pose ready2Pose = new Pose(60, 15, Math.toRadians(180));
    private final Pose get1Pose = new Pose(16, 17.5, Math.toRadians(180));
    //    private final Pose readyHang2Pose = new Pose(20, 68, Math.toRadians(0));
    private final Pose hang2Pose = new Pose(40, 81.7, Math.toRadians(0));
    //    private final Pose readyget2Pose = new Pose(20, 68, Math.toRadians(310));
    private final Pose get2Pose = new Pose(16, 19, Math.toRadians(180));
    //    private final Pose readyHang3Pose = new Pose(20, 72, Math.toRadians(0));
    private final Pose hang3Pose = new Pose(40, 81, Math.toRadians(0));
    //    private final Pose readyget3Pose = new Pose(20, 72, Math.toRadians(310));
    private final Pose get3Pose = new Pose(16, 19, Math.toRadians(180));
    //    private final Pose readyHang3Pose = new Pose(20, 72, Math.toRadians(0));
    private final Pose hang4Pose = new Pose(40, 81, Math.toRadians(0));
    //    private final Pose readyget3Pose = new Pose(20, 72, Math.toRadians(310));
    private final Pose endPose = new Pose(14, 19, Math.toRadians(0));


    //   private Path ;
    private PathChain hang1, leave1, readypush, push1, ready2, get1, hang2, get2, hang3, get3, hang4, end;

    public void buildPaths() {
        hang1 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(startPose), new Point(hang1Pose)))
                .setLinearHeadingInterpolation(startPose.getHeading(), hang1Pose.getHeading())
                .build();

        leave1 = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(hang1Pose), new Point(leave1Control) ,new Point(leave1Pose)))
                .setLinearHeadingInterpolation(hang1Pose.getHeading(), leave1Pose.getHeading())
                .build();

        readypush = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(leave1Pose), new Point(readypushControl), new Point(readypushPose)))
                .setLinearHeadingInterpolation(leave1Pose.getHeading(), readypushPose.getHeading())
                .build();

        push1 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(readypushPose), new Point(push1stop)))
                .setLinearHeadingInterpolation(readypushPose.getHeading(), push1stop.getHeading())
                .build();

        ready2 = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(push1stop), new Point(ready2Control), new Point(ready2Pose)))
                .setLinearHeadingInterpolation(push1stop.getHeading(), ready2Pose.getHeading())
                .build();

//        push2 = follower.pathBuilder()
//                .addPath(new BezierCurve(new Point(ready2Pose), new Point(push2Control), new Point(push2stop)))
//                .setLinearHeadingInterpolation(ready2Pose.getHeading(), push2stop.getHeading())
//                .build();

        get1 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(ready2Pose), new Point(get1Pose)))
                .setLinearHeadingInterpolation(ready2Pose.getHeading(), get1Pose.getHeading())
                .build();

//        readyHang2 = follower.pathBuilder()
//                .addPath(new BezierLine(new Point(get1Pose), new Point(readyHang2Pose)))
//                .setLinearHeadingInterpolation(get1Pose.getHeading(), readyHang2Pose.getHeading())
//                .build();

        hang2 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(get1Pose), new Point(hang2Pose)))
                .setLinearHeadingInterpolation(get1Pose.getHeading(), hang2Pose.getHeading())
                .build();

//        readyget2 = follower.pathBuilder()
//                .addPath(new BezierLine(new Point(hang2Pose), new Point(readyget2Pose)))
//                .setLinearHeadingInterpolation(hang2Pose.getHeading(), readyget2Pose.getHeading())
//                .build();

        get2 = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(hang2Pose), new Point(get2Pose)))
                .setLinearHeadingInterpolation(hang2Pose.getHeading(), get2Pose.getHeading())
                .build();

//        readyHang3 = follower.pathBuilder()
//                .addPath(new BezierLine(new Point(get2Pose), new Point(readyHang3Pose)))
//                .setLinearHeadingInterpolation(get2Pose.getHeading(), readyHang3Pose.getHeading())
//                .build();

        hang3 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(get2Pose), new Point(hang3Pose)))
                .setLinearHeadingInterpolation(get2Pose.getHeading(), hang3Pose.getHeading())
                .build();

//        readyget3 = follower.pathBuilder()
//                .addPath(new BezierLine(new Point(hang3Pose), new Point(readyget3Pose)))
//                .setLinearHeadingInterpolation(hang3Pose.getHeading(), readyget3Pose.getHeading())
//                .build();

        get3 = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(hang3Pose), new Point(get3Pose)))
                .setLinearHeadingInterpolation(hang3Pose.getHeading(), get3Pose.getHeading())
                .build();

        hang4 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(get3Pose), new Point(hang4Pose)))
                .setLinearHeadingInterpolation(get3Pose.getHeading(), hang4Pose.getHeading())
                .build();

//        readyget3 = follower.pathBuilder()
//                .addPath(new BezierLine(new Point(hang3Pose), new Point(readyget3Pose)))
//                .setLinearHeadingInterpolation(hang3Pose.getHeading(), readyget3Pose.getHeading())
//                .build();

        end = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(hang4Pose), new Point(endPose)))
                .setLinearHeadingInterpolation(hang4Pose.getHeading(), endPose.getHeading())
                .build();


    }

    public void autonomousPathUpdate() {
        if (pathState == 0) {
            slideTarget = slide_hang;
            armTarget = arm_hang;
            clawCombo(claw_Close, intake_off);
            wristCombo(wrist_hang, 0);
            follower.followPath(hang1);
            setPathState(101);
        } else if (pathState == 101) {
            if (follower.getPose().getX() > 38) setPathState(102);
        } else if (pathState == 102) {
            clawCombo(claw_bigger, 0);
            slideTarget = smin;
            if (pathTimer.getElapsedTimeSeconds() >= 0.3) setPathState(1);
        } else if (pathState == 1) {
            if (!follower.isBusy()) {
                follower.followPath(leave1, true);
                setPathState(201);
            }
        } else if (pathState == 201) {
            if (pathTimer.getElapsedTimeSeconds() > 0.2) setPathState(202);
        } else if (pathState == 202) {
            if (!follower.isBusy()) {
                //FollowerConstants.pathEndTimeoutConstraint = 500;
                follower.followPath(readypush, true);
                setPathState(2);
            }
        }
        //
//        else if (pathState == 201) {
//            if (pathTimer.getElapsedTimeSeconds() >= 0.5) setPathState(202);
//        } else if (pathState == 202) {
//            //armTarget = 90;
//            setPathState(2);
//        }
        //
        else if (pathState == 2) {
            if (!follower.isBusy()) {
                follower.followPath(push1, true);
                //FollowerConstants.pathEndTimeoutConstraint = 50;
                setPathState(301);
            }
        } else if (pathState == 301) {
            if (!follower.isBusy()) {
                follower.followPath(ready2, true);
                setPathState(3);
            }
        } else if (pathState == 3) {
            if (!follower.isBusy()) {
                slideTarget = slide_wall;
                armTarget = arm_wall;
                wristCombo(wrist_wall, 0);
                clawCombo(claw_bigger, intake_off);
                setPathState(4);
            }
        } else if (pathState == 4) {
            if (pathTimer.getElapsedTimeSeconds() > 0.35) setPathState(5);
        } else if (pathState == 5) {
            clawCombo(claw_bigger, intake_on);
            follower.followPath(get1, true);
            setPathState(6);
        } else if (pathState == 6) {
            if (!follower.isBusy()) {
                setPathState(7);
            }
        }

        //2

        else if (pathState == 7) {
            clawCombo(claw_Close, intake_off);
            if (pathTimer.getElapsedTimeSeconds() >= clawCloseTime) setPathState(8);
        } else if (pathState == 8) {
            follower.followPath(hang2, true);
            armTarget = arm_hang;
            wristCombo(wrist_hang, 0);
            setPathState(9);
        } else if (pathState == 9) {
            if (pathTimer.getElapsedTimeSeconds() >= 0.3) setPathState(10);
        } else if (pathState == 10) {
            slideTarget = slide_hang;
            if (follower.getPose().getX() > 39) setPathState(11);
        } else if (pathState == 11) {
            clawCombo(claw_bigger, intake_off);
            slideTarget = slide_wall;
            if (pathTimer.getElapsedTimeSeconds() >= 0.1) setPathState(12);
        } else if (pathState == 12) {
            if (!follower.isBusy()) {
                follower.followPath(get2, true);
                setPathState(13);
            }
        } else if (pathState == 13) {
            if (pathTimer.getElapsedTimeSeconds() > 0.6) setPathState(14);
        } else if (pathState == 14) {
            armTarget = arm_wall;
            wristCombo(wrist_wall, 0);
            clawCombo(claw_bigger, intake_on);
            if (!follower.isBusy()) setPathState(15);
        }

        //3

        else if (pathState == 15) {
            clawCombo(claw_Close, intake_off);
            if (pathTimer.getElapsedTimeSeconds() >= clawCloseTime) setPathState(16);
        } else if (pathState == 16) {
            follower.followPath(hang3, true);
            armTarget = arm_hang;
            wristCombo(wrist_hang, 0);
            setPathState(17);
        } else if (pathState == 17) {
            if (pathTimer.getElapsedTimeSeconds() >= 0.3) setPathState(18);
        } else if (pathState == 18) {
            slideTarget = slide_hang;
            if (follower.getPose().getX() > 39) setPathState(19);
        } else if (pathState == 19) {
            clawCombo(claw_bigger, intake_off);
            slideTarget = slide_wall;
            if (pathTimer.getElapsedTimeSeconds() >= 0.1) setPathState(20);
        } else if (pathState == 20) {
            if (!follower.isBusy()) {
                follower.followPath(get3, true);
                setPathState(21);
            }
        } else if (pathState == 21) {
            if (pathTimer.getElapsedTimeSeconds() > 0.6) setPathState(22);
        } else if (pathState == 22) {
            armTarget = arm_wall;
            wristCombo(wrist_wall, 0);
            clawCombo(claw_bigger, intake_on);
            if (!follower.isBusy()) setPathState(23);
        }

        // 4

        else if (pathState == 23) {
            clawCombo(claw_Close, intake_off);
            if (pathTimer.getElapsedTimeSeconds() >= clawCloseTime) setPathState(24);
        } else if (pathState == 24) {
            follower.followPath(hang4, true);
            armTarget = arm_hang;
            wristCombo(wrist_hang, 0);
            setPathState(25);
        } else if (pathState == 25) {
            if (pathTimer.getElapsedTimeSeconds() >= 0.3) setPathState(26);
        } else if (pathState == 26) {
            slideTarget = slide_hang;
            if (follower.getPose().getX() > 39) setPathState(27);
        } else if (pathState == 27) {
            clawCombo(claw_bigger, intake_off);
            slideTarget = slide_wall;
            if (pathTimer.getElapsedTimeSeconds() >= 0.1) setPathState(28);
        } else if (pathState == 28) {
            if (!follower.isBusy()) {
                follower.followPath(end, true);
                setPathState(29);
            }
        } else if (pathState == 29) {
            if (pathTimer.getElapsedTimeSeconds() > 0.7) setPathState(30);
        } else if (pathState == 30) {
            armTarget = arm_wall;
            wristCombo(wrist_wall, 0);
            if (!follower.isBusy()) setPathState(-1);
        }
    }

    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }

    /**
     * This is the main loop of the OpMode, it will run repeatedly after clicking "Play".
     **/
    @Override
    public void robotLoop() {
        // These loop the movements of the robot
        follower.update();
        autonomousPathUpdate();

//        if (downCatch) autoFloor(); //Floor catching

        //DC Motor to position
        slideToPosition(slideTarget);
        armTurn2angle(armTarget);

        // Feedback to Driver Hub
//        telemetry.addData("path state", pathState);
//        telemetry.addData("x", follower.getPose().getX());
//        telemetry.addData("y", follower.getPose().getY());
//        telemetry.addData("heading", follower.getPose().getHeading());
//        telemetry.addData("pathEndTimeoutConstraint", FollowerConstants.pathEndTimeoutConstraint);
//        telemetry.update();
    }

    /**
     * This method is called once at the init of the OpMode.
     **/
    @Override
    public void robotInit() {
        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();
        follower.setStartingPose(startPose);
        buildPaths();
    }

    @Override
    public void robotInitLoop() {
        wristCombo(wrist_init, delta_init);
        clawCombo(claw_Close, intake_off);
        armTurn2angle(arm_init);
        slideToPosition(smin);
        armTarget = arm_init;
        slideTarget = smin;
    }

    @Override
    public void robotStart() {
        opmodeTimer.resetTimer();
        setPathState(0);
    }

    @Override
    public void robotStop() {
    }
}

