package pedroPathing.examples;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@Config
@TeleOp(name = "SlideMoving", group = "Linear OpMode")
public class SlideMoving extends basic {
    public static double for_testing_1 = 0.1, for_testing_2 = 0, for_testing_3 = 0;

    @Override
    public void robotInit() {
        isReset = true;
    }

    @Override
    protected void robotInitLoop() {
    }

    @Override
    public void robotStart() {
    }

    @Override
    public void robotLoop() {
        //slide
        SlideFront.setPower(-gamepad1.right_stick_y);
        SlideBack.setPower(-gamepad1.right_stick_y);
        ArmUp.setPower(-gamepad1.left_stick_y);
        ArmDown.setPower(-gamepad1.left_stick_y);

        // 顯示數據
        telemetry.addData("slidePosNow", (SlideBack.getCurrentPosition() / slide2length) * 2 + smin);
        telemetry.addData("armPosNow", ArmUp.getCurrentPosition() / armEnc2deg + armOffset);
        telemetry.update();
    }

    public void robotStop() {
    }
}




