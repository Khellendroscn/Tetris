package net.khe.tetris;

/**
 * Created by hyc on 2016/11/9.
 */
public class MoveEvent extends GameEvent{
    public final Point.Dir dir;
    public MoveEvent(Point.Dir dir) {
        super(Type.MOVE);
        this.dir = dir;
    }
    @Override
    public String toString(){
        return super.toString()+" "+dir;
    }
}
