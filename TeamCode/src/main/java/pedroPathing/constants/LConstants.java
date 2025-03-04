package pedroPathing.constants;

import com.pedropathing.localization.*;
import com.pedropathing.localization.constants.*;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class LConstants {
    static {
        //修正乘數
        PinpointConstants.forwardY = 1; //英吋   前後移動舵輪離中心點的水平距離
        PinpointConstants.strafeX = -2.5; //英吋
        PinpointConstants.distanceUnit = DistanceUnit.INCH; //設定單位
        PinpointConstants.hardwareMapName = "pinpoint";
        PinpointConstants.useYawScalar = false;
        PinpointConstants.yawScalar = 1.0;
        PinpointConstants.useCustomEncoderResolution = false;
        PinpointConstants.encoderResolution = GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD;
        PinpointConstants.customEncoderResolution = 13.26291192;
        PinpointConstants.forwardEncoderDirection = GoBildaPinpointDriver.EncoderDirection.REVERSED;
        PinpointConstants.strafeEncoderDirection = GoBildaPinpointDriver.EncoderDirection.FORWARD;

//        DriveEncoderConstants.forwardTicksToInches = 0.0056;
//        DriveEncoderConstants.strafeTicksToInches = 0.0056;
//        DriveEncoderConstants.turnTicksToInches = 0.0121;
//
//        DriveEncoderConstants.robot_Width = 13.4;
//        DriveEncoderConstants.robot_Length = 9.4488;
//
//        DriveEncoderConstants.leftFrontEncoderDirection = Encoder.REVERSE;
//        DriveEncoderConstants.rightFrontEncoderDirection = Encoder.FORWARD;
//        DriveEncoderConstants.leftRearEncoderDirection = Encoder.REVERSE;
//        DriveEncoderConstants.rightRearEncoderDirection = Encoder.FORWARD;
    }
}




