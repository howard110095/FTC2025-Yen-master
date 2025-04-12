package pedroPathing.examples;

import static com.arcrobotics.ftclib.util.MathUtils.clamp;

import com.arcrobotics.ftclib.controller.PIDController;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.util.Constants;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;

@TeleOp(name = "Monster", group = "Examples")
public class Monster extends OpMode {
    //-------------arm--------
    private static double Power = 1,nowpos = 0; //手臂目標角度
    private static double P = 0.04, I = 0.04, D = 0.002, F = 0.08; //手臂 PID 參數  0.04~0.175
    private static double armEnc2deg = 537.7 * 4.2 / 360;  //編碼器值換算角度
    private Follower follower;
    private DcMotorEx ArmL, ArmR;
    private final Pose startPose = new Pose(0, 0, 0);
    private PIDController APID = new PIDController(0, 0, 0); // 手臂 PID 控制器

    /**
     * This method is call once when init is played, it initializes the follower
     **/
    @Override
    public void init() {
        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);
        follower.setStartingPose(startPose);
        ArmL = hardwareMap.get(DcMotorEx.class,"armL");
        ArmR = hardwareMap.get(DcMotorEx.class,"armR");
        ArmL.setDirection(DcMotorEx.Direction.REVERSE);

        ArmL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        ArmL.setPower(0);
        ArmR.setPower(0);
        ArmL.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        ArmR.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
    }

    /**
     * This method is called continuously after Init while waiting to be started.
     **/
    @Override
    public void init_loop() {
    }

    /**
     * This method is called once at the start of the OpMode.
     **/
    @Override
    public void start() {
        follower.startTeleopDrive();
    }

    /**
     * This is the main loop of the opmode and runs continuously after play
     **/
    @Override
    public void loop() {
        nowpos = ArmL.getCurrentPosition() / armEnc2deg; // 計算當前角度
        APID.setPID(P, I, D); // 設置 PID
        APID.setTolerance(10); // 設置誤差容忍度
        Power = APID.calculate(nowpos, 70) + F * Math.cos(Math.toRadians(nowpos))/* */; // 計算輸出
        ArmL.setPower(Power);
        ArmR.setPower(Power);

        follower.setTeleOpMovementVectors(-gamepad1.left_stick_y, -gamepad1.left_stick_x, -gamepad1.right_stick_x*0.6, true);
        follower.update();
        telemetry.addData("enc",ArmL.getCurrentPosition());
        telemetry.update();
    }

    /**
     * We do not use this because everything automatically should disable
     **/
    @Override
    public void stop() {
    }
}