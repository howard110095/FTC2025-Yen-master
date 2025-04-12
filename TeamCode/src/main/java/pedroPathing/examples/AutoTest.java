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

@Autonomous(name = "AutoTest", group = "Examples")
public class AutoTest extends OpMode {
    private Timer pathTimer, actionTimer, opmodeTimer;
    private Follower follower;
    private double wrist_pos;
    private int pathState;

    private final Pose startPose = new Pose(9, 62, Math.toRadians(0));
    private final Pose hang1Control = new Pose(45, 62, Math.toRadians(0));
    private final Pose hang1Pose = new Pose(38, 62, Math.toRadians(0));
    private final Pose readypushControl = new Pose(20, 46, Math.toRadians(320));
    private final Pose readypushPose = new Pose(60, 37, Math.toRadians(270));

    // private final Pose push1Control = new Pose(90, 25, Math.toRadians(0));
    private final Pose push1stop = new Pose(15, 33, Math.toRadians(270));
    private final Pose ready2Control = new Pose(60, 36, Math.toRadians(270));
    private final Pose ready2Pose = new Pose(60, 26, Math.toRadians(270));
    private final Pose push2Control = new Pose(10, 21, Math.toRadians(270));
    private final Pose push2stop = new Pose(10, 32, Math.toRadians(270));
    private final Pose getPose = new Pose(10, 29.5, Math.toRadians(270));
    private final Pose readyHangPose = new Pose(20, 65, Math.toRadians(0));
    private final Pose hang2Control = new Pose(60, 65, Math.toRadians(0));
    private final Pose hang2Pose = new Pose(40, 65, Math.toRadians(0));


    //   private Path ;
    private PathChain hang1, readypush, push1, ready2, push2, pick3, put3, get1, hang2, get2, readyHang2;


    private PathChain pathing1, pathing2,pathing3, pathing4;

    public void buildPaths() {
        hang1 = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(startPose), new Point(hang1Control), new Point(hang1Pose)))
                .setLinearHeadingInterpolation(startPose.getHeading(), hang1Pose.getHeading())
                .build();

        readypush = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(hang1Pose), new Point(readypushControl), new Point(readypushPose)))
                .setLinearHeadingInterpolation(hang1Pose.getHeading(), readypushPose.getHeading())
                .build();

        push1 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(readypushPose), new Point(push1stop)))
                .setLinearHeadingInterpolation(readypushPose.getHeading(), push1stop.getHeading())
                .build();

        ready2 = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(push1stop), new Point(ready2Control), new Point(ready2Pose)))
                .setLinearHeadingInterpolation(push1stop.getHeading(), ready2Pose.getHeading())
                .build();

        push2 = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(ready2Pose), new Point(push2Control), new Point(push2stop)))
                .setLinearHeadingInterpolation(ready2Pose.getHeading(), push2stop.getHeading())
                .build();

//        pick3 = follower.pathBuilder()
//                .addPath(new BezierLine(new Point(push2stop), new Point(pick3Pose)))
//                .setLinearHeadingInterpolation(push2stop.getHeading(), pick3Pose.getHeading())
//                .build();
//
//        put3 = follower.pathBuilder()
//                .addPath(new BezierLine(new Point(pick3Pose), new Point(put3Pose)))
//                .setLinearHeadingInterpolation(pick3Pose.getHeading(), put3Pose.getHeading())
//                .build();

        get1 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(push2stop), new Point(getPose)))
                .setLinearHeadingInterpolation(push2stop.getHeading(), getPose.getHeading())
                .build();

        readyHang2 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(getPose), new Point(readyHangPose)))
                .setLinearHeadingInterpolation(getPose.getHeading(), readyHangPose.getHeading())
                .build();

        hang2 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(readyHangPose), new Point(hang2Pose)))
                .setLinearHeadingInterpolation(readyHangPose.getHeading(), hang2Pose.getHeading())
                .build();

        get2 = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(hang2Pose), new Point(getPose)))
                .setLinearHeadingInterpolation(hang2Pose.getHeading(), getPose.getHeading())
                .build();
    }

    /**
     * This switch is called continuously and runs the pathing, at certain points, it triggers the action state.
     * Everytime the switch changes case, it will reset the timer. (This is because of the setPathState() method)
     * The followPath() function sets the follower to run the specific path, but does NOT wait for it to finish before moving on.
     */
    public void autonomousPathUpdate() {
        if (pathState == 0) {
            follower.followPath(hang1);
            setPathState(101);
        } else if (pathState == 101) {
            if (pathTimer.getElapsedTimeSeconds() >= 1.2) setPathState(102);
        } else if (pathState == 102) {
            if (pathTimer.getElapsedTimeSeconds() >= 0.1) setPathState(1);
        } else if (pathState == 1) {
            if (!follower.isBusy()) {
                follower.followPath(readypush, true);
                setPathState(201);
            }
        } else if (pathState == 201) {
            if (pathTimer.getElapsedTimeSeconds() >= 0.5) setPathState(202);
        } else if (pathState == 202) {
            setPathState(2);
        } else if (pathState == 2) {
            if (!follower.isBusy()) {
                follower.followPath(push1, true);
                setPathState(301);
            }
        } else if (pathState == 301) {
            if (!follower.isBusy()) {
                follower.followPath(ready2, true);
                setPathState(3);
            }
        } else if (pathState == 3) {
            if (!follower.isBusy()) {
                follower.followPath(push2, true);
                setPathState(4);
            }
        } else if (pathState == 4) {
            if (!follower.isBusy()) {
                setPathState(5);
            }
        } else if (pathState == 5) {
            if (pathTimer.getElapsedTimeSeconds() >= 0.5) setPathState(6);
        } else if (pathState == 6) {
            if (!follower.isBusy()) {
                follower.followPath(get1, true);
                setPathState(701);
            }
        } else if (pathState == 701) {
            if (!follower.isBusy()) {
                follower.followPath(get1, true);
                setPathState(7);
            }
        } else if (pathState == 7) {
            if (pathTimer.getElapsedTimeSeconds() >= 0.3) setPathState(8);
        } else if (pathState == 8) {
            follower.followPath(readyHang2, true);
            setPathState(9);
        } else if (pathState == 9) {
            if (!follower.isBusy()) {
                follower.followPath(hang2, true);
                setPathState(10);
            }
        } else if (pathState == 10) {
            if (pathTimer.getElapsedTimeSeconds() >= 1) setPathState(11);
        } else if (pathState == 11) {
            if (pathTimer.getElapsedTimeSeconds() >= 0.1) setPathState(12);
        } else if (pathState == 12) {
            if (!follower.isBusy()) {
                follower.followPath(get2, true);
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
    public void loop() {
        // These loop the movements of the robot
        follower.update();
        autonomousPathUpdate();

        // Feedback to Driver Hub
        telemetry.addData("path state", pathState);
        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData("heading", follower.getPose().getHeading());
        telemetry.update();
    }

    /**
     * This method is called once at the init of the OpMode.
     **/
    @Override
    public void init() {
        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();

        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);
        follower.setStartingPose(startPose);
        buildPaths();
    }


    @Override
    public void init_loop() {
    }


    @Override
    public void start() {
        opmodeTimer.resetTimer();
        setPathState(0);
    }


}

