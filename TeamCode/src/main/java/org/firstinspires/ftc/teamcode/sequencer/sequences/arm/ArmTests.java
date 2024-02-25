package org.firstinspires.ftc.teamcode.sequencer.sequences.arm;

import org.firstinspires.ftc.teamcode.BillsAmazingArm.ArmPoseXZ;
import org.firstinspires.ftc.teamcode.BillsUnexpectedRoadtrip.Cadbot;
import org.firstinspires.ftc.teamcode.sequencer.engine.ActionSequence;
import org.firstinspires.ftc.teamcode.sequencer.engine.SequenceBuilder;

public class ArmTests {

    // First, set motor power to 0 and read the motion telemetry while moving it to various positions
    // After passing this inspection, set the motor power to about .3, extending the arm manually to straight up,
    // reset the start position to match what is below
    // and then run this test to hold the arm in the described position.
    public static ActionSequence reachAccuracy(Cadbot cadbot) throws InterruptedException {
        return new SequenceBuilder(cadbot)
                .moveArmTo(ArmPoseXZ.home())
                .moveArmTo(ArmPoseXZ.carry())
                .moveArmTo(ArmPoseXZ.ready2())
                //.moveArmTo(ArmPoseXZ.forward())
                //.moveArmTo(ArmPoseXZ.passingOverForward())
                .moveArmTo(ArmPoseXZ.reachingForward(12, 8.0))
                .moveArmTo(ArmPoseXZ.reachingForward(12, 0.0))
                .wait(2.0)
                .moveArmTo(ArmPoseXZ.reachingForward(12, 8.0))
                .moveArmTo(ArmPoseXZ.reachingForward(13, 0.0))
                .wait(2.0)
                .moveArmTo(ArmPoseXZ.reachingForward(13, 8.0))
                .moveArmTo(ArmPoseXZ.reachingForward(14, 0.0))
                .moveArmTo(ArmPoseXZ.reachingForward(14,0.0))
                .wait(2.0)
                .moveArmTo(ArmPoseXZ.reachingForward(13, 8.0))
                //.moveArmTo(ArmPoseXZ.passingOverForward())
                .moveArmTo(ArmPoseXZ.ready2())
                .moveArmTo(ArmPoseXZ.carry())
                .build();
    }

    public static ActionSequence armTest1(Cadbot cadbot){
        return new SequenceBuilder(cadbot)
                .moveArmTo(ArmPoseXZ.home())
                .moveArmTo(ArmPoseXZ.carry())
                .moveArmTo(ArmPoseXZ.ready2())
                //.moveArmTo(ArmPoseXZ.forward())
                //.moveArmTo(ArmPoseXZ.passingOverForward())
                .moveArmTo(ArmPoseXZ.reachingBackward(-12, 8.0))
                .moveArmTo(ArmPoseXZ.reachingBackward(-12, 0.0))
                .wait(1.0)
                .gripperPush(3.0, false)
                .moveArmTo(ArmPoseXZ.reachingBackward(-12, 8.0))
                //.moveArmTo(ArmPoseXZ.passingOverForward())
                .moveArmTo(ArmPoseXZ.ready2())
                .moveArmTo(ArmPoseXZ.carry())
                .build();
    }

    // move out and return to carry position (tested successfully at full power, but jerky)
    public static ActionSequence armTest2(Cadbot cadbot){
        return new SequenceBuilder(cadbot)
                .moveArmTo(ArmPoseXZ.home())
                .moveArmTo(ArmPoseXZ.carry())
                .moveArmTo(ArmPoseXZ.ready2())
                .moveArmTo(ArmPoseXZ.forward())
                .gripperPush(0.01, true)
                .moveArmTo(ArmPoseXZ.forward())
                .gripperPush(1.0, false)
                .moveArmTo(ArmPoseXZ.ready2())
                .moveArmTo(ArmPoseXZ.carry())
                .build();
    }

    // moves the arm outward, forward, runs the gripper, then to straight up, then back to carry
    public static ActionSequence armTest3(Cadbot cadbot){
        return new SequenceBuilder(cadbot)
                .moveArmTo(ArmPoseXZ.home())
                .moveArmTo(ArmPoseXZ.carry())
                .moveArmTo(ArmPoseXZ.ready2())
                .moveArmTo(ArmPoseXZ.forward())
                .gripperPush(0.01, true)
                .moveArmTo(ArmPoseXZ.forward())
                .gripperPush(1.0, false)
                .moveArmTo(ArmPoseXZ.straightUp())
                .wait(2.0)
                .moveArmTo(ArmPoseXZ.ready2())
                .moveArmTo(ArmPoseXZ.carry())
                .build();
    }

    // test that all motors run in the correct direction

    // test that it can move from home to straight up
}
