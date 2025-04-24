package pedroPathing.examples;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;


@TeleOp(name = "ColorSensorTest", group = "Examples")
public class ColorSensorTest extends OpMode {

    public ColorSensor CS;

    @Override
    public void loop() {
        telemetry.addData("R", Rpercent());
        telemetry.addData("G", Gpercent());
        telemetry.addData("B", Bpercent());
        telemetry.addData("color_detect", color_detect());
        telemetry.update();
    }


    @Override
    public void init() {
        CS = hardwareMap.get(ColorSensor.class, "cs");
    }


    public double Rpercent() {
        return (double) CS.red() / (CS.red() + CS.green() + CS.blue());      //  CS.green();
    }

    public double Gpercent() {
        return (double) CS.green() / (CS.red() + CS.green() + CS.blue());      //  CS.green();
    }

    public double Bpercent() {
        return (double) CS.blue() / (CS.red() + CS.green() + CS.blue());      //  CS.green();
    }

    public int color_detect() {
        if (Rpercent() < 0.45 && Bpercent() < 0.2) return 2; //yellow
        else if (Rpercent() > 0.35) return 1; //red
        else if (Bpercent() > 0.42) return 3; //blue
        else return 0;
    }
}
