package pedroPathing.examples;

import static com.arcrobotics.ftclib.util.MathUtils.clamp;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.util.Constants;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;

@Config
@TeleOp(name = "Howard", group = "Linear OpMode")
public class Howard extends basic {
    public static double for_testing_1 = 0, for_testing_2 = 0, for_testing_3 = 0;   //0.53 0.94
    public boolean catching = false;
    boolean togglePressed = false, cirPressd = false; // 防抖動變數

    @Override
    public void robotInit() {

    }

    @Override
    protected void robotInitLoop() {
        //target
        wristCombo(wrist_init, delta_init);
        clawCombo(claw_Close, intake_off);
        armTurn2angle(arm_init);
        slideToPosition(smin);
        armTarget = arm_init;
        slideTarget = smin;
    }

    @Override
    public void robotStart() {
        follower.startTeleopDrive();
    }

    @Override
    public void robotLoop() {
        //button mode
        step_logic();
        yellow_logic();
        specimen_logic();
        hang_logic();
        hang_robot_logic();
        intakeout_logic();

        // 更新手臂與滑軌當前位置
//        armPosNow = ArmDown.getCurrentPosition() / armEnc2deg + armOffset;
//        slidePosNow = (SlideFront.getCurrentPosition() / slide2lenth) * 2 + smin;
//        // 吊掛模式切換
//        if (gamepad2.left_stick_button && !togglePressed)
//            isHangingMode = !isHangingMode;
//        togglePressed = gamepad2.left_stick_button;
//        //slide
//        if (gamepad1.dpad_up) slideTarget = slidePosNow + 5;
//        else if (gamepad1.dpad_down) slideTarget = slidePosNow - 5;
//        //slideTarget = slidePosNow + (-gamepad2.right_stick_y * slide_Speed);
//        slideTarget = clamp(slideTarget, smin, smax);
//        slideToPosition(slideTarget);
//        if (isHangingMode) {
//            SlideFront.setPower(-gamepad2.right_stick_y);
//            slideOffset = -slidePosNow + 40;
//            slideTarget = 40;
//            if (!gamepad1.isRumbling()) gamepad1.runRumbleEffect(effect);
//        } else {
//            //gamepad1.stopRumble();
//        }
//        // 手臂上下控制
//        double gp2_l_Y = -gamepad2.left_stick_y;
//        if (gp2_l_Y < -0.3 || gp2_l_Y > 0.3) armTarget = armPosNow - (gp2_l_Y * arm_Speed);
//        armTarget = clamp(armTarget, armBottomLimit, armUpLimit);
//
//        if (isHangingMode) {
//            ArmUp.setPower(gp2_l_Y);
//            ArmDown.setPower(gp2_l_Y);
//        } else {
//            armTurn2angle(armTarget);
//        }
//        //arm
//        armTarget = armPosNow - gamepad1.right_stick_y * 10;
//        armTarget = clamp(armTarget, armBottomLimit, armUpLimit);
//        armTurn2angle(armTarget);
        //if (gamepad2.right_stick_button) {
//            armOffset = -armPosNow;
//            armTarget = 0;
//        }


        //catching wrist turn
        if (catching && !downCatch) {
            if (gamepad1.dpad_left && !Button1DL) wrist_position -= wrist_delta;
            if (gamepad1.dpad_right && !Button1DR) wrist_position += wrist_delta;
            wrist_position = clamp(wrist_position, -wrist_delta * 2, wrist_delta * 2);
            wristCombo(wrist_ready, wrist_position);
            Button1DL = gamepad1.dpad_left;
            Button1DR = gamepad1.dpad_right;
        } else if (catching) {
            if (gamepad1.dpad_up) slideTarget += 0.8;
            if (gamepad1.dpad_down) slideTarget -= 0.8;
            slideTarget = clamp(slideTarget, smin, smax);
            autoFloor();
        }

        //slide
        if (gamepad1.dpad_up) slideTarget += 0.8;
        if (gamepad1.dpad_down) slideTarget -= 0.8;
        slideTarget = clamp(slideTarget, smin, smax);

        if (!isHangingMode) {
            slideToPosition(slideTarget);
        } else {
            slideOn(slideTarget);
        }

        //arm
        armTurn2angle(armTarget);

        // 底盤遙控
        double axial = -gamepad1.left_stick_y;
        double lateral = -gamepad1.left_stick_x;
        double yaw = gamepad1.right_stick_x * 0.6;
        follower.setTeleOpMovementVectors(axial, lateral, -yaw, true);
        follower.update();

        // 顯示數據
        telemetry.addData("slidePosNow", slidePosNow);
        telemetry.addData("armPosNow", armPosNow);
//        telemetry.addData("X", follower.getPose().getX());
//        telemetry.addData("Y", follower.getPose().getY());
//        telemetry.addData("Heading", follower.getPose().getHeading());
        // telemetry.addData("armTarget", -Math.toDegrees(Math.asin(15.7 / slidePosNow)));
        telemetry.update();

    }

    void step_logic() {
        // reset -> 0
        if (gamepad1.a && !Button1A) {
            yellow_count = 1;
            specimen_count = 1;
            hang_count = 0;
            hang_robot_count = 0;
            wrist_position = 0;

            slideTarget = slide_ready;
            armTarget = arm_ready;
            wristCombo(wrist_ready, 0);
            clawCombo(claw_Open, intake_off);
            downCatch = false;
            catching = true;
        }
        Button1A = gamepad1.a;

        // last step
        if (gamepad1.x && !Button1X) {
            if (NowMode == 1) yellow_On(-1);
            else if (NowMode == 2) specimen_On(8, -1);
            else if (NowMode == 3) hang_On(-1);
        }
        Button1X = gamepad1.x;
    }

    void yellow_logic() {
        if (gamepad1.right_bumper && !Button1RB) yellow_On(1);
        if (gamepad1.left_stick_button && !Button1LSB) {
            downCatch = true;
            yellow_On(1);
        }

        Button1RB = gamepad1.right_bumper;
        Button1LSB = gamepad1.left_stick_button;
    }

    void yellow_On(int change) {
        NowMode = 1;
        yellow_count += change;
        if (yellow_count < 0) yellow_count += yellow_step;

        if (yellow_count % yellow_step == 1) {
            slideTarget = slide_ready;
            armTarget = arm_ready;
            wrist_position = 0;
            wristCombo(wrist_ready, 0);
            clawCombo(claw_Open, intake_off);
            catching = true;
            downCatch = false;
        } else if (yellow_count % yellow_step == 2) {
            if (!downCatch) {
                armTarget = arm_catch;
                wristCombo(wrist_down, wrist_position);
                catching = false;
            }
            clawCombo(claw_Open, intake_on);
        } else if (yellow_count % yellow_step == 3) {
            clawCombo(claw_Close, intake_off);
            catching = false;
            downCatch = false;
        } else if (yellow_count % yellow_step == 4) {
            slideTarget = slide_ready;
            armTarget = arm_ready;
            wristCombo(wrist_bucket, 0);
        } else if (yellow_count % yellow_step == 5) {
            slideTarget = slide_bucket;
            armTarget = arm_bucket;
        } else if (yellow_count % yellow_step == 0) {
            clawCombo(claw_Open, -intake_on);
            catching = false;
        }
    }

    void specimen_logic() {
        if (gamepad1.left_bumper && !Button1LB) specimen_On(8, 1);
        Button1LB = gamepad1.left_bumper;
    }

    void specimen_On(int all, int change) {
        NowMode = 2;
        specimen_count += change;
        if (specimen_count < 0) specimen_count += all;

        if (specimen_count % all == 1) {
            slideTarget = slide_ready;
            armTarget = arm_ready;
            wristCombo(wrist_ready, 0);
            clawCombo(claw_Open, intake_off);
            catching = true;
        } else if (specimen_count % all == 2) {
            armTarget = arm_catch;
            wristCombo(wrist_down, wrist_position);
            clawCombo(claw_Open, intake_on);
            catching = false;
        } else if (specimen_count % all == 3) {
            clawCombo(claw_Close, intake_off);
        } else if (specimen_count % all == 4) {
            slideTarget = slide_wall;
            armTarget = arm_wall;
            wristCombo(wrist_wall, 0);
        } else if (specimen_count % all == 5) {
            clawCombo(claw_Open, intake_on);
        } else if (specimen_count % all == 6) {
            clawCombo(claw_Close, intake_off);
        } else if (specimen_count % all == 7) {
            slideTarget = slide_hang;
            armTarget = arm_hang;
            wristCombo(wrist_hang, 0);
            clawCombo(claw_Close, intake_off);
        } else if (specimen_count % all == 0) {
            clawCombo(claw_Open, intake_off);
            catching = false;
        }
    }

    void hang_logic() {
        if (gamepad1.y && !Button1Y) hang_On(1);
        Button1Y = gamepad1.y;
    }

    void hang_On(int change) {
        NowMode = 3;
        hang_count += change;
        if (hang_count < 0) hang_count += hang_step;

        if (hang_count % hang_step == 1) {
            slideTarget = slide_wall;
            armTarget = arm_wall;
            wristCombo(wrist_wall, 0);
            clawCombo(claw_Open, intake_on);
            catching = false;
        } else if (hang_count % hang_step == 2) {
            clawCombo(claw_Close, intake_off);
        } else if (hang_count % hang_step == 3) {
            slideTarget = slide_hang;
            armTarget = arm_hang;
            wristCombo(wrist_hang, 0);
            clawCombo(claw_Close, intake_off);
        } else if (hang_count % hang_step == 0) {
            clawCombo(claw_Open, intake_off);
            slideTarget = slide_wall;
        }
    }

    void hang_robot_logic() {
        if (gamepad1.b && !Button1B)    hang_robot_On(6, 1);
        Button1B = gamepad1.b;
    }

    void hang_robot_On(int all, int change) {
        hang_robot_count += change;
        if (hang_robot_count < 0) hang_robot_count += all;

        if (hang_robot_count % all == 1) {
            slideTarget = slide_robot;
            armTarget = arm_robot;
            wristCombo(wrist_wall, 0);
            clawCombo(claw_Close, intake_off);
            downCatch = false;
            catching = false;
            isHangingMode = true;
        } else if (hang_robot_count % all == 2) {
            slideTarget = slide2floor;
        } else if (hang_robot_count % all == 3) {
            slideTarget = slide2floor_down;
        } else if (hang_robot_count % all == 4) {
            slideTarget = slide3floor;
            armTarget = 97;
        } else if (hang_robot_count % all == 5) {
            armTarget = -34;
        }
    }

    void intakeout_logic() {
        if (gamepad1.right_stick_button && !Button1RSB) intakeout_On();
        Button1RSB = gamepad1.right_stick_button;
    }

    void intakeout_On() {
        intakeout_count += 1;

        if (intakeout_count % 2 == 1) {
            Left.setPower(-intake_on);
            Right.setPower(-intake_on);
        } else {
            Left.setPower(intake_off);
            Right.setPower(intake_off);
        }
    }

    public void robotStop() {

    }
}




