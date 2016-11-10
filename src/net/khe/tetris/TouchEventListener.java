package net.khe.tetris;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by hyc on 2016/11/9.
 */
public class TouchEventListener extends TetrisEventListener{
    @Override
    protected GameEvent.Type listeningEvent() {
        return GameEvent.Type.TOUCH;
    }

    @Override
    protected void actionPerformed(GameController2 controller, GameEvent event) {
        //�ָ������ٶ�
        controller.setSleepTime(GameController2.DEFAULT_SLEEP_TIME);
        //���������Ϸ�������˳��÷���
        if(!checkGameOver(controller)) return;
        //��������ڿ��Ƶķ�����ӵ�map
        addBlocks(controller);
        //������������
        tryClearLine(controller);
        //ȡ����һ������
        controller.nextBlocks();
        controller.setSpeedUpLock(true);
        try{
            controller.eventQueue.put(new GameEvent(GameEvent.Type.UPDATE));
            controller.eventQueue.put(new GameEvent(GameEvent.Type.NEXT_BLOCK_UPDATE));
            TimeUnit.MILLISECONDS.sleep(controller.getSleepTime());
        }catch (InterruptedException err){
            System.out.println("TouchEventListener interrupted");
        }
    }
    private boolean checkGameOver(GameController2 controller){
        Block[] blocks = controller.getActiveBlock().getBlocks();
        for(Block block:blocks){
            if(block.getCoordin().getY()<=controller.gameOverLine){
                System.out.println(block);
                try{
                    controller.eventQueue.put
                            (new GameEvent(GameEvent.Type.GAMEOVER));
                }catch (InterruptedException e){
                    System.out.println("checkGameOver interrupted");
                }
                return false;
            }
        }
        return true;
    }
    private void tryClearLine(GameController2 controller){
        //����������������
        Map<Point,Block> blockMap = controller.getBlockMap();
        for(int j=0;j<controller.height;++j){
            int count = 0;
            for(int i=0;i<controller.width;++i){
                if(blockMap.containsKey(new Point(i,j)))
                    ++count;
                else break;
            }
            if(count>=controller.width){
                clearLine(controller,j);
            }
        }
        try {
            controller.eventQueue.put(new GameEvent(GameEvent.Type.SCORE_UPDATE));
        }catch (InterruptedException e){
            System.out.println("tryClearLine interrupted");
        }
    }
    private void clearLine(GameController2 controller,int row){
        Map<Point,Block> map = controller.getBlockMap();
        for(int i=0;i<controller.width;++i){
            map.remove(new Point(i,row));
        }
        for(int j=row-1;j>=0;--j){
            for(int i=0;i<controller.width;++i){
                Point p = new Point(i,j);
                if(map.containsKey(p)){
                    Block block = map.get(p);
                    block.move(Point.Dir.SOUTH);
                    map.remove(p);
                    map.put(block.getCoordin(),block);
                }
            }
        }
        controller.setScore(controller.getScore()+controller.SCORE_OF_LINE);
    }
    private void addBlocks(GameController2 gameController){
        Map<Point,Block> map = gameController.getBlockMap();
        for(Block block:gameController.getActiveBlock().getBlocks()){
            map.put(block.getCoordin(),block);
        }
    }
}
