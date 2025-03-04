package pedroPathing.examples;

import static com.arcrobotics.ftclib.util.MathUtils.clamp;

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

@TeleOp(name = "lin", group = "Examples")
public class lin extends OpMode {
    private Follower follower;
    private final Pose startPose = new Pose(0, 0, 0);
    //    private CRServo LS, RS;
    private Servo S1, S2, S3;
    private double pos = 0;
    int logic = 0;

    private boolean lastpress = false;

    @Override
    public void init() {
        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);
        follower.setStartingPose(startPose);

        // 確保在硬體映射中有一個名稱為 "axonMiniServo" 的連續旋轉伺服
        S1 = hardwareMap.get(Servo.class, "s1");
        S2 = hardwareMap.get(Servo.class, "s2");
        S3 = hardwareMap.get(Servo.class, "s3");
    }

    public void start() {
        follower.startTeleopDrive();
    }

    @Override
    public void loop() {
        //LS.setPower(1);
        //RS.setPower(1);

        if (!lastpress && gamepad1.y) arm_manage();

        lastpress = gamepad1.y;


//        pos -= gamepad1.right_stick_y * 0.01;
//        pos = clamp(pos,0.0,1.0);
//        S1.setPosition(pos);

        double axial = -gamepad1.left_stick_y;
        double lateral = -gamepad1.left_stick_x;
        double yaw = (gamepad1.right_trigger - gamepad1.left_trigger) * -0.5;
        follower.setTeleOpMovementVectors(axial, lateral, yaw, true);
        follower.update();

        // 顯示伺服的當前功率值
        telemetry.addData("pos", pos);
        telemetry.update();
    }

    void arm_manage() {
        arm_logic(4, 1);
    }

    void arm_logic(int all, int change) {
        logic += change;
        if (logic % all == 1) {
            S1.setPosition(0);
            S2.setPosition(0.7);
            S3.setPosition(0.2); //open
        } else if (logic % all == 2) {
            S3.setPosition(0.6); //close
        } else if (logic % all == 3) {
            S1.setPosition(1);
            S2.setPosition(0.31);
        } else if (logic % all == 0) {
            S3.setPosition(0.2); //open
        }
    }

}
