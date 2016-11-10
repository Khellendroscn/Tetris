package net.khe.tetris;

import net.khe.util.Generator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import static net.khe.util.Print.*;
/**
 * Created by hyc on 2016/11/7.
 * 使用模板方法实现的游戏控制器
 * 旧的实现，已废弃
 */
public class TetrisGameController {
    private TetrisBlock activeBlock;
    private TetrisBlock nextBlock;
    private Map<Point,Block> blocks = new HashMap<>();
    public static int scoreOfLine = 50;
    private int score = 0;
    //private boolean failed = false;
    //private boolean scoreChanged = false;
    //private boolean hasTouched =false;
    private final Generator<TetrisBlock> blockGenerator;
    public final int width;
    public final int height;
    public final int DEFAULT_WAIT_TIME = 500;
    private int WAIT_TIME = DEFAULT_WAIT_TIME;
    public TetrisGameController(int width,int height){
        this.width = width;
        this.height = height;
        blockGenerator = new TetrisBlock.RandomGenerator(new Point(width/2,0));
        activeBlock = blockGenerator.next();
        nextBlock = blockGenerator.next();
    }
    public synchronized TetrisBlock getActiveBlock(){return activeBlock;}
    protected synchronized void updateBlocks(){}
    protected synchronized void updateScore(){}
    protected synchronized void gameOver(){}
    protected synchronized void updateNextBlock(){}
    public synchronized void update(){
        updateBlocks();
        updateNextBlock();
        updateScore();
    }
    public void move(Point.Dir dir){
        synchronized (this){
            println(Arrays.toString(activeBlock.getBlocks()));
            activeBlock.move(dir);
        }
        checkOutOfRange();
        updateBlocks();
        checkBlockTouched();
        try {
            TimeUnit.MILLISECONDS.sleep(WAIT_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    public void transform(){
        synchronized (this){
            activeBlock.transform();
        }
        checkOutOfRange();
        updateBlocks();
        try {
            TimeUnit.MILLISECONDS.sleep(WAIT_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private synchronized Point[] getBlockCoordins(){
        Point[] points = new Point[activeBlock.getBlocks().length];
        for(int i=0;i<points.length;++i){
            points[i] = activeBlock.getBlocks()[i].getCoordin();
        }
        return points;
    }
    public synchronized int getScore(){return score;}
    public synchronized TetrisBlock getNextBlock(){return nextBlock;}
    //public synchronized boolean isFailed(){return failed;}
    //public synchronized boolean isScoreChanged(){return scoreChanged;}
    //public synchronized void setScoreChanged(boolean isChanged){scoreChanged = isChanged;}
    public synchronized Map<Point,Block> getBlocks(){return blocks;}
    //public synchronized boolean isTouched(){return hasTouched;}

    private synchronized void checkOutOfRange(){
        for(Point point:getBlockCoordins()){
            if(point.getX()<0){
                activeBlock.move(Point.Dir.EAST);
                checkOutOfRange();
                break;
            }
            if(point.getX()>=width){
                println("touch the wall!");
                activeBlock.move(Point.Dir.WEST);
                checkOutOfRange();
                break;
            }
            if(point.getY()>=height-1){
                println(point+"touch bottom");
                touched();
                break;
            }
        }
        println("not out of range");
    }
    private synchronized void checkBlockTouched(){
        for(Point point:getBlockCoordins()){
            if(blocks.containsKey(point.move(Point.Dir.SOUTH))){
                touched();
                println("blocks touched");
                break;
            }
        }
    }
    private synchronized void tryClearLine(){
        for(int j=height-1;j>=0;--j){
            int blockCount = 0;
            for(int i=0;i<width;++i){
                if(blocks.containsKey(new Point(i,j)))
                    ++blockCount;
            }
            if(blockCount>=width){
                clearLine(j);
                tryClearLine();
            }
        }
        updateScore();
    }
    private synchronized void clearLine(int index){
        for(int i=0;i<width;++i){
            println("delete blocks");
            blocks.remove(new Point(i,index));
        }
        for(int j=index-1;j>=0;--j){
            for(int i=0;i<width;++i){
                Block block = blocks.get(new Point(i,j));
                if(block!=null){
                    println("move down");
                    block.move(Point.Dir.SOUTH);
                    blocks.remove(new Point(i,j));
                    blocks.put(block.getCoordin(),block);
                }
            }
        }
        score+=scoreOfLine;
        updateBlocks();
        try {
            TimeUnit.MILLISECONDS.sleep(WAIT_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private synchronized void touched(){
        for(Block block:activeBlock.getBlocks()){
            if(block.getCoordin().getY()<=2){
                //failed = true;
                //notifyAll();
                println("game over "+block);
                gameOver();
                return;
            }
            blocks.put(block.getCoordin(),block);
        }
        tryClearLine();
        updateBlocks();
        try {
            TimeUnit.MILLISECONDS.sleep(WAIT_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        activeBlock = nextBlock;
        nextBlock = blockGenerator.next();
        WAIT_TIME = DEFAULT_WAIT_TIME;
        updateNextBlock();
        //hasTouched = true;
        //notifyAll();
    }
    public void speedUp(){
        WAIT_TIME = 10;
    }
}
