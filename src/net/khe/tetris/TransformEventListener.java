package net.khe.tetris;

import java.util.concurrent.TimeUnit;

/**
 * Created by hyc on 2016/11/9.
 */
public class TransformEventListener extends TetrisEventListener {

    @Override
    protected GameEvent.Type listeningEvent() {
        return GameEvent.Type.TRANSFORM;
    }

    @Override
    protected void actionPerformed(GameController2 controller, GameEvent event) {
        controller.getActiveBlock().transform();
        if(controller.isTouchBlock()) controller.getActiveBlock().transform();
        checkOutOfRange(controller);
        try {
            controller.eventQueue.put(new GameEvent(GameEvent.Type.UPDATE));
        } catch (InterruptedException e) {
            System.out.println("TransformEventListener intettupted");
        }
    }
    private void checkOutOfRange(GameController2 controller){
        Block[] blocks = controller.getActiveBlock().getBlocks();
        for(Block block:blocks){
            int x = block.getCoordin().getX();
            if(x<0){
                //如果左边出界，向右移动
                controller.getActiveBlock().move(Point.Dir.EAST);
                //再次检查
                checkOutOfRange(controller);
                return;
            }
            if(x>controller.width-1){
                //如果右边出界，向左移动
                controller.getActiveBlock().move(Point.Dir.WEST);
                //再次检查
                checkOutOfRange(controller);
                return;
            }
        }
    }
}
