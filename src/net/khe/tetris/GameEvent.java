package net.khe.tetris;

import java.io.Serializable;

/**
 * Created by hyc on 2016/11/9.
 * ��Ϸ�¼���
 * ����һ����¼�������¼������ö��
 */
public class GameEvent implements Serializable{
    public enum Type{
        MOVE,TRANSFORM,SPEED_UP,
        TOUCH, GAMEOVER, UPDATE,
        SCORE_UPDATE,NEXT_BLOCK_UPDATE,BLOCK_CHANGED_EVENT,
        SLEEP_EVENT,
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
