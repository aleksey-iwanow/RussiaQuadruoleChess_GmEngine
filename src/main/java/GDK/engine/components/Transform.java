package GDK.engine.components;

import GDK.engine.*;

public class Transform extends Component {

    public class Translate{
        public Vector2 offset;
        public Vector2 newPos;
        public Vector2 duration;
        public int msTime;
        public double speed;

        public Translate(Vector2 offset, Vector2 duration, int msTime, double speed, Vector2 newPos){
            this.speed = speed;
            this.duration = duration;
            this.msTime = msTime;
            this.offset = offset;
            this.newPos = newPos;
        }
    }

    public Vector2 position;
    public Vector2 localPosition=Vector2.zero();
    public Vector2 size;
    public double angle;

    public Translate translateObject;
    public int index;
    public Runnable coroutineRunnable;
    public boolean isTranslate=false;
    public boolean positionRelativeParent;
    public Transform(GameObject gameObject, String[] position, String[] size, int index, String angle) {
        super(gameObject);
        this.size = new Vector2( Double.parseDouble(size[0]), Double.parseDouble(size[1]) );
        this.position =  new Vector2( Double.parseDouble(position[0]) - this.size.x/2, Double.parseDouble(position[1]) - this.size.y/2 );
        localPosition.set(this.position);
        this.angle = Double.parseDouble(angle);
        this.index = index;
    }


    public void setPositionRelativeParent(boolean value){
        positionRelativeParent = value;
    }

    public Vector2 getSize(){
        return new Vector2(size.x, size.y);
    }

    public Vector2 getPos(){
        return new Vector2(position.x, position.y);
    }

    public Vector2 getCenterPosition(){
        return position.addVector(size.divideVector(2));
    }
    public void setCenterPosition(Vector2 center){
        Vector2 global = center.subtractVector(size.divideVector(2));
        /*if (gameObject.parent != null)
            localPosition = global.subtractVector(gameObject.parent.transform.position);
        else
            localPosition = global;*/
        position.set(global);

    }

    public void setCenterPosition(double x, double y){
        setCenterPosition(new Vector2(x, y));
    }

    public void translate(Vector2 offset, int timeMs, double speed){
        translate(offset, offset.normalize(), timeMs, speed);
    }


    public void translateToTarget(Vector2 target, int timeMs, double speed){
        Vector2 offset = target.subtractVector(position);
        translate(offset, offset.normalize(), timeMs, speed);
    }

    public void translate(Vector2 offset, Vector2 dur, int timeMs, double speed){
        isTranslate = true;
        if (coroutineRunnable != null) {
            position.set(position);
            Main.stopCoroutine(coroutineRunnable);
        }
        Vector2 newPos = position.addVector(offset);
        translateObject = new Translate(offset, dur, timeMs, speed, newPos);
        CoroutineObject cObj = new CoroutineObject(() -> moveTranslate(dur, newPos,speed), timeMs, true);

        coroutineRunnable = Main.startCoroutine(cObj);
    }

    private boolean translateRelease(Vector2 dur, Vector2 newPos){
        boolean oneCheck = (dur.oneIsNegative() && position.oneLess(newPos)) || (dur.oneIsPositive() && position.oneMore(newPos));

        boolean twoCheck = (dur.isNegative() && position.less(newPos)) || (dur.isPositive() && position.more(newPos));
        boolean threeCheck = (dur.isNegativeR() && position.lessR(newPos)) || (dur.isPositiveR() && position.moreR(newPos));
        return dur.oneIsZero() && oneCheck ||  twoCheck || threeCheck;
    }

    private void moveTranslate(Vector2 dur, Vector2 newPos, double speed){
        move(dur.increaseVector(Time.deltaTime()), speed);
        //(dur.oneIsNegative() && position.less(newPos))
        // || (dur.oneIsPositive() && position.more(newPos))
        if (translateRelease(dur, newPos)){
            position.set(newPos);

            Main.stopCoroutine(coroutineRunnable);
            coroutineRunnable = null;

            isTranslate = false;
        }
    }

    public void setSize(Vector2 newSize){
        size.set(newSize);
    }

    public void setSize(double value){
        size.x = value;
        size.y = value;
    }

    public void setSize(double x, double y){
        size.x = x;
        size.y = y;
    }


    public boolean isHover(Vector2 other){
        return other.x >= position.x && other.x <= position.x + size.x &&
                other.y >= position.y && other.y <= position.y + size.y;
    }


    public void setPos(Vector2 newPos){
        position.set(newPos);
    }

    public void setPos(double x, double y){
        position.set(new Vector2(x, y));
    }

    @Override
    public void update() {
        if (!positionRelativeParent) return;

        if (gameObject.parent != null)
            this.position = gameObject.parent.transform.position.addVector(localPosition);
        else
            this.position = localPosition;
    }

    public double getGlobalAngle(){
        double parentAngle = 0;
        if (gameObject.parent != null)
            parentAngle = gameObject.parent.transform.getGlobalAngle();
        return (angle + parentAngle) % 360.0;
    }

    public void setAngle(double angle){
        this.angle = angle;
        updateRotate();
        if (gameObject.parent == null) return;

        double dir = Vector2.distance(
                gameObject.parent.transform.getCenterPosition(),
                getCenterPosition());

        position = gameObject.parent.transform.getCenterPosition().addVector(
                new Vector2(dir*Math.cos(Math.toRadians(this.angle)),
                        dir*Math.sin(Math.toRadians(this.angle))))
                .subtractVector(size.divideVector(2));
    }

    private void updateRotate(){
        this.angle %= 360.0;
    }

    public void move(Vector2 direction, double speed){
        position = position.addVector(direction.increaseVector(speed));
    }
    public void move(Vector2 direction){
        move(direction, 1);
    }

    public void move(double x, double y){
        move(new Vector2(x, y), 1);
    }
}