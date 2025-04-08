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
@TeleOp(name = "test", group = "Linear OpMode")
public class test extends basic {
    public static double for1 = 0.5, for2 = 0, for_testing_3 = 0;   //0.53 0.94
    public boolean catching = false;
    boolean togglePressed = false, cirPressd = false; // 防抖動變數

    double possss = 0;

    @Override
    public void robotInit() {

    }

    @Override
    protected void robotInitLoop() {
        //armTurn2angle(0);
//        slideToPosition(slide_hang);
//        armTurn2angle(arm_hang);
//        wristCombo(wrist_hang, 0);
    }

    @Override
    public void robotStart() {
        //follower.startTeleopDrive();
    }

    @Override
    public void robotLoop() {
        armTurn2angle(for1);

        // 顯示數據
        telemetry.addData("target", for1);
        telemetry.addData("NowDegree", armPosNow);
        telemetry.update();
    }

    public void robotStop() {
    }
}




