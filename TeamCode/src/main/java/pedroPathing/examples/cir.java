//package pedroPathing.examples;
//
//import androidx.core.math.MathUtils;
//
//import com.pedropathing.follower.Follower;
//import com.pedropathing.localization.Pose;
//import com.pedropathing.pathgen.BezierCurve;
//import com.pedropathing.pathgen.BezierLine;
//import com.pedropathing.pathgen.BezierPoint;
//import com.pedropathing.pathgen.Path;
//import com.pedropathing.pathgen.PathChain;
//import com.pedropathing.pathgen.Point;
//import com.pedropathing.util.Constants;
//import com.pedropathing.util.Timer;
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//import com.qualcomm.robotcore.eventloop.opmode.OpMode;
//
//import pedroPathing.constants.FConstants;
//import pedroPathing.constants.LConstants;
//
//@Autonomous(name = "cir", group = "Examples")
//public class cir extends OpMode {
//
//    private Follower follower;
//    private Timer pathTimer, actionTimer, opmodeTimer;
//
//    private int pathState;
//
//    private final Pose startPose = new Pose(9, 111, Math.toRadians(270));
//    private final Pose scorePose = new Pose(16, 125, Math.toRadians(315));
//    private final Pose pickup1Pose = new Pose(30, 112, Math.toRadians(0));
//    private final Pose pickup2Pose = new Pose(30, 121, Math.toRadians(0));
//    private final Pose pickup3Pose = new Pose(33, 130, Math.toRadians(30));
//    private final Pose parkPose = new Pose(60, 98, Math.toRadians(270));
//    private final Pose parkControlPose = new Pose(60, 90, Math.toRadians(270));
//
//    private Path scorePreload, park, grabPickup1, grabPickup2, grabPickup3, scorePickup1, scorePickup2, scorePickup3;
//    private PathChain circle;
//
//    public void buildPaths() {
//
//        scorePreload = new Path(new BezierLine(new Point(startPose), new Point(scorePose)));
//        scorePreload.setLinearHeadingInterpolation(startPose.getHeading(), scorePose.getHeading());
//
//        grabPickup1 = new Path(new BezierLine(new Point(scorePose), new Point(pickup1Pose)));
//        grabPickup1.setLinearHeadingInterpolation(scorePose.getHeading(), pickup2Pose.getHeading());
//
//        scorePickup1 = new Path(new BezierLine(new Point(pickup1Pose), new Point(scorePose)));
//        scorePickup1.setLinearHeadingInterpolation(pickup1Pose.getHeading(), scorePose.getHeading());
//
//        grabPickup2 = new Path(new BezierLine(new Point(scorePose), new Point(pickup2Pose)));
//        grabPickup2.setLinearHeadingInterpolation(scorePose.getHeading(), pickup2Pose.getHeading());
//
//
//        scorePickup2 = new Path(new BezierLine(new Point(pickup2Pose), new Point(scorePose)));
//        scorePickup2.setLinearHeadingInterpolation(pickup2Pose.getHeading(), scorePose.getHeading());
//
//
//        grabPickup3 = new Path(new BezierLine(new Point(scorePose), new Point(pickup3Pose)));
//        grabPickup3.setLinearHeadingInterpolation(scorePose.getHeading(), pickup3Pose.getHeading());
//
//
//        scorePickup3 = new Path(new BezierLine(new Point(pickup3Pose), new Point(scorePose)));
//        scorePickup3.setLinearHeadingInterpolation(pickup3Pose.getHeading(), scorePose.getHeading());
//
//
//        park = new Path(new BezierCurve(new Point(scorePose), new Point(parkControlPose), new Point(parkPose)));
//        park.setLinearHeadingInterpolation(scorePose.getHeading(), parkPose.getHeading());
//
//
//    }
//
//    /**
//     * This switch is called continuously and runs the pathing, at certain points, it triggers the action state.
//     * Everytime the switch changes case, it will reset the timer. (This is because of the setPathState() method)
//     * The followPath() function sets the follower to run the specific path, but does NOT wait for it to finish before moving on.
//     */
//    public void autonomousPathUpdate() {
//        if (pathState == 0) {
//            follower.followPath(scorePreload);
//            setPathState(1);
//        } else if (pathState == 1) {
//            if (!follower.isBusy()) {
//                follower.followPath(grabPickup1, true);
//                setPathState(2);
//            }
//        } else if (pathState == 2) {
//            if (!follower.isBusy()) {
//                follower.followPath(scorePickup1, true);
//                setPathState(3);
//            }
//        } else if (pathState == 3) {
//            if (!follower.isBusy()) {
//                follower.followPath(grabPickup2, true);
//                setPathState(4);
//            }
//        } else if (pathState == 4) {
//            if (!follower.isBusy()) {
//                follower.followPath(scorePickup2, true);
//                setPathState(5);
//            }
//        } else if (pathState == 5) {
//            if (!follower.isBusy()) {
//                follower.followPath(grabPickup3, true);
//                setPathState(6);
//            }
//        } else if (pathState == 6) {
//            if (!follower.isBusy()) {
//                follower.followPath(scorePickup3, true);
//                setPathState(7);
//            }
//        } else if (pathState == 7) {
//            if (!follower.isBusy()) {
//                follower.followPath(park, true);
//                setPathState(8);
//            }
//        } else if (pathState == 8) {
//            if (!follower.isBusy()) {
//                setPathState(-1);
//            }
//        }
//    }
//
//    public void setPathState(int pState) {
//        pathState = pState;
//        pathTimer.resetTimer();
//    }
//
//    /**
//     * This is the main loop of the OpMode, it will run repeatedly after clicking "Play".
//     **/
//    @Override
//    public void loop() {
//
//        // These loop the movements of the robot
//        follower.update();
//        autonomousPathUpdate();
//
//        // Feedback to Driver Hub
//        telemetry.addData("path state", pathState);
//        telemetry.addData("x", follower.getPose().getX());
//        telemetry.addData("y", follower.getPose().getY());
//        telemetry.addData("heading", follower.getPose().getHeading());
//        telemetry.update();
//    }
//
//    /**
//     * This method is called once at the init of the OpMode.
//     **/
//    @Override
//    public void init() {
//        pathTimer = new Timer();
//        opmodeTimer = new Timer();
//        opmodeTimer.resetTimer();
//
//        circle = follower.pathBuilder()
//                .addPath(new BezierLine(new Point(1, 1, 1), new Point(1, 1, 1)))
//                .build();
//
//        Constants.setConstants(FConstants.class, LConstants.class);
//        follower = new Follower(hardwareMap);
//        follower.setStartingPose(startPose);
//        buildPaths();
//    }
//
//    /**
//     * This method is called continuously after Init while waiting for "play".
//     **/
//    @Override
//    public void init_loop() {
//    }
//
//    /**
//     * This method is called once at the start of the OpMode.
//     * It runs all the setup actions, including building paths and starting the path system
//     **/
//    @Override
//    public void start() {
//        opmodeTimer.resetTimer();
//        setPathState(0);
//    }
//
//    /**
//     * We do not use this because everything should automatically disable
//     **/
//    @Override
//    public void stop() {
//    }
//}
//
