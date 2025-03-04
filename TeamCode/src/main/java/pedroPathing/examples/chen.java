package pedroPathing.examples;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.util.Constants;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;

@TeleOp(name = "chen", group = "Examples")
public class chen extends OpMode {
    private Follower follower;
    private final Pose startPose = new Pose(0, 0, 0);
    private CRServo LS, RS;
    private Servo Claw;
    private DcMotorEx slideF, slideB, ArmU, ArmD;
    private double pos = 0;
    static double time = 0;
    private boolean lastpress = false;

    @Override
    public void init() {
        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);
        follower.setStartingPose(startPose);

        // 確保在硬體映射中有一個名稱為 "axonMiniServo" 的連續旋轉伺服
        LS = hardwareMap.get(CRServo.class, "l");
        RS = hardwareMap.get(CRServo.class, "r");
        Claw = hardwareMap.get(Servo.class, "claw");
        slideF = hardwareMap.get(DcMotorEx.class, "slidef");
        slideB = hardwareMap.get(DcMotorEx.class, "slideb");
        ArmU = hardwareMap.get(DcMotorEx.class, "au");
        ArmD = hardwareMap.get(DcMotorEx.class, "ad");

        // 設置馬達方向（可選）
        RS.setDirection(CRServo.Direction.REVERSE); // 反向旋轉：使用 REVERSE
        slideF.setDirection(DcMotorEx.Direction.FORWARD);
        slideB.setDirection(DcMotorEx.Direction.FORWARD);
        ArmD.setDirection(DcMotorEx.Direction.REVERSE);

        ArmU.setPower(0);
        ArmU.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        ArmD.setPower(0);
        ArmD.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        slideF.setPower(0);
        slideF.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        slideB.setPower(0);
        slideB.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        //slideF.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        //  pos = slideF.getCurrentPosition();

    }

    public void start() {
        follower.startTeleopDrive();
    }

    @Override
    public void loop() {
        // 控制伺服的旋轉方向：
        // 正向旋轉
        //LS.setPower(1);  // 設為1表示正向全速旋轉
        //RS.setPower(1);
        // 或者反向旋轉：
        // axonMiniServo.setPower(-1.0);  // 設為-1表示反向全速旋轉


        if (gamepad1.y) {
            slideF.setPower(1);
            slideB.setPower(1);
        }
        else if(gamepad1.a){
            slideF.setPower(-1);
            slideB.setPower(-1);
        }
        else{
            slideF.setPower(0);
            slideB.setPower(0);
        }

        if(gamepad1.dpad_up){
            ArmU.setPower(0.6);
            ArmD.setPower(0.6);
        }
        else if(gamepad1.dpad_down){
            ArmU.setPower(-0.6);
            ArmD.setPower(-0.6);
        }
        else{
            ArmU.setPower(0);
            ArmD.setPower(0);
        }





        double axial = -gamepad1.left_stick_y;
        double lateral = -gamepad1.left_stick_x;
        double yaw = (gamepad1.right_trigger - gamepad1.left_trigger) * 0.8;
        follower.setTeleOpMovementVectors(axial, lateral, yaw, true);
        follower.update();

        // 顯示伺服的當前功率值
        telemetry.addData("pos", -gamepad1.right_stick_y);
        telemetry.update();
    }
}
