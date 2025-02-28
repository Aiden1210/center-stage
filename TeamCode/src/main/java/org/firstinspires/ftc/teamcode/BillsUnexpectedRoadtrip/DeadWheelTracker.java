package org.firstinspires.ftc.teamcode.BillsUnexpectedRoadtrip;

import android.util.Log;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.BillsUtilityGarage.Vector2D;
import org.firstinspires.ftc.teamcode.BillsUtilityGarage.Vector2D1;

import java.util.ArrayList;

/**
 * The point of a journey is not to arrive.
 */
public class DeadWheelTracker {

    // Dead wheel data
    public static double TICKS_PER_REV = 2000;
    public static double WHEEL_RADIUS = 0.9448819; // in                24mm = 0.9448819 in
    public static double GEAR_RATIO = 1; // output (wheel) speed / input (encoder) speed - not used
    public static double LATERAL_DISTANCE = 7.25; // in; distance between the left and right wheels
    public static double FORWARD_OFFSET = -3.3; //-3.75 + 0.3; // in; offset of the lateral wheel from center of robot (negative is toward the back), the first number is measured, the second is tuning
    public static double INCHES_PER_TICK = 2*Math.PI*WHEEL_RADIUS/TICKS_PER_REV;
    public static double DISTANCE_FROM_WALL = 8.5; // The distance from the back of the robot to its center at start
    public static double COLLISION_RADIUS = 10; // Assuming we remain square to the field with 18" robot and have 1" margin for safety

    private double heading = 0; // robot's bearing in radians
    private double xWorld = 0; // the robot's position in world coordinates, in inches
    private double yWorld = 0;

    public double dyTot = 0; // for calibrating the FORWARD_OFFSET, measure this while spinning, divide by radians to get offset

    // Last wheel positions
    int lfOld;
    int rfOld;
    int rbOld;

    // last field position
    double headingOld = 0;
    double xWorldOld = 0;
    double yWorldOld = 0;

    // field velocity and last field velocity
    double vxWorld, vxWorldOld;
    double vyWorld, vyWorldOld;
    double omegaWorld, omegaWorldOld;

    double axWorld;
    double ayWorld;
    double alphaWorld;

    double dt, dtOld, dta;

    // records
    double topSpeed;
    int topSpeedIncident;
    double topAcceleration;
    int topAccelerationIncident;

    public final static int GRANULARITY = 10;
    public final static double GRANES = GRANULARITY;
    double[][] powerResponseX = new double[GRANULARITY][GRANULARITY];
    double[][] powerResponseY = new double[GRANULARITY][GRANULARITY];

    private ElapsedTime runtime = new ElapsedTime();

    // history of positions and headings
    public ArrayList<Vector2D1> path = new ArrayList<>();

    // history of velocities and angular velocities
    public ArrayList<Vector2D1> velocities = new ArrayList<>();

    // history of history of accelerations and angular accelerations
    public ArrayList<Vector2D1> accelerations = new ArrayList<>();

    // history of time intervals
    public ArrayList<Double> times = new ArrayList<>();

    private Cadbot cadbot;
    private MotorPool motorPool;
    private double steps = 0;
    private double avgDt = 0;

    public void initialize(Cadbot cadbot){
        this.cadbot = cadbot;
        this.motorPool = cadbot.motorPool;
        // initialize the coordinates
        resetPose(GameField.getAutonomousStartPose(cadbot.allianceColor, cadbot.alliancePosition, DISTANCE_FROM_WALL));
    }

    public void resetPose(Vector2D1 pose){
        xWorldOld = pose.getX();
        yWorldOld = pose.getY();
        headingOld = pose.getHeading();
        xWorld = xWorldOld;
        yWorld = yWorldOld;
        heading = headingOld;
        path.add(new Vector2D1(xWorldOld, yWorldOld, headingOld));
        velocities.add(new Vector2D1(0, 0, 0));
        accelerations.add(new Vector2D1(0, 0, 0));
        dt = runtime.seconds();
        runtime.reset();
        times.add(dt);
        Log.e("DriveTo", "DWT resetPose to" + pose.toString());
    }

    public void update(){

        int lfp = motorPool.getLeftFrontTicks();   // Uses 1 bulk-read to obtain ALL the motor data
        int rfp = motorPool.getRightFrontTicks();   // There is no penalty for doing more `get` operations in this cycle,
        int rbp = motorPool.getRightBackTicks();
        dt = runtime.seconds();
        runtime.reset();  // reset the stop watch
        dta = (dt + dtOld)/2.0; // average dt over the last two time intervals
        dtOld = dt;

        // difference from last time
        double dlf = lfp - lfOld;
        double drf = rfp - rfOld;
        double drb = rbp - rbOld;

        // store the current value
        lfOld = lfp;
        rfOld = rfp;
        rbOld = rbp;

        // convert from ticks to inches
        double e1 = INCHES_PER_TICK * dlf;
        double e2 = INCHES_PER_TICK * drf;
        double e3 = INCHES_PER_TICK * drb;

        // update robot motion
        double dTheta = (e2 - e1)/LATERAL_DISTANCE;
        double dx = (e1 + e2)/2;  // forward motion
        double dy = e3 - FORWARD_OFFSET * dTheta;
        dyTot += dy; // todo: for calibrating only

        // transform motion to world coordinates and update world coordinates
        heading += dTheta;
        double dxWorld = dx * Math.cos(heading) - dy * Math.sin(heading);
        double dyWorld = dy * Math.cos(heading) + dx * Math.sin(heading);
        xWorld += dxWorld;
        yWorld += dyWorld;

        // velocity update
        vxWorld = (xWorld - xWorldOld)/dt;
        vyWorld = (yWorld - yWorldOld)/dt;
        omegaWorld = (heading - headingOld)/dt;

        // acceleration will lag behind the application of motor power
        axWorld = (vxWorld - vxWorldOld)/dta;
        ayWorld = (vyWorld - vyWorldOld)/dta;
        alphaWorld = (omegaWorld - omegaWorldOld)/dta;

        // out with the old, in with the new...
        xWorldOld = xWorld;
        yWorldOld = yWorld;
        headingOld = heading;

        vxWorldOld = vxWorld;
        vyWorldOld = vyWorld;
        omegaWorldOld = omegaWorld;

        path.add(new Vector2D1(xWorld, yWorld, heading));
        velocities.add(new Vector2D1(vxWorld, vyWorld, omegaWorld));
        accelerations.add(new Vector2D1(axWorld, ayWorld, alphaWorld));
        times.add(new Double(dt));

        // throw away outliers of dt and calculate the average dt
        if(dt < .1 && dt > 0.000001) {
            avgDt = (avgDt * steps + dt)/(steps+1);
            steps++;
        }

        // ka-chow!
    }

    // call this function after an update if you want to record things like top speed and top acceleration
    public void recordsUpdate(){
        Vector2D v = new Vector2D(getVelocity().getX(), getVelocity().getY());
        topSpeed = Math.max(v.magnitude(), topSpeed);
        Vector2D a = new Vector2D(getAcceleration().getX(), getAcceleration().getY());
        topAcceleration = Math.max(a.magnitude(), topAcceleration);
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("  pos=");
        sb.append(getPose().toString());
        sb.append("  vel=");
        sb.append(getVelocity().toString());
        sb.append("  acc=");
        sb.append(getAcceleration().toString());
        sb.append("  dt=");
        sb.append(String.format("%5f", getTime()));
        sb.append("  avgDt=");
        sb.append(String.format("%5f", avgDt));
        return sb.toString();
    }

    public Vector2D projectNextPose(Vector2D delta){
        Vector2D1 pose = getPose();
        Vector2D1 velocity = getVelocity();
        Vector2D acc = projectAcceleration(delta); // we need to project the acceleration from the delta
        double avgDt = getAvgDt() * 10; // we can project 10 time steps in the future, at 60 in/s that's looking 6 in ahead, enough time to stop

        Vector2D projected = new Vector2D(
                pose.getX() + velocity.getX() * avgDt + .5 * acc.getX() * avgDt * avgDt,
                pose.getY() + velocity.getY() * avgDt + .5 * acc.getY() * avgDt * avgDt);

        Log.e("DeadWheelTracker", this + " proj=" + projected + " pow=" + delta);
        return projected;
    }

    /**
     * To project acceleration we consider the maximum stopping rate.
     * Since the time to stop is estimated at avgDt * 10, and the maximum speed observed is
     * around 60 in/sec.  a = (vf - vi) / (avgDt * 10) = 600
     * @param delta
     * @return
     */
    public Vector2D projectAcceleration(Vector2D delta){
        //delta.getX()/GRANES;
        // given the current velocity and the power delta, what is the likely acceleration
        //return new Vector2D(powerResponseX[vel][del], powerResponseY[vel][del]);
        return delta.copy().multiplyBy(600); // as high as 1500 but stopped us 8 inches too early
    }

    public Vector2D1 getPose(){
        return path.get(path.size()-1);
    }

    public Vector2D1 getVelocity(){
        return velocities.get(velocities.size()-1);
    }

    public Vector2D1 getAcceleration(){
        return accelerations.get(accelerations.size()-1);
    }

    public double getTime(){
        return times.get(times.size()-1);
    }

    public double getAvgDt(){
        return avgDt;
    }
}
