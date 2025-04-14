package pedroPathing.examples;

import static com.arcrobotics.ftclib.util.MathUtils.clamp;

import androidx.core.content.pm.PermissionInfoCompat;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.controller.PIDController;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.util.Constants;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;
import com.qualcomm.robotcore.hardware.ColorSensor;

import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.VisionProcessor;

import java.util.List;

import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;

@Config
public abstract class basic extends OpMode {
    public Follower follower;
    public ColorSensor CS;
    public final Pose startPose = new Pose(0, 0, 0);
    protected Limelight3A limelight; // Limelight 相機，用於目標追蹤
    protected DcMotorEx SlideFront, SlideBack, ArmUp, ArmDown; //定義直流馬達
    protected CRServo Left, Right; // 手腕控制伺服馬達 (左/右)
    protected Servo Claw, Lwrist, Rwrist; // 爪子控制伺服馬達
    private PIDController ArmPID = new PIDController(0, 0, 0); // 手臂 PID 控制器
    private PIDController SlidePID = new PIDController(0, 0, 0); // 升降滑軌 PID 控制器

    //-------------arm--------
    public static double armTarget = 49; //手臂目標角度
    public static double armP = 0.04, armI = 0.04, armD = 0.002, armF = 0.04; //手臂 PID 參數  0.04~0.175
    public static double armPosNow, armOutput; //now degree
    public static double armUpLimit = 118, armBottomLimit = -47, armPowerMax = 1, armPowerMin = -0.6;
    public static double armEnc2deg = 751.8 * 4.2 / 360;  //編碼器值換算角度
    public static double armOffset = -42; // 起始角度   偏移量

    //---------------- slide -------------
    public static double slideP = 0.8, slideI = 0, slideD = 0.018, slideF = 0; // 吊掛滑軌前饋
    public static double slideOffset = 0; // 滑軌前饋係數
    public static double slide_motorEnc = 145.1; // 滑軌馬達編碼器常數
    public static double slide_Ratio = 1 / 1.5; // 傳動比
    public static double slide2length = slide_motorEnc * slide_Ratio / 0.8; // 編碼器值轉長度
    public double slidePower;
    public static double smax = 84.5, smin = 28; // slide length limit
    public static double slidePosNow = 0; //slide current CM
    public double slideTarget = 0; //slide Target CM

    //-----------------position-----------------
    public static double clawCloseTime = 0.2, putBucketTime = 0.3, claw_bigger = 0.52, claw_Close = 0.63,clawWallClose = 0.68, intake_on = 1, intake_off = 0;
    public static double wrist_pos = 0, wrist_all_pose = 0.75, wrist_delta = 0.06, wrist_position = 0;
    public static double wrist_init = 0, delta_init = 0.2, wrist_ready = 0.64, wrist_down = 0.6, wrist_bucket = 0.2, wrist_wall = 0.3, wrist_hang = 0.23;
    public static double arm_init = -42, arm_robot = 70, arm_ready = 0, arm_catch = -24, arm_bucket = 88, arm_wall = 0, arm_hang = 38;
    public static double slide_ready = 33, slide_bucket = 83.5, slide_wall = 30, slide_hang = 45;
    public static double slide_robot = 81, slide2floor = 49, slide2floor_down = 55, slide3floor = 28;
    public static double slideBackHang = 34, armBackHang = 117, wristBackHang = 0.4, deltaBackHang = 0.2;
    public boolean isHangingMode = false; //hang mode

    //---------teleOp---------------
    public boolean Button1A = false, Button1B = false, Button1X = false, Button1Y = false;
    public boolean Button1LB = false, Button1RB = false, Button1DL = false, Button1DR = false;
    public boolean Button1LSB = false, Button1RSB = false;
    public int yellow_count = 0, hang_count = 0, hang_robot_count = 0, intakeout_count = 0, NowMode = 1;
    static public int yellow_step = 5, hang_step = 5;

    // auto floor
    public boolean downCatch = false, isReset = false;
    public int OutColor = 1; // 1->red 3->blue

    Gamepad.RumbleEffect effect = new Gamepad.RumbleEffect.Builder()
            .addStep(1.0, 1.0, 1000) // 右馬達全速震動 1000 毫秒
            .addStep(0.0, 0.0, 1000) // 暫停 1000 毫秒
            .build();

    public void init() {
        // 初始化硬體元件
//        List<LynxModule> allHubs = hardwareMap.getAll(LynxModule.class);
//        for (LynxModule hub : allHubs) {
//            hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO); // 設置批量緩存模式
//        }

        //follower
        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);
        follower.setStartingPose(startPose);

        //telemetry
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        //motor
        CS = hardwareMap.get(ColorSensor.class, "cs");
        SlideFront = hardwareMap.get(DcMotorEx.class, "sf");
        SlideBack = hardwareMap.get(DcMotorEx.class, "sb");
        ArmUp = hardwareMap.get(DcMotorEx.class, "au");
        ArmDown = hardwareMap.get(DcMotorEx.class, "ad");
        SlideFront.setDirection(DcMotorEx.Direction.FORWARD);
        SlideBack.setDirection(DcMotorEx.Direction.FORWARD);
        ArmUp.setDirection(DcMotorEx.Direction.REVERSE);
        ArmDown.setDirection(DcMotorEx.Direction.FORWARD);
        Left = hardwareMap.get(CRServo.class, "l");
        Right = hardwareMap.get(CRServo.class, "r");
        Claw = hardwareMap.get(Servo.class, "claw");
        Lwrist = hardwareMap.get(Servo.class, "lw");
        Rwrist = hardwareMap.get(Servo.class, "rw");
        Right.setDirection(CRServo.Direction.REVERSE);

        robotInit(); // 執行自定義初始化邏輯

        //ENCODER
        if (!isReset) {
            SlideBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            ArmUp.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            isReset = true;
        }

        //motor mode
        SlideFront.setPower(0);
        SlideFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        SlideBack.setPower(0);
        SlideBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        ArmUp.setPower(0);
        ArmUp.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        ArmDown.setPower(0);
        ArmDown.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        telemetry.addData("init", "done");
        telemetry.update();
    }

    public void init_loop() {
        // 等待比賽開始
        robotInitLoop();
        telemetry.addData("Status", "Waiting for start...");
        telemetry.update();
    }

    public void start() {
        robotStart();
    }

    public void loop() {
        robotLoop(); //比賽進行中，自定義執行邏輯
    }

    public void stop() {
        robotStop();
        //limelight.stop(); // 停止 Limelight
    }

    //
    protected abstract void robotInit();

    protected abstract void robotInitLoop();

    protected abstract void robotStart();

    protected abstract void robotLoop();

    protected abstract void robotStop();

    // 手臂轉動到指定角度
    public void armTurn2angle(double target) {
        armPosNow = ArmUp.getCurrentPosition() / armEnc2deg + armOffset; // 計算當前角度
        if (slidePosNow >= 50 && target <= 15 && armPosNow > 15) target = armPosNow;
        target = clamp(target, armBottomLimit, armUpLimit); // 限制目標角度範圍
        //28 -> 0 ; 80 -> 0.16
        armF = (slidePosNow - smin) / (smax - smin) * 0.16 + 0;// 計算前饋
        ArmPID.setPID(armP, armI, armD); // 設置 PID
        ArmPID.setTolerance(10); // 設置誤差容忍度
        armOutput = ArmPID.calculate(armPosNow, target) + armF * Math.cos(Math.toRadians(armPosNow))/* */; // 計算輸出
        armOutput = clamp(armOutput, armPowerMin, armPowerMax); // 限制輸出範圍
        ArmUp.setPower(armOutput);
        ArmDown.setPower(armOutput);
    }

    public void armOn(double target) {
        armPosNow = ArmUp.getCurrentPosition() / armEnc2deg + armOffset; // 計算當前角度
        target = clamp(target, armBottomLimit, armUpLimit); // 限制目標角度範圍
        if (target - armPosNow >= 1) armOutput = 1;
        else if (Math.abs(target - armPosNow) < 1) armOutput = target - armPosNow;
        else armOutput = -1;
        ArmUp.setPower(armOutput);
        ArmDown.setPower(armOutput);
    }

    // 滑軌移動到指定位置
    public void slideToPosition(double slidePos) {
        slidePosNow = (SlideBack.getCurrentPosition() / slide2length) * 2 + smin + slideOffset; // 計算當前位置
        slidePos = clamp(slidePos, smin, smax); // 限制目標範圍
        SlidePID.setPID(slideP, slideI, slideD); // 設置 PID
        slidePower = SlidePID.calculate(slidePosNow, slidePos) + Math.sin(Math.toRadians(slidePosNow)) * slideF; // 計算輸出
        SlideFront.setPower(slidePower);
        SlideBack.setPower(slidePower);
    }

    public void slideOn(double slidePos) {
        slidePosNow = (SlideBack.getCurrentPosition() / slide2length) * 2 + smin + slideOffset; // 計算當前位置
        slidePos = clamp(slidePos, smin, smax); // 限制目標範圍
        SlidePID.setPID(slideP, slideI, slideD); // 設置 PID
        if (slidePos - slidePosNow >= 1) slidePower = 1;
        else if (Math.abs(slidePos - slidePosNow) < 1) slidePower = slidePos - slidePosNow;
        else slidePower = -1;
        SlideFront.setPower(slidePower);
        SlideBack.setPower(slidePower);
    }

    public void wristCombo(double pose, double delta) {
        Lwrist.setPosition(wrist_all_pose - pose + delta);
        Rwrist.setPosition(pose);
    }

    public void clawCombo(double pose, double power) {
        Claw.setPosition(pose);
        Left.setPower(power);
        Right.setPower(power);
    }

    public void autoFloor() {
        //255 -> 355
        slidePosNow = (SlideBack.getCurrentPosition() / slide2length) * 2 + smin + slideOffset;
        armTarget = -Math.toDegrees(Math.asin(15 / slidePosNow));
        wrist_pos = -Math.toDegrees(Math.asin(15 / slidePosNow)) / 250 / 355 * 255;
        wrist_pos += 0.35;
        wristCombo(wrist_pos, 0);
        if(gamepad1.left_bumper) clawCombo(claw_Close, -intake_on);
        else  clawCombo(claw_Close, intake_on);
    }

    // 計算角度誤差
//    private double calculateAngleError(double target, double current) {
//        double error = target - current;
//        if (error > 180) error -= 360; // 誤差大於 180 時，取反方向
//        if (error < -180) error += 360; // 誤差小於 -180 時，取反方向
//        return error;
//    }
    // 將 Limelight 輸出轉換為 Pose2d
//    public void convertToPose2d(String telemetryOutput) {
//        limelight.stop();
//        String positionData = telemetryOutput.split("position=\\(")[1].split("\\)")[0];
//        String orientationData = telemetryOutput.split("yaw=")[1].split(",")[0];
//        String[] positionParts = positionData.split(" ");
//        double xMeters = Double.parseDouble(positionParts[0]);
//        double yMeters = Double.parseDouble(positionParts[1]);
//        double yawDegrees = Double.parseDouble(orientationData);
//        x_target = xMeters * 39.3701;
//        y_target = yMeters * 39.3701;
//        heading_target = yawDegrees;
//    }

    public double Rpercent() {
        return (double) CS.red() / (CS.red() + CS.green() + CS.blue());
    }

    public double Gpercent() {
        return (double) CS.green() / (CS.red() + CS.green() + CS.blue());
    }

    public double Bpercent() {
        return (double) CS.blue() / (CS.red() + CS.green() + CS.blue());
    }

    public int color_detect() {
        if (Rpercent() < 0.45 && Bpercent() < 0.2) return 2; //yellow
        else if (Rpercent() > 0.35) return 1; //red
        else if (Bpercent() > 0.42) return 3; //blue
        else return 0;
    }
}
