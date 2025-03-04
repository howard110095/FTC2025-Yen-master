package pedroPathing.examples;

import static com.arcrobotics.ftclib.util.MathUtils.clamp;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.util.Constants;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;

@Config
@TeleOp(name = "Wang", group = "Linear OpMode")
public class Wang extends basic {
    private Follower follower;
    private final Pose startPose = new Pose(0, 0, 0);
    private final ElapsedTime runtime = new ElapsedTime();

    // 滑軌與手臂速度參數
    public static double slide_Speed = 10; // 滑軌速度
    public static double arm_Speed = 5; // 手臂速度

    double clawPos = 0.5; // 爪子初始位置

    // 狀態機定義


    // 防抖動變數
    boolean togglePressed = false;
    boolean cirPressd = false;


    // 按鈕防抖動與執行狀態
    private boolean wasButtonPressedB = false, stateExecutedB = true;
    private boolean wasButtonPressedC = false, stateExecutedC = true;
    private boolean wasButtonPressedY = false, stateExecutedY = true;
    private boolean wasButtonPressedX = false, stateExecutedX = true;
    private boolean wasButtonPressedA = false, stateExecutedA = true;
    private boolean wasButtonPressed1X = false, stateExecuted1X = true;

    @Override
    public void robotInit() {
        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);
        follower.setStartingPose(startPose);

        armPowerMax = 1; // 設定手臂最大功率

        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());


        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    protected void robotInitLoop() {

        armTurn2angle(60); // 手臂初始化角度
        slideToPosition(40); // 滑軌初始化位置

        // 顯示初始化狀態
        telemetry.addData("Arm Position", armPosNow);
        telemetry.addData("Target Angle", armTarget);
        telemetry.update();
    }

    @Override
    public void robotStart() {
        // 更新手臂與滑軌當前位置
        armPosNow = ArmDown.getCurrentPosition() / armEnc2deg + armOffset;
        slidePosNow = (SlideFront.getCurrentPosition() / slide2lenth) * 2 + smin;

        // 控制爪子
        if (gamepad2.left_bumper) Claw.setPosition(claw_Open); // 打開爪子
        if (gamepad2.right_bumper) Claw.setPosition(claw_Close); // 關閉爪子

        // 吊掛模式切換
        if (gamepad2.left_stick_button && !togglePressed) {
            isHangingMode = !isHangingMode;
            togglePressed = true; // 防止抖動
        } else if (!gamepad2.left_stick_button) {
            togglePressed = false;
        }

        // 手臂伸縮控制
        double gp2_r_Y = gamepad2.right_stick_y;
        if (gp2_r_Y > 0.1 || gp2_r_Y < -0.1)
            slideTarget = slidePosNow + (-gp2_r_Y * slide_Speed);
        slideTarget = clamp(slideTarget, smin, smax);

        if (isHangingMode) {
            SlideFront.setPower(gp2_r_Y);
            slideOffset = -slidePosNow + 40;
            slideTarget = 40;
            if (!gamepad1.isRumbling()) gamepad1.runRumbleEffect(effect);
        } else {
            gamepad1.stopRumble();
            slideToPosition(slideTarget);
        }

        // 手臂上下控制
        double gp2_l_Y = -gamepad2.left_stick_y;
        if (gp2_l_Y < -0.3 || gp2_l_Y > 0.3) armTarget = armPosNow - (gp2_l_Y * arm_Speed);
        armTarget = clamp(armTarget, armBottomLimit, armUpLimit);

        if (isHangingMode) {
            ArmUp.setPower(gp2_l_Y);
            ArmDown.setPower(gp2_l_Y);
        } else {
            armTurn2angle(armTarget);
        }

        if (gamepad2.right_stick_button) {
            armOffset = -armPosNow;
            armTarget = 0;
        }

        // 爪子抬起與旋轉控制
        if (gamepad2.dpad_up || gamepad1.dpad_up) lift += 5;
        else if (gamepad2.dpad_down || gamepad1.dpad_down) lift -= 5;
        lift = clamp(lift, lift_Mini, lift_Max);

        if (gamepad2.dpad_left || gamepad1.dpad_left) turn += 10;
        else if (gamepad2.dpad_right || gamepad1.dpad_right) turn -= 10;
        turn = clamp(turn, turn_Mini, turn_Max);


        // 底盤遙控
        double axial = -gamepad1.left_stick_y;
        double lateral = -gamepad1.left_stick_x;
        double yaw = (gamepad1.left_trigger - gamepad1.right_trigger) * 0.8;
        follower.setTeleOpMovementVectors(axial, lateral, yaw, true);
        follower.update();

        // 顯示數據
        telemetry.addData("slideCM", slidePosNow);
        telemetry.addData("slideTAR", slideTarget);
        telemetry.addData("slidePOW", slidePower);
        telemetry.addData("armNOW", armPosNow);
        telemetry.addData("armTAR", armTarget);
        telemetry.addData("armPOWER", armOutput);
        telemetry.addData("armOffset", armOffset);
        telemetry.addData("lift", lift);
        telemetry.addData("turn", turn);
        telemetry.update();
    }


}




