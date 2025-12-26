package GDK.engine;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Random;


@Getter
@NoArgsConstructor

public class Vector2 {
    public double x, y;

    public Vector2(double x, double y){
        this.x = x;
        this.y = y;
    }
    public Vector2(int x, int y){
        this.x = x;
        this.y = y;
    }

    public Vector2(int value){
        this.x = value;
    }

    public Vector2(double value){
        this.x = value;
    }

    public static double angleBetween(Vector2 first, Vector2 second){
        return Math.atan2((second.y - first.y), (second.x - first.x));
    }

    public static double angleToRad(double angleDeg) {
        return Math.toRadians(angleDeg);
    }

    public static Vector2 minus() {
        return new Vector2(-1, -1);
    }

    public double angleBetween(Vector2 vec2){
        return Math.atan2((vec2.y - y), (vec2.x - x));
    }

    public static double angleDegBetween(Vector2 first, Vector2 second){
        return Math.atan2((second.y - first.y), (second.x - first.x)) * (180 / Math.PI);
    }

    public static double angleToDeg(double angleRad){
        return angleRad  * (180 / Math.PI);
    }

    public double angleDegBetween(Vector2 vec2){
        return Math.atan2((vec2.y - y), (vec2.x - x)) * (180 / Math.PI);
    }


    public boolean isZero(){
        return x == 0 && y == 0;
    }
    public boolean oneIsZero(){
        return x == 0 || y == 0;
    }

    public static Vector2 zero(){
        return new Vector2(0, 0);
    }

    public static Vector2 left(){
        return new Vector2(-1, 0);
    }

    public static Vector2 right(){
        return new Vector2(1, 0);
    }

    public static Vector2 up(){
        return new Vector2(0, -1);
    }

    public static Vector2 down(){
        return new Vector2(0, 1);
    }

    public Vector2 addVector(Vector2 vec){
        Vector2 nv = new Vector2(x, y);
        nv.x += vec.x;
        nv.y += vec.y;
        return nv;
    }

    public Vector2 addVector(double value){
        Vector2 nv = new Vector2(x, y);
        nv.x += value;
        nv.y += value;
        return nv;
    }

    public void set(Vector2 vec){
        x = vec.x;
        y = vec.y;
    }

    /**
     * Нормализует вектор (делает длину равной 1)
     * @return нормализованный вектор
     */
    public Vector2 normalize() {
        double mag = magnitude();
        if (mag > 0) {
            return new Vector2(x / mag, y / mag);
        }
        return new Vector2(0, 0);
    }

    /**
     * Вычисляет длину (модуль) вектора
     * @return длина вектора
     */
    public double magnitude() {
        return Math.sqrt(x * x + y * y);
    }

    public int xInt(){return (int)x;}
    public int yInt(){return (int)y;}

    public Vector2 reverse(){
        return new Vector2(-x, -y);
    }

    public Vector2 copy(){
        return new Vector2(x, y);
    }

    public boolean equal(Vector2 other){
        return other.x == x && other.y == y;
    }


    /**
     @return возвращает дистанцию между двумя векторами
     */
    public static double distance(Vector2 first, Vector2 second){
        return Math.sqrt(Math.pow(second.x - first.x, 2) + Math.pow(second.y - first.y, 2));
    }
    /**
     @return возвращает дистанцию между двумя векторами
     */
    public double distance(Vector2 second){
        return Math.sqrt(Math.pow(second.x - x, 2) + Math.pow(second.y - y, 2));
    }

    public void zeroing(){
        x = 0; y = 0;
    }

    public boolean oneMore(Vector2 sec){
        return x > sec.x || y > sec.y;
    }
    public boolean moreR(Vector2 sec){
        return x > sec.x && y < sec.y;
    }
    public boolean more(Vector2 sec){
        return x > sec.x && y > sec.y;
    }

    public static Vector2 toVec(int[] intArray){
        return new Vector2(intArray[0], intArray[1]);
    }
    public static Vector2 toVec(int intValue){
        return new Vector2(intValue, intValue);
    }
    public static Vector2 toVec(Vector2 vec){
        return new Vector2(vec.x, vec.y);
    }

    public boolean notEqual(Vector2 vec){
        return vec.x != x || vec.y != y;
    }

    public boolean oneLess(Vector2 sec){
        return x < sec.x || y < sec.y;
    }
    public boolean lessR(Vector2 sec){
        return x < sec.x && y > sec.y;
    }
    public boolean less(Vector2 sec){
        return x < sec.x && y < sec.y;
    }

    public boolean oneIsNegative(){
        return x < 0 || y < 0;
    }

    public boolean oneIsPositive(){
        return x > 0 || y > 0;
    }

    public boolean isNegativeR(){
        return x < 0 && y > 0;
    }

    public boolean isPositiveR(){
        return x > 0 && y < 0;
    }

    public boolean isNegative(){
        return x < 0 && y < 0;
    }

    public boolean isPositive(){
        return x > 0 && y > 0;
    }

    public static Vector2 one(){
        return new Vector2(1,1);
    }

    public boolean isOneZeroAbs(){
        boolean xOne = x == 1 || x == -1;
        boolean xZero = x == 0;
        boolean yOne = y == -1 || y == 1;
        boolean yZero = y == 0;
        return (xOne && (yZero || yOne)) || (yOne && xZero);
    }

    @Override
    public String toString(){
        return "{ "+ x + " , " + y + " }";
    }

    public Vector2 getRandomIntRange(){
        Random rand = new Random();
        return new Vector2(rand.nextInt((int)x+1), rand.nextInt((int)y+1));
    }

    public Vector2 subtractVector(Vector2 vec){
        Vector2 nv = new Vector2(x, y);
        nv.x -= vec.x;
        nv.y -= vec.y;
        return nv;
    }

    public Vector2 subtractVector(double value){
        Vector2 nv = new Vector2(x, y);
        nv.x -= value;
        nv.y -= value;
        return nv;
    }

    public Vector2 increaseVector(double value){
        Vector2 nv = new Vector2(x, y);
        nv.x *= value;
        nv.y *= value;
        return nv;
    }

    public Vector2 divideVector(double value){
        Vector2 nv = new Vector2(x, y);
        nv.x /= value;
        nv.y /= value;
        return nv;
    }

    public Vector2 divideVector(Vector2 vec){
        Vector2 nv = new Vector2(x, y);
        nv.x /= vec.x;
        nv.y /= vec.y;
        return nv;
    }

    public Vector2 roundVector(){
        Vector2 nv = new Vector2(x, y);
        nv.x = Math.round(nv.x);
        nv.y = Math.round(nv.y);
        return nv;
    }

    public Vector2 increaseVector(Vector2 vec){
        Vector2 nv = new Vector2(x, y);
        nv.x *= vec.x;
        nv.y *= vec.y;
        return nv;
    }
    public Vector2 remainderVector(double value){return remainderVector(new Vector2(value, value));}
    public Vector2 remainderVector(Vector2 vec){
        Vector2 nv = new Vector2(x, y);
        nv.x %= vec.x;
        nv.y %= vec.y;
        return nv;
    }
    public Vector2 negative() {
        return new Vector2(-x, -y);
    }

    public double sum() {
        return x + y;
    }

    public Vector2 vecInt() {
        return new Vector2((int)x, (int)y);
    }
}
