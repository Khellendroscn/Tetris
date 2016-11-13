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
                //�����߳��磬�����ƶ�
                controller.getActiveBlock().move(Point.Dir.EAST);
                //�ٴμ��
                checkOutOfRange(controller);
                return;
            }
            if(x>controller.width-1){
                //����ұ߳��磬�����ƶ�
                controller.getActiveBlock().move(Point.Dir.WEST);
                //�ٴμ��
                checkOutOfRange(controller);
                return;
            }
        }
    }
}
