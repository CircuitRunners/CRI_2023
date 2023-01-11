package org.firstinspires.ftc.teamcode.commands.liftcommands;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.control.PIDCoefficients;
import com.acmerobotics.roadrunner.control.PIDFController;
import com.arcrobotics.ftclib.command.CommandBase;

import org.firstinspires.ftc.teamcode.subsystems.Lift;

@Config
public class LiftPositionCommand extends CommandBase {

    private PIDFController liftController;
    public static PIDCoefficients coefficients = new PIDCoefficients(0.031, 0, 0.001);
    private double kG = 0.176; //gravity
    private double tolerance = 4;
    private boolean holdAtEnd;
    private final Lift lift;
    private final double targetPosition;

    private double liftPosition;

    public LiftPositionCommand(Lift lift, int targetPosition){
        this(lift, targetPosition, false);
    }

    public LiftPositionCommand(Lift lift, int targetPosition, boolean holdAtEnd){
        addRequirements(lift);

        this.holdAtEnd = holdAtEnd;
        this.lift = lift;
        this.targetPosition = targetPosition;

        //Add a feedforward term to counteract gravity
        liftController = new PIDFController(coefficients, 0.00175, 0.002, 0.055, (x, v) -> kG);
        liftController.setOutputBounds(-0.9, 0.95);
    }
    @Override
    public void initialize(){
        //once
        lift.stop();
        liftController.reset();
        liftController.setTargetPosition(targetPosition);
    }

    //Run repeatedly while the command is active
    @Override
    public void execute(){
        liftPosition = lift.getLiftPosition();
        //Update the lift power with the controller
        lift.setLiftPower(liftController.update(liftPosition));
    }

    @Override
    public boolean isFinished(){
        //End if the lift position is within the tolerance
        return Math.abs(targetPosition - liftPosition) <= tolerance;
    }

    @Override
    public void end(boolean interrupted){
        if (holdAtEnd) lift.setLiftPower(0.18);
        else lift.stop();
    }

}
