package edu.nyu.at2706.quoridor;

public class Vec2 {
    public float x, y;

    public Vec2(){
        x = y = 0;
    }

    public Vec2(float x, float y) {
        this.x = x;

        this.y = y;
    }

    public Vec2(Vec2 v) {
        this.x = v.x;
        this.y = v.y;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public Vec2 negate() {
        x *= -1;
        y *= -1;
        return this;
    }

    public Vec2 add(Vec2 v) {
        this.x += v.x;
        this.y += v.y;
        return this;
    }

    public Vec2 subtract(Vec2 v){
        this.x -= v.x;
        this.y -= v.y;
        return this;
    }

    public Vec2 multiply(float f){
        this.x *= f;
        this.y *= f;
        return this;
    }

    public Vec2 divide(float f) {
        this.x /= f;
        this.y /= f;
        return this;
    }

    public static float dot(Vec2 v1, Vec2 v2) {
        return v1.x * v2.x + v1.y * v2.y;
    }

    public static float norm2(Vec2 v) {
        return dot(v, v);
    }

    public static float norm(Vec2 v) {
        return (float)Math.sqrt(dot(v, v));
    }

    public Vec2 normalize() {
        float n = norm(this);
        x /= n;
        y /= n;
        return this;
    }

    public static Vec2 normalize(Vec2 v) {
        float n = norm(v);
        v.x /= n;
        v.y /= n;
        return v;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vec2 vec2 = (Vec2) o;

        return Float.compare(vec2.x, x) == 0 && Float.compare(vec2.y, y) == 0;

    }

    @Override
    public int hashCode() {
        int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
        result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
        return result;
    }

    @Override
    public String toString(){
        return "{x = " + x + " | y = " + y + " }";
    }
}
