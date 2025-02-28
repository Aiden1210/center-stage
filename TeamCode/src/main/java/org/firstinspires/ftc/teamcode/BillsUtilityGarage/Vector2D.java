package org.firstinspires.ftc.teamcode.BillsUtilityGarage;

import android.util.Log;

import org.firstinspires.ftc.teamcode.BillsAmazingArm.ArmConstants;

public class Vector2D {
    private double x;
    private double y;

    public Vector2D(){

    }

    public Vector2D(double x, double y){
        this.x = x;
        this.y = y;
    }

    public double getX(){ return x; }

    public double getY(){ return y; }

    public Vector2D set(double x, double y){
        this.x = x;
        this.y = y;
        return this;
    }

    public Vector2D set(Vector2D vector2D){
        this.x = vector2D.x;
        this.y = vector2D.y;
        return this;
    }

    public Vector2D subtract(Vector2D vector2D){
        this.x -= vector2D.x;
        this.y -= vector2D.y;
        return this;
    }

    public Vector2D subtract(double dx, double dy){
        this.x -= dx;
        this.y -= dy;
        return this;
    }

    public Vector2D divideBy(double a){
        this.x = this.x/a;
        this.y = this.y/a;
        return this;
    }

    public Vector2D multiplyBy(double a){
        this.x = this.x*a;
        this.y = this.y*a;
        return this;
    }

    public Vector2D add(Vector2D vector2D){
        this.x += vector2D.x;
        this.y += vector2D.y;
        return this;
    }

    public Vector2D add(double dx, double dy){
        this.x += dx;
        this.y += dy;
        return this;
    }

    public Vector2D copy(){
        return new Vector2D(x, y);
    }

    public Vector2D rotate(double angle){
        double x = this.x;
        double y = this.y;
        this.x = x * Math.cos(Math.toRadians(angle)) - y * Math.sin(Math.toRadians(angle));
        this.y = x * Math.sin(Math.toRadians(angle)) + y * Math.cos(Math.toRadians(angle));
        return this;
    }

    public double magnitude(){
        return Math.sqrt(x*x + y*y);
    }

    public double bearingAngleInRadians(){ return Math.atan2(y, x); }

    public double bearingAngle(){
        return zeroTo360(Math.toDegrees(Math.atan2(y, x)));
    }

    public static double zeroTo360(double angle){
        while(angle > 360){
            angle -= 360;
        }
        while(angle < 0){
            angle += 360;
        }
        return angle;
    }

    public static double range180ToNeg180(double angle){
        while (angle > 180) {
            angle -= 360;
        }
        while (angle < -180){
            angle += 360;
        }
        return angle;
    }

    public double distance(Vector2D p){
        return distance(this, p);
    }

    public static double distance(Vector2D p1, Vector2D p2){
        double dx = p2.x - p1.x;
        double dy = p2.y = p1.y;
        return Math.sqrt(dx*dx + dy*dy);
    }

    public void limitToRange(double min, double max){
        x = UtilityKit.limitToRange(x, min, max);
        y = UtilityKit.limitToRange(y, min, max);
    }

    public String toString(){
        return "["+x+", " + y + "]";
        //String.format("[%.2f, %.2f]", x, y);
    }

    public Vector2D unitVector() {
        double m = magnitude();
        if(m == 0){
            return new Vector2D(0.0, 0.0);
        }
        return new Vector2D(x/m, y/m);
    }

    public void limitMagnitude(double max) {
        if (magnitude() > max) {
            set(unitVector().multiplyBy(max));
        }
    }

    public static Vector2D weightedAverage(Vector2D v1, double w1, Vector2D v2, double w2) {
        return v1.copy().multiplyBy(w1).add(v2.copy().multiplyBy(w2)).divideBy(w1 + w2);
    }

    public Vector2D setX(double x){
        this.x = x;
        return this;
    }

    public Vector2D setY(double y){
        this.y = y;
        return this;
    }
}
