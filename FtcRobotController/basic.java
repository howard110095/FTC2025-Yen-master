package pedroPathing.examples;

import android.provider.SyncStateContract;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.util.Constants;
import  com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;


import java.util.List;

@Config
public abstract class basic extends OpMode {
    // _____________底盤程式在drive_/SampleMecanumDrive___________
    protected SampleMecanumDrive drive;

    //----------------定義直流馬達-----------------
    protected DcMotorEx slide; // 升降滑軌馬達
    protected DcMotorEx armR; // 右側手臂馬達
    protected DcMotorEx armL; // 左側手臂馬達

    //----------------定義伺服馬達-----------------
    protected Servo FrontL, FrontR; // 手腕控制伺服馬達 (左/右)
    protected Servo Claw; // 爪子控制伺服馬達

    //-------------外掛Encoder--------------
    protected Encoder armEnc; // 手臂的外掛編碼器

    //-------------Limelight 相機--------------
    protected Limelight3A limelight; // Limelight 相機，用於目標追蹤

    //-------------PID 控制器---------------
    private PIDController ArmPID = new PIDController(0, 0, 0); // 手臂 PID 控制器
    private PIDController SlidePID = new PIDController(0, 0, 0); // 升降滑軌 PID 控制器

    /*------------------手臂參數-----------------------*/

    //-----------------手臂目標角度------------------
    public static double armTarget = 49;

    //-----------------手臂 PID 參數------------------
    public static double armP = 0.1; // 比例增益
    public static double armP_hang = 2; // 吊掛時比例增益
    public static double armI = 0.1; // 積分增益
    public static double armD = 0.004; // 微分增益
    public static double armF = 0; // 前饋控制
    public static double arm_f_coeff = 0.0053; // 前饋係數

    public static double arm_ff = 0.0053; // 前饋係數

    public static double arm_f_hang=-0.3;


    //-----------------手臂現在位置------------------
    public static double armPosNow; // 手臂當前角度

    //-----------------手臂限制------------------
    public static double armUpLimit = 95; // 上限角度
    public static double armBottomLimit = 0; // 下限角度

    //-----------------手臂電力限制------------------
    public static double armPowerMax = 0.6; // 最大電力
    public static double armPowerMin = -0.4; // 最小電力

    //-----------------手臂編碼器轉角度換算------------------
    public static double arm2deg = 6.27; // 角度換算常數
    public static double armEnc2deg = 8192 / 360; // 編碼器值換算角度
    public static double armStartAngle = 49; // 起始角度
    public static double armOffset = 0; // 偏移量

    public static double armOutput;

    /*------------------滑軌 PIDF 參數-----------------------*/
    public static double slideP = 0.5; // 滑軌比例增益
    public static double slideI = 0; // 滑軌積分增益
    public static double slideD = 0; // 滑軌微分增益

    public static double slideF_hang = -0.3; // 吊掛滑軌前饋
    public static double slideFF = 0; // 滑軌前饋
    public static double slide_f_coeff = 0; // 滑軌前饋係數

    public static double slide_motorEnc = 103.8; // 滑軌馬達編碼器常數
    public static double slide_Ratio = 1 / 1.4; // 傳動比
    public static double slide2lenth = slide_motorEnc * slide_Ratio / 0.8; // 編碼器值轉長度

    public double slidePower;

    //-----------------滑軌限制------------------
    public static double smax = 98; // 最大高度
    public static double smax0 = 76; // 最大初始高度
    public static double smin = 40; // 最小高度
    public static double slideOffset = 0; // 滑軌偏移

    //-----------------滑軌當前位置------------------
    public static double slidePosNow = 0; // 滑軌當前高度
    public double slideTarget = 0; // 滑軌目標高度

    /*-----------------手腕----------------------*/
    public static double gear_ratio = 2.888888; // 手腕齒輪比
    public static double currentAngleRight; // 右側當前角度
    public static double tarAngleLeft = 0; // 左側目標角度
    public static double tarAngleRight = 0; // 右側目標角度

    //-----------------爪子開關角度------------------
    public static double claw_Open = 0.7; // 爪子開啟角度
    public static double claw_Close = 0.39; // 爪子關閉角度

    //-----------------伺服馬達角度限制------------------
    public static double lift_Offset = -10, turn_Offset = -20; // 手腕偏移量
    public static double lift_Max = 90, lift_Mini = -90; // 升降最大/最小角度
    public static double turn_Max = 90, turn_Mini = -90; // 旋轉最大/最小角度

    //-----------------伺服馬達角度常數------------------
    public static double maxServoAngleLeft = 270.078; // 左伺服馬達最大角度
    public static double maxServoAngleRight = 270.078; // 右伺服馬達最大角度

    //-----------------伺服馬達目標位置------------------
    public static double tarPositionLeft, tarPositionRight; // 左/右伺服馬達目標位置

    public boolean isHangingMode = false; // 吊掛模式（預設為普通模式）

    Gamepad.RumbleEffect effect = new Gamepad.RumbleEffect.Builder()
            .addStep(1.0, 1.0, 1000) // 右馬達全速震動 1000 毫秒
            .addStep(0.0, 0.0, 1000) // 暫停 1000 毫秒
            .build();

    double lift = 0, turn = 0; // 手腕升降與旋轉控制變量
    public static boolean motorResetDone = false; // 馬達重置狀態

    @Override
    public void runOpMode() {
        // 初始化硬體元件
        List<LynxModule> allHubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO); // 設置批量緩存模式
        }

        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        drive = new SampleMecanumDrive(hardwareMap); // 初始化驅動系統

        // 初始化直流馬達
        slide = hardwareMap.get(DcMotorEx.class, "slide");
        armL = hardwareMap.get(DcMotorEx.class, "armL");
        armR = hardwareMap.get(DcMotorEx.class, "armR");

        slide.setDirection(DcMotorSimple.Direction.FORWARD);
        armL.setDirection(DcMotorSimple.Direction.REVERSE);
        armR.setDirection(DcMotorSimple.Direction.FORWARD);

        // 初始化伺服馬達
        FrontL = hardwareMap.get(Servo.class, "frontl");
        FrontR = hardwareMap.get(Servo.class, "frontr");
        Claw = hardwareMap.get(Servo.class, "claw");

        // 初始化手臂編碼器
        armEnc = new Encoder(hardwareMap.get(DcMotorEx.class, "armR"));
        armEnc.setDirection(Encoder.Direction.REVERSE);

        // 如果馬達尚未重置，執行重置
        if (!motorResetDone) {
            motorReset();
        }

        // 初始化馬達參數
        slide.setPower(0);
        slide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        armR.setPower(0);
        armL.setPower(0);
        armL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        armR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // 初始化 Limelight
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(1); // 切換到目標識別管道
        limelight.start();

        robotInit(); // 執行自定義初始化邏輯

        telemetry.addData("init", "done");
        telemetry.update();

        // 等待比賽開始
        while (!isStarted() && !isStopRequested()) {
            robotInitLoop(); // 自定義初始化迴圈
            telemetry.addData("Status", "Waiting for start...");
            telemetry.update();
        }

        // 比賽進行中
        while (opModeIsActive()) {
            robotStart(); // 自定義執行邏輯
        }

        limelight.stop(); // 停止 Limelight
    }