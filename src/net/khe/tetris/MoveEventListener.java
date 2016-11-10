package net.khe.tetris;

import java.util.concurrent.TimeUnit;

/**
 * Created by hyc on 2016/11/9.
 */
public class MoveEventListener extends TetrisEventListener{
    //移动事件
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
            //移动方块
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
            //发送更新事件
            controller.eventQueue.put(new GameEvent(GameEvent.Type.UPDATE));
            //如果被处理的是一个下落事件，再添加一个下落事件并等待
            if(e.dir== Point.Dir.SOUTH) {
                //如果发现方块接触，发送接触事件
                if(controller.isTouchBlock())
                    controller.eventQueue.put(new GameEvent(GameEvent.Type.TOUCH));
                controller.eventQueue.put(new MoveEvent(Point.Dir.SOUTH));
                //等待
                TimeUnit.MILLISECONDS.sleep(controller.getSleepTime());
            }
        }catch (InterruptedException err){
            System.out.println("MoveEventListener interrupted");
        }
    }
}
