package pedroPathing.examples;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;
import com.pedropathing.util.Constants;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;
import pedroPathing.examples.basic;

@Autonomous(name = "AutoYellow4", group = "Examples")
public class AutoYellow4 extends basic {
    private Timer pathTimer, actionTimer, opmodeTimer;
    private int pathState;
    private final Pose startPose = new Pose(9, 111, Math.toRadians(270));
    private final Pose scorePose = new Pose(15, 128, Math.toRadians(315));
    private final Pose pickup1Pose = new Pose(35, 118, Math.toRadians(0));
    private final Pose pickup2Pose = new Pose(35, 128, Math.toRadians(0));
    private final Pose pickup3Pose = new Pose(29, 130, Math.toRadians(20));
    private final Pose pickup4Pose = new Pose(62, 95, Math.toRadians(270));
    private final Pose parkControlPose = new Pose(63, 127, Math.toRadians(270));
    private final Pose parkPose = new Pose(63, 90, Math.toRadians(270));

    private Path park;
    private PathChain grab1, grab2, grab3, grab4, score1, score2, score3, score4, score5;

    public void buildPaths() {
        score1 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(startPose), new Point(scorePose)))
                .setLinearHeadingInterpolation(startPose.getHeading(), scorePose.getHeading())
                .build();

        grab1 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(scorePose), new Point(pickup1Pose)))
                .setLinearHeadingInterpolation(scorePose.getHeading(), pickup1Pose.getHeading())
                .build();

        score2 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(pickup1Pose), new Point(scorePose)))
                .setLinearHeadingInterpolation(pickup1Pose.getHeading(), scorePose.getHeading())
                .build();

        grab2 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(scorePose), new Point(pickup2Pose)))
                .setLinearHeadingInterpolation(scorePose.getHeading(), pickup2Pose.getHeading())
                .build();

        score3 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(pickup2Pose), new Point(scorePose)))
                .setLinearHeadingInterpolation(pickup2Pose.getHeading(), scorePose.getHeading())
                .build();

        grab3 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(scorePose), new Point(pickup3Pose)))
                .setLinearHeadingInterpolation(scorePose.getHeading(), pickup3Pose.getHeading())
                .build();

        score4 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(pickup3Pose), new Point(scorePose)))
                .setLinearHeadingInterpolation(pickup3Pose.getHeading(), scorePose.getHeading())
                .build();

//        grab4 = follower.pathBuilder()
//                .addPath(new BezierLine(new Point(scorePose), new Point(pickup4Pose)))
//                .setLinearHeadingInterpolation(scorePose.getHeading(), pickup4Pose.getHeading())
//                .build();
//
//        score5 = follower.pathBuilder()
//                .addPath(new BezierLine(new Point(pickup4Pose), new Point(scorePose)))
//                .setLinearHeadingInterpolation(pickup4Pose.getHeading(), scorePose.getHeading())
//                .build();

        park = new Path(new BezierCurve(new Point(scorePose), new Point(parkControlPose), new Point(parkPose)));
        park.setLinearHeadingInterpolation(scorePose.getHeading(), parkPose.getHeading());
    }

    /**
     * This switch is called continuously and runs the pathing, at certain points, it triggers the action state.
     * Everytime the switch changes case, it will reset the timer. (This is because of the setPathState() method)
     * The followPath() function sets the follower to run the specific path, but does NOT wait for it to finish before moving on.
     */
    public void autonomousPathUpdate() {
        // 1
        if (pathState == 0) {
            slideTarget = slide_bucket;
            armTarget = arm_bucket;
            wristCombo(wrist_bucket, 0);
            setPathState(101);
        } else if (pathState == 101) {
            if (pathTimer.getElapsedTimeSeconds() >= 0.3) setPathState(102);
        } else if (pathState == 102) {
            follower.followPath(score1, true);
            setPathState(103);
        } else if (pathState == 103) {
            if (!follower.isBusy()) setPathState(104);
        } else if (pathState == 104) {
            if (pathTimer.getElapsedTimeSeconds() >= 0.7) setPathState(105);
        } else if (pathState == 105) {
            clawCombo(claw_Open, -intake_on);
            if (pathTimer.getElapsedTimeSeconds() >= putBucketTime) setPathState(1);
        }

        // 2

        else if (pathState == 1) {
            if (!follower.isBusy()) {
                slideTarget = 35;
                armTarget = -5;
                wristCombo(wrist_down, 0);
                clawCombo(claw_bigger, intake_on);
                follower.followPath(grab1, true);
                setPathState(201);
            }
        } else if (pathState == 201) {
            if (!follower.isBusy()) setPathState(202);
        } else if (pathState == 202) {
            armTarget = arm_catch;
            if (pathTimer.getElapsedTimeSeconds() >= 0.4) setPathState(203);
        } else if (pathState == 203) {
            clawCombo(claw_Close, intake_off);
            if (pathTimer.getElapsedTimeSeconds() >= clawCloseTime) setPathState(2);
        } else if (pathState == 2) {
            slideTarget = slide_bucket;
            armTarget = arm_bucket;
            wristCombo(wrist_bucket, 0);
            if (!follower.isBusy()) {
                follower.followPath(score2, true);
                setPathState(301);
            }
        } else if (pathState == 301) {
            if (!follower.isBusy()) setPathState(302);
        } else if (pathState == 302) {
            if (pathTimer.getElapsedTimeSeconds() >= 0.1) setPathState(303);
        } else if (pathState == 303) {
            clawCombo(claw_Open, -intake_on);
            if (pathTimer.getElapsedTimeSeconds() >= putBucketTime) setPathState(3);
        }

        // 3

        else if (pathState == 3) {
            if (!follower.isBusy()) {
                slideTarget = 35;
                armTarget = -5;
                wristCombo(wrist_down, 0);
                clawCombo(claw_bigger, intake_on);
                follower.followPath(grab2, true);
                setPathState(401);
            }
        } else if (pathState == 401) {
            if (!follower.isBusy()) setPathState(402);
        } else if (pathState == 402) {
            armTarget = arm_catch;
            if (pathTimer.getElapsedTimeSeconds() >= 0.4) setPathState(403);
        } else if (pathState == 403) {
            clawCombo(claw_Close, intake_off);
            if (pathTimer.getElapsedTimeSeconds() >= clawCloseTime) setPathState(4);
        } else if (pathState == 4) {
            slideTarget = slide_bucket;
            armTarget = arm_bucket;
            wristCombo(wrist_bucket, 0);
            if (!follower.isBusy()) {
                follower.followPath(score3, true);
                setPathState(501);
            }
        } else if (pathState == 501) {
            if (!follower.isBusy()) setPathState(502);
        } else if (pathState == 502) {
            if (pathTimer.getElapsedTimeSeconds() >= 0.1) setPathState(503);
        } else if (pathState == 503) {
            clawCombo(claw_Open, -intake_on);
            if (pathTimer.getElapsedTimeSeconds() >= putBucketTime) setPathState(5);
        }

        // 4

        else if (pathState == 5) {
            if (!follower.isBusy()) {
                slideTarget = 35;
                armTarget = -5;
                wristCombo(wrist_down, 0);
                clawCombo(claw_bigger, intake_on);
                follower.followPath(grab3, true);
                setPathState(601);
            }
        } else if (pathState == 601) {
            if (!follower.isBusy()) setPathState(602);
        } else if (pathState == 602) {
            downCatch = true;
            if (pathTimer.getElapsedTimeSeconds() >= 0.5) setPathState(603);
        } else if (pathState == 603) {
            slideTarget = 65;
            setPathState(604);
        } else if (pathState == 604) {
            if (slidePosNow >= 62) setPathState(605);
        } else if (pathState == 605) {
            downCatch = false;
            clawCombo(claw_Close, intake_off);
            if (pathTimer.getElapsedTimeSeconds() >= clawCloseTime) setPathState(606);
        } else if (pathState == 606) {
            slideTarget = smin;
            armTarget = -15;
            if (pathTimer.getElapsedTimeSeconds() >= 1) setPathState(6);
        } else if (pathState == 6) {
            slideTarget = slide_bucket;
            armTarget = arm_bucket;
            wristCombo(wrist_bucket, 0);
            if (!follower.isBusy()) {
                follower.followPath(score4, true);
                setPathState(701);
            }
        } else if (pathState == 701) {
            if (!follower.isBusy()) setPathState(702);
        } else if (pathState == 702) {
            if (pathTimer.getElapsedTimeSeconds() >= 0.1) setPathState(703);
        } else if (pathState == 703) {
            clawCombo(claw_Open, -intake_on);
            if (pathTimer.getElapsedTimeSeconds() >= 0.5) setPathState(7);
        }

        // End

        else if (pathState == 7) {
            slideTarget = smin;
            armTarget = 52;
            wristCombo(wrist_wall, 0);
            clawCombo(claw_Open, intake_off);
            if (!follower.isBusy()) {
                follower.followPath(park, true);
                setPathState(8);
            }
        } else if (pathState == 8) {
            if (!follower.isBusy()) {
                setPathState(-1);
            }
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

        if (downCatch) autoFloor();

        //DC Motor to position
        slideToPosition(slideTarget);
        armTurn2angle(armTarget);

        // Feedback to Driver Hub
        telemetry.addData("path state", pathState);
//        telemetry.addData("x", follower.getPose().getX());
//        telemetry.addData("y", follower.getPose().getY());
//        telemetry.addData("heading", follower.getPose().getHeading());
        telemetry.addData("armTarget", armTarget);
        telemetry.update();
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

    /**
     * This method is called continuously after Init while waiting for "play".
     **/
    @Override
    public void robotInitLoop() {
        wristCombo(wrist_init, delta_init);
        clawCombo(claw_Close, intake_off);
        armTurn2angle(arm_init);
        slideToPosition(smin);
        armTarget = arm_init;
        slideTarget = smin;
    }

    /**
     * This method is called once at the start of the OpMode.
     * It runs all the setup actions, including building paths and starting the path system
     **/
    @Override
    public void robotStart() {
        opmodeTimer.resetTimer();
        setPathState(0);
    }

    @Override
    public void robotStop() {
    }
}

