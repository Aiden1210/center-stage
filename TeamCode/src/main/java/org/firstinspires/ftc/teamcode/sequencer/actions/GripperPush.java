package org.firstinspires.ftc.teamcode.sequencer.actions;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.BillsUnexpectedRoadtrip.Cadbot;

public class GripperPush implements RobotAction{

    private boolean done = false;
    private double duration;
    private boolean push = false; // false is pull
    private Cadbot cadbot;
    private ElapsedTime runtime = new ElapsedTime();
    private boolean firstTime = true;

    public GripperPush(Cadbot cadbot, double duration, boolean push){
        this.cadbot = cadbot;
        this.duration = duration;
        this.push = push;
    }

    @Override
    public void update() {
        if(firstTime){
            runtime.reset();
            firstTime = false;
            if(push)
                cadbot.motorPool.setGripperPush();
            else
                cadbot.motorPool.setGripperPull();
        }
        else{
            if(runtime.seconds() > duration){
                cadbot.motorPool.setGripperStop();
                done = true;
            }
        }
    }

    @Override
    public boolean isDone() {
        return done;
    }
}
