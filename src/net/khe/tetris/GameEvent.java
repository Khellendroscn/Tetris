package net.khe.tetris;

/**
 * Created by hyc on 2016/11/9.
 * ��Ϸ�¼���
 * ����һ����¼�������¼������ö��
 */
public class GameEvent {
    public enum Type{
        MOVE,TRANSFORM,SPEED_UP,
        TOUCH, GAMEOVER, UPDATE,
        SCORE_UPDATE,NEXT_BLOCK_UPDATE
    }
    public final Type type;
    public GameEvent(Type type){
        this.type = type;
    }
    @Override
    public String toString(){
        return "GameEvent_"+type;
    }
}
