package dev.oblivion.client.gui.animation;

public class Animation {
    private float current;
    private float target;
    private final float speed;

    public Animation(float initial, float speed) {
        this.current = initial;
        this.target = initial;
        this.speed = speed;
    }

    public Animation(float speed) {
        this(0f, speed);
    }

    public void update() {
        if (Math.abs(current - target) < 0.001f) {
            current = target;
        } else {
            current += (target - current) * speed;
        }
    }

    public float get() {
        return current;
    }

    public void setTarget(float target) {
        this.target = target;
    }

    public void set(float value) {
        this.current = value;
        this.target = value;
    }

    public float getTarget() {
        return target;
    }

    public boolean isComplete() {
        return Math.abs(current - target) < 0.001f;
    }
}
