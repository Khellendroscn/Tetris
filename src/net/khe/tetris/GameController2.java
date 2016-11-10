package net.khe.tetris;
import java.util.*;
import java.util.concurrent.*;
import static net.khe.util.Print.*;
/**
 * Created by hyc on 2016/11/9.
 * 俄罗斯方块游戏控制器2.0
 * 使用观察者模式实现
 * GameController会维护一个事件队列
 * 为GameController启动线程后，控制器会将接收到的事件发放给每个监听者
 * 部分事件已经内置了监听者，如果想要修改默认的监听列表，只需要继承GameController，然后覆盖
 * protected void initListeners();方法
 * 有三个事件与gui绘图有关，分别是
 * UPDATE 该事件会在方块的位置产生变化，游戏主界面需要重绘的时候触发
 * NEXT_BLOCK_UPDATE 该事件会在“下一个方块”产生变化时触发
 * SCORE_UPDATE 该事件会在得分产生变化时触发
 * 由于这三个事件与具体的gui紧密耦合，因此GameController不提供默认实现，
 * 需要在具体的gui中通过方法
 * public synchronized void addListener(GameEventListener listener)
 * 来添加监听器
 * 关于监听器的具体内容参阅TetrisEventListener类
 */
public class GameController2 implements Subject,Runnable{
    private TetrisBlock activeBlock;//玩家正常控制的方块
    private TetrisBlock nextBlock;//下一个方块
    //保存已落下的方块的map
    private Map<Point,Block> blockMap = new HashMap<>();
    public final int width;//游戏场景宽度
    public final int height;//游戏场景高度
    public static int gameOverLine = 2;
    private int score = 0;//游戏得分
    public final static int SCORE_OF_LINE = 50;//每行方块被消除时取得的分数
    //每次方块移动后的等待时间
    public final static int DEFAULT_SLEEP_TIME = 500;
    private int sleepTime = DEFAULT_SLEEP_TIME;
    private boolean speedUpLock = true;
    //事件列表
    public BlockingQueue<GameEvent> eventQueue = new LinkedBlockingDeque<>();
    //监听者列表
    private Set<GameEventListener> eventListeners = new HashSet<>();
    //方块生成器
    private static TetrisBlock.RandomGenerator generator;
    public GameController2(int width,int height){
        this.width = width;
        this.height = height;
        generator = new TetrisBlock.RandomGenerator(new Point(width/2,0));
        activeBlock = generator.next();
        nextBlock = generator.next();
        initListeners();
        try {
            eventQueue.put(new GameEvent(GameEvent.Type.UPDATE));
            eventQueue.put(new GameEvent(GameEvent.Type.NEXT_BLOCK_UPDATE));
            eventQueue.put(new GameEvent(GameEvent.Type.SCORE_UPDATE));
            eventQueue.put(new MoveEvent(Point.Dir.SOUTH));
        } catch (InterruptedException e) {
            println("GameController2 init interrupted");
        }
    }
    protected void initListeners(){
        addListener(new TouchEventListener());
        addListener(new MoveEventListener());
        addListener(new TransformEventListener());
        addListener(new SpeedUpEventListener());
    }
    public synchronized void nextBlocks(){
        //更新正在操控的方块和下一个方块
        activeBlock = nextBlock;
        nextBlock = generator.next();
    }
    public synchronized TetrisBlock getActiveBlock(){return activeBlock;}

    @Override
    public synchronized void addListener(GameEventListener listener) {
        eventListeners.add(listener);
    }

    @Override
    public synchronized void removeListener(GameEventListener listener) {
        eventListeners.remove(listener);
    }

    @Override
    public synchronized void notifyListeners() {
        try {
            if(!Thread.interrupted()){
                //如果发现更新，尝试从事件队列中获取一个事件，并分发给所以监听者
                GameEvent event = eventQueue.take();
                println("get event: "+event);
                for(GameEventListener listener:eventListeners){
                    listener.update(this,event);
                }
            }
        }catch (InterruptedException e){
            println("NotifyListeners interrupted.");
        }
    }

    public synchronized Map<Point,Block> getBlockMap() {
        return blockMap;
    }
    public synchronized int isTouchWall(){
        //判断是否碰到墙壁
        Block[] blocks = activeBlock.getBlocks();
        for(Block block:blocks){
            int x = block.getCoordin().getX();
            if(x<=0)return 1;
            if(x>=width-1)return 2;
            if(blockMap.containsKey(new Point(x+1,block.getCoordin().getY())))
                return 3;
            if(blockMap.containsKey(new Point(x-1,block.getCoordin().getY())))
                return 3;
        }
        return 0;
    }
    public synchronized boolean isTouchBlock(){
        for(Block block:activeBlock.getBlocks()){
            if(block.getCoordin().getY()>=height-1) return true;
            if(blockMap.containsKey(block.getCoordin())) return true;
            Point southCoordin = block.getCoordin().move(Point.Dir.SOUTH);
            if(blockMap.containsKey(southCoordin)){
                println("touch at"+southCoordin);
                return true;
            }
        }
        return false;
    }

    public synchronized int getSleepTime() {
        return sleepTime;
    }
    public synchronized void resetSleepTime(){
        sleepTime = DEFAULT_SLEEP_TIME;
    }
    public synchronized void setSleepTime(int sleepTime){this.sleepTime = sleepTime;}
    public synchronized TetrisBlock getNextBlock(){return nextBlock;}
    public synchronized void reset(){
        try {
            println("reset");
            eventQueue.clear();
            blockMap.clear();
            score = 0;
            resetSleepTime();
            activeBlock = generator.next();
            nextBlock = generator.next();
            eventQueue.put(new GameEvent(GameEvent.Type.UPDATE));
            eventQueue.put(new GameEvent(GameEvent.Type.NEXT_BLOCK_UPDATE));
            eventQueue.put(new GameEvent(GameEvent.Type.SCORE_UPDATE));
            eventQueue.put(new MoveEvent(Point.Dir.SOUTH));
            println("succeed");
        }catch (InterruptedException e){
            System.out.println("reset interrupted");
        }
    }

    public synchronized int getScore() {
        return score;
    }

    public synchronized void setScore(int score) {
        this.score = score;
    }
    @Override
    public void run(){
        while (!Thread.interrupted()){
            notifyListeners();
        }
        println("pause");
    }

    public synchronized boolean couldSpeedUp() {
        return speedUpLock;
    }

    public synchronized void setSpeedUpLock(boolean speedUpLock) {
        this.speedUpLock = speedUpLock;
    }
}
