package org.firstinspires.ftc.teamcode.auto.cycle;

import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.outoftheboxrobotics.photoncore.PhotonCore;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.firstinspires.ftc.teamcode.commands.BulkCacheCommand;
import org.firstinspires.ftc.teamcode.commands.autocommands.DropPreloadCommand;
import org.firstinspires.ftc.teamcode.commands.autocommands.ThreeCycleTrajectories;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.subsystems.Arm;
import org.firstinspires.ftc.teamcode.subsystems.Claw;
import org.firstinspires.ftc.teamcode.subsystems.Lift;
import org.firstinspires.ftc.teamcode.vision.BeaconDetector;

@Disabled
@Autonomous(name = "Blue Three Cycle")
public class LeftThreeCycleAuto extends CommandOpMode {


    private SampleMecanumDrive drive;
    private Claw claw;
    private Lift lift;
    private Arm arm;


    private BeaconDetector beaconDetector;
    private BeaconDetector.BeaconTags beaconId = BeaconDetector.BeaconTags.LEFT;

    @Override
    public void initialize() {
        PhotonCore.enable();
        schedule(new BulkCacheCommand(hardwareMap));

        drive = new SampleMecanumDrive(hardwareMap);
        claw = new Claw(hardwareMap);
        lift = new Lift(hardwareMap);
        arm = new Arm(hardwareMap);

        drive.setPoseEstimate(ThreeCycleTrajectories.leftStartingPosition);
        ThreeCycleTrajectories.generateTrajectories(drive);
        claw.close();

        beaconDetector = new BeaconDetector(hardwareMap);

        DropPreloadCommand preloadCommand = new DropPreloadCommand(drive, lift, claw, arm, false);


        //Start vision
        beaconDetector.startStream();

        while (!isStarted()) {
            beaconId = beaconDetector.update();
            telemetry.addLine("Ready for start!");
            telemetry.addData("Beacon", beaconId);
            telemetry.update();
        }

        beaconDetector.stopStream();

        schedule(new SequentialCommandGroup(
                preloadCommand
        ));
    }
}
