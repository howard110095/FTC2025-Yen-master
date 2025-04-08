//package pedroPathing.examples;
//
//import com.pedropathing.follower.Follower;
//import com.pedropathing.localization.Pose;
//import com.pedropathing.pathgen.BezierCurve;
//import com.pedropathing.pathgen.BezierLine;
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
//@Autonomous(name = "AutoYellow", group = "Examples")
//public class AutoYellow extends basic {
//    private Timer pathTimer, actionTimer, opmodeTimer;
//    private double wrist_pos;
//    private int pathState;
//
//    private final Pose startPose = new Pose(9, 111, Math.toRadians(270));
//    private final Pose scorePose = new Pose(14, 127, Math.toRadians(315));
//    private final Pose pickup1Pose = new Pose(20, 118, Math.toRadians(0));
//    private final Pose pickup2Pose = new Pose(20, 128, Math.toRadians(0));
//    private final Pose pickup3Pose = new Pose(20, 130, Math.toRadians(15));
//    private final Pose parkPose = new Pose(60, 80, Math.toRadians(270));
//    private final Pose parkControlPose = new Pose(60, 120, Math.toRadians(270));
//
//    private Path scorePreload, park;
//    private PathChain grabPickup1, grabPickup2, grabPickup3, scorePickup1, scorePickup2, scorePickup3;
//
//    public void buildPaths() {
//        scorePreload = new Path(new BezierLine(new Point(startPose), new Point(scorePose)));
//        scorePreload.setLinearHeadingInterpolation(startPose.getHeading(), scorePose.getHeading());
//
//        grabPickup1 = follower.pathBuilder()
//                .addPath(new BezierLine(new Point(scorePose), new Point(pickup1Pose)))
//                .setLinearHeadingInterpolation(scorePose.getHeading(), pickup1Pose.getHeading())
//                .build();
//
//        scorePickup1 = follower.pathBuilder()
//                .addPath(new BezierLine(new Point(pickup1Pose), new Point(scorePose)))
//                .setLinearHeadingInterpolation(pickup1Pose.getHeading(), scorePose.getHeading())
//                .build();
//
//        grabPickup2 = follower.pathBuilder()
//                .addPath(new BezierLine(new Point(scorePose), new Point(pickup2Pose)))
//                .setLinearHeadingInterpolation(scorePose.getHeading(), pickup2Pose.getHeading())
//                .build();
//
//        scorePickup2 = follower.pathBuilder()
//                .addPath(new BezierLine(new Point(pickup2Pose), new Point(scorePose)))
//                .setLinearHeadingInterpolation(pickup2Pose.getHeading(), scorePose.getHeading())
//                .build();
//
//        grabPickup3 = follower.pathBuilder()
//                .addPath(new BezierLine(new Point(scorePose), new Point(pickup3Pose)))
//                .setLinearHeadingInterpolation(scorePose.getHeading(), pickup3Pose.getHeading())
//                .build();
//
//        scorePickup3 = follower.pathBuilder()
//                .addPath(new BezierLine(new Point(pickup3Pose), new Point(scorePose)))
//                .setLinearHeadingInterpolation(pickup3Pose.getHeading(), scorePose.getHeading())
//                .build();
//
//        park = new Path(new BezierCurve(new Point(scorePose), new Point(parkControlPose), new Point(parkPose)));
//        park.setLinearHeadingInterpolation(scorePose.getHeading(), parkPose.getHeading());
//    }
//
//    /**
//     * This switch is called continuously and runs the pathing, at certain points, it triggers the action state.
//     * Everytime the switch changes case, it will reset the timer. (This is because of the setPathState() method)
//     * The followPath() function sets the follower to run the specific path, but does NOT wait for it to finish before moving on.
//     */
//    public void autonomousPathUpdate() {
//        if (pathState == 0) {
//            slideTarget = slide_bucket;
//            armTarget = arm_bucket;
//            wristCombo(wrist_bucket, 0);
//            follower.followPath(scorePreload);
//            setPathState(101);
//        } else if (pathState == 101) {
//            if (!follower.isBusy()) setPathState(102);
//        } else if (pathState == 102) {
//            if (pathTimer.getElapsedTimeSeconds() >= 0.1) setPathState(103);
//        } else if (pathState == 103) {
//            clawCombo(claw_Open, -intake_on);
//            setPathState(104);
//        } else if (pathState == 104) {
//            if (pathTimer.getElapsedTimeSeconds() >= 1) setPathState(1);
//        } else if (pathState == 1) {
//            if (!follower.isBusy()) {
//                slideTarget = 35;
//                downCatch = true;
//                follower.followPath(grabPickup1, true);
//                setPathState(201);// 201
//            }
//        } else if (pathState == 201) {
//            if (!follower.isBusy()) setPathState(202);
//        } else if (pathState == 202) {
//            if (pathTimer.getElapsedTimeSeconds() >= 0.1) setPathState(203);
//        } else if (pathState == 203) {
//            slideTarget = 65;
//            setPathState(204);
//        } else if (pathState == 204) {
//            if (slidePosNow > 60) setPathState(205);
//           // if (pathTimer.getElapsedTimeSeconds() >= 1) setPathState(205);
//        } else if (pathState == 205) {
//            downCatch = false;
//            clawCombo(claw_Close, intake_off);
//            setPathState(206);
//        } else if (pathState == 206) {
//            if (pathTimer.getElapsedTimeSeconds() >= 0.6) setPathState(207);
//        } else if (pathState == 207) {
//            slideTarget = 35;
//            setPathState(208);
//        } else if (pathState == 208) {
//            if (pathTimer.getElapsedTimeSeconds() >= 1) setPathState(2);
//        }
//
//        //---------
//
//        else if (pathState == 2) {
//            slideTarget = slide_bucket;
//            armTarget = arm_bucket;
//            wristCombo(wrist_bucket, 0);
//            if (!follower.isBusy()) {
//                follower.followPath(scorePickup1, true);
//                setPathState(301);
//            }
//        } else if (pathState == 301) {
//            if (!follower.isBusy()) setPathState(302);
//        } else if (pathState == 302) {
//            if (pathTimer.getElapsedTimeSeconds() >= 0.1) setPathState(303);
//        } else if (pathState == 303) {
//            clawCombo(claw_Open, -intake_on);
//            setPathState(304);
//        } else if (pathState == 304) {
//            if (pathTimer.getElapsedTimeSeconds() >= 1) setPathState(3);
//        } else if (pathState == 3) {
//            if (!follower.isBusy()) {
//                slideTarget = 35;
//                downCatch = true;
//                follower.followPath(grabPickup2, true);
//                setPathState(401);// 201
//            }
//        } else if (pathState == 401) {
//            if (!follower.isBusy()) setPathState(402);
//        } else if (pathState == 402) {
//            if (pathTimer.getElapsedTimeSeconds() >= 0.1) setPathState(403);
//        } else if (pathState == 403) {
//            slideTarget = 65;
//            setPathState(404);
//        } else if (pathState == 404) {
//            if (slidePosNow > 60) setPathState(405);
//            //if (pathTimer.getElapsedTimeSeconds() >= 1) setPathState(405);
//        } else if (pathState == 405) {
//            downCatch = false;
//            clawCombo(claw_Close, intake_off);
//            setPathState(406);
//        } else if (pathState == 406) {
//            if (pathTimer.getElapsedTimeSeconds() >= 0.6) setPathState(407);
//        } else if (pathState == 407) {
//            slideTarget = 35;
//            setPathState(408);
//        } else if (pathState == 408) {
//            if (pathTimer.getElapsedTimeSeconds() >= 1) setPathState(4);
//        }
//
//        //--------
//
//        else if (pathState == 4) {
//            slideTarget = slide_bucket;
//            armTarget = arm_bucket;
//            wristCombo(wrist_bucket, 0);
//            if (!follower.isBusy()) {
//                follower.followPath(scorePickup2, true);
//                setPathState(501);
//            }
//        } else if (pathState == 501) {
//            if (!follower.isBusy()) setPathState(502);
//        } else if (pathState == 502) {
//            if (pathTimer.getElapsedTimeSeconds() >= 0.1) setPathState(503);
//        } else if (pathState == 503) {
//            clawCombo(claw_Open, -intake_on);
//            setPathState(504);
//        } else if (pathState == 504) {
//            if (pathTimer.getElapsedTimeSeconds() >= 1) setPathState(5);
//        } else if (pathState == 5) {
//            if (!follower.isBusy()) {
//                slideTarget = 35;
//                downCatch = true;
//                follower.followPath(grabPickup3, true);
//                setPathState(601);// 201
//            }
//        } else if (pathState == 601) {
//            if (!follower.isBusy()) setPathState(602);
//        } else if (pathState == 602) {
//            if (pathTimer.getElapsedTimeSeconds() >= 0.1) setPathState(603);
//        } else if (pathState == 603) {
//            slideTarget = 65;
//            setPathState(604);
//        } else if (pathState == 604) {
//            if (slidePosNow > 60) setPathState(605);
//            //if (pathTimer.getElapsedTimeSeconds() >= 1) setPathState(605);
//        } else if (pathState == 605) {
//            downCatch = false;
//            clawCombo(claw_Close, intake_off);
//            setPathState(606);
//        } else if (pathState == 606) {
//            if (pathTimer.getElapsedTimeSeconds() >= 0.6) setPathState(607);
//        } else if (pathState == 607) {
//            slideTarget = 35;
//            setPathState(608);
//        } else if (pathState == 608) {
//            if (pathTimer.getElapsedTimeSeconds() >= 1) setPathState(6);
//        }
//
//        //---------
//
//        else if (pathState == 6) {
//            slideTarget = slide_bucket;
//            armTarget = arm_bucket;
//            wristCombo(wrist_bucket, 0);
//            if (!follower.isBusy()) {
//                follower.followPath(scorePickup3, true);
//                setPathState(701);
//            }
//        } else if (pathState == 701) {
//            if (!follower.isBusy()) setPathState(702);
//        } else if (pathState == 702) {
//            if (pathTimer.getElapsedTimeSeconds() >= 0.5) setPathState(703);
//        } else if (pathState == 703) {
//            clawCombo(claw_Open, -intake_on);
//            setPathState(704);
//        } else if (pathState == 704) {
//            if (pathTimer.getElapsedTimeSeconds() >= 1) setPathState(7);
//        } else if (pathState == 7) {
//            slideTarget = smin;
//            armTarget = 52;
//            wristCombo(wrist_wall, 0);
//            clawCombo(claw_Open, intake_off);
//            if (!follower.isBusy()) {
//                follower.followPath(park, true);
//                setPathState(8);
//            }
//        } else if (pathState == 8) {
//            if (!follower.isBusy()) {
//                setPathState(-1);
//            }
//        }
//
//        //------------
//
////        else if (pathState == -4) {
////            if (!follower.isBusy()) {
////                follower.followPath(scorePickup2, true);
////                setPathState(-5);
////            }
////        } else if (pathState == -5) {
////            if (!follower.isBusy()) {
////                follower.followPath(grabPickup3, true);
////                setPathState(-6);
////            }
////        } else if (pathState == -6) {
////            if (!follower.isBusy()) {
////                follower.followPath(scorePickup3, true);
////                setPathState(-7);
////            }
////        } else if (pathState == -7) {
////            if (!follower.isBusy()) {
////                follower.followPath(park, true);
////                setPathState(8);
////            }
////        } else if (pathState == -8) {
////            if (!follower.isBusy()) {
////                setPathState(-1);
////            }
////        }
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
//    public void robotLoop() {
//        // These loop the movements of the robot
//        follower.update();
//        autonomousPathUpdate();
//
//        if (downCatch) {
//            autoFloor();
//            clawCombo(claw_bigger, intake_on);
//        }
//
//        //DC Motor to position
//        slideToPosition(slideTarget);
//        armTurn2angle(armTarget);
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
//    public void robotInit() {
//        pathTimer = new Timer();
//        opmodeTimer = new Timer();
//        opmodeTimer.resetTimer();
//        follower.setStartingPose(startPose);
//        buildPaths();
//    }
//
//    /**
//     * This method is called continuously after Init while waiting for "play".
//     **/
//    @Override
//    public void robotInitLoop() {
//        wristCombo(wrist_init, 0);
//        clawCombo(claw_Close, intake_off);
//        armTurn2angle(arm_init);
//        slideToPosition(smin);
//        armTarget = arm_init;
//        slideTarget = smin;
//    }
//
//    /**
//     * This method is called once at the start of the OpMode.
//     * It runs all the setup actions, including building paths and starting the path system
//     **/
//    @Override
//    public void robotStart() {
//        opmodeTimer.resetTimer();
//        setPathState(0);
//    }
//
//    /**
//     * We do not use this because everything should automatically disable
//     **/
//    // @Override
//    // public void stop() {
//    // }
//}
//
