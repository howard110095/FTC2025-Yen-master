package pedroPathing.examples;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@Config
@TeleOp(name = "ServoMoving", group = "Linear OpMode")
public class ServoMoving extends basic {
    public static double vertical = 0.5, turn = 0, pawPos = 0.6, intakePower = 0;   //0.53 0.94

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
        //wrist
        vertical -= gamepad1.left_stick_y * 0.01;
        turn += gamepad1.right_stick_x * 0.01;
        Lwrist.setPosition(wrist_all_pose - vertical + turn);
        Rwrist.setPosition(vertical);
        //claw
        if (gamepad1.y) pawPos += 0.005;
        else if (gamepad1.a) pawPos -= 0.005;
        Claw.setPosition(pawPos);
        //intake
        if (gamepad1.b) intakePower = 1;
        else if (gamepad1.x) intakePower = -1;
        else intakePower = 0;
        Left.setPower(intakePower);
        Right.setPower(intakePower);
        //show data
        telemetry.addData("LeftWrist", wrist_all_pose - vertical + turn);
        telemetry.addData("RightWrist", vertical);
        telemetry.addData("Claw", pawPos);
        telemetry.update();
    }

    public void robotStop() {
    }
}




