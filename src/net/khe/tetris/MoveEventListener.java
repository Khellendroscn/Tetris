package net.khe.tetris;

import java.util.concurrent.TimeUnit;

/**
 * Created by hyc on 2016/11/9.
 */
public class MoveEventListener extends TetrisEventListener{
    //�ƶ��¼�
    @Override
    protected GameEvent.Type listeningEvent() {
        return GameEvent.Type.MOVE;
    }

    @Override
    protected void actionPerformed(GameController2 controller, GameEvent event) {
        if(!(event instanceof MoveEvent)) return;
        MoveEvent e = (MoveEvent)event;
        if(e.dir.equals(Point.Dir.NORTH)) return;
        try {
            if(controller.isTouchBlock()){
                if(((MoveEvent) event).dir == Point.Dir.SOUTH)
                    controller.eventQueue.put(new GameEvent(GameEvent.Type.TOUCH));
                return;
            }
            //�ƶ�����
            if(e.dir!= Point.Dir.SOUTH
                    &&controller.isTouchWall()==3)
                return;
            if(e.dir== Point.Dir.WEST&&
                    controller.isTouchWall()==1)
                return;
            if(e.dir== Point.Dir.EAST&&
                    controller.isTouchWall()==2)
                return;
            controller.getActiveBlock().move(e.dir);
            //���͸����¼�
            controller.eventQueue.put(new GameEvent(GameEvent.Type.UPDATE));
            //������������һ�������¼��������һ�������¼����ȴ�
            if(e.dir== Point.Dir.SOUTH) {
                //�ȴ�
                controller.eventQueue.put(new GameEvent(GameEvent.Type.SLEEP_EVENT));
                controller.eventQueue.put(new MoveEvent(Point.Dir.SOUTH));
            }
        }catch (InterruptedException err){
            System.out.println("MoveEventListener interrupted");
        }
    }
}
