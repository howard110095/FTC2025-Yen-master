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
@TeleOp(name = "SlideMoving", group = "Linear OpMode")
public class SlideMoving extends basic {
    public static double for_testing_1 = 0.1, for_testing_2 = 0, for_testing_3 = 0;   //0.53 0.94
    public boolean catching = false;
    boolean togglePressed = false, cirPressd = false; // 防抖動變數

    @Override
    public void robotInit() {

    }

    @Override
    protected void robotInitLoop() {
    }

    @Override
    public void robotStart() {
        //follower.startTeleopDrive();
    }

    @Override
    public void robotLoop() {
        //slide
        SlideFront.setPower(-gamepad1.right_stick_y);
        SlideBack.setPower(-gamepad1.right_stick_y);
        ArmUp.setPower(0);
        ArmDown.setPower(0);

        // 底盤遙控
//        follower.setTeleOpMovementVectors(0, 0, 0, true);
//        follower.update();

        // 顯示數據
        telemetry.addData("slidePosNow", (SlideBack.getCurrentPosition() / slide2length) * 2 + smin);
        telemetry.update();
    }

    public void robotStop(){}
}




