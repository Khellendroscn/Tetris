package net.khe.tetris;
import java.util.*;
import java.util.concurrent.*;
import static net.khe.util.Print.*;
/**
 * Created by hyc on 2016/11/9.
 * ����˹������Ϸ������2.0
 * ʹ�ù۲���ģʽʵ��
 * GameController��ά��һ���¼�����
 * ΪGameController�����̺߳󣬿������Ὣ���յ����¼����Ÿ�ÿ��������
 * �����¼��Ѿ������˼����ߣ������Ҫ�޸�Ĭ�ϵļ����б�ֻ��Ҫ�̳�GameController��Ȼ�󸲸�
 * protected void initListeners();����
 * �������¼���gui��ͼ�йأ��ֱ���
 * UPDATE ���¼����ڷ����λ�ò����仯����Ϸ��������Ҫ�ػ��ʱ�򴥷�
 * NEXT_BLOCK_UPDATE ���¼����ڡ���һ�����顱�����仯ʱ����
 * SCORE_UPDATE ���¼����ڵ÷ֲ����仯ʱ����
 * �����������¼�������gui������ϣ����GameController���ṩĬ��ʵ�֣�
 * ��Ҫ�ھ����gui��ͨ������
 * public synchronized void addListener(GameEventListener listener)
 * ����Ӽ�����
 * ���ڼ������ľ������ݲ���TetrisEventListener��
 */
public class GameController2 implements Subject,Runnable{
    private TetrisBlock activeBlock;//����������Ƶķ���
    private TetrisBlock nextBlock;//��һ������
    //���������µķ����map
    private Map<Point,Block> blockMap = new HashMap<>();
    public final int width;//��Ϸ�������
    public final int height;//��Ϸ�����߶�
    public static int gameOverLine = 2;
    private int score = 0;//��Ϸ�÷�
    public final static int SCORE_OF_LINE = 50;//ÿ�з��鱻����ʱȡ�õķ���
    //ÿ�η����ƶ���ĵȴ�ʱ��
    public final static int DEFAULT_SLEEP_TIME = 500;
    private int sleepTime = DEFAULT_SLEEP_TIME;
    private boolean speedUpLock = true;
    //�¼��б�
    public BlockingQueue<GameEvent> eventQueue = new LinkedBlockingDeque<>();
    //�������б�
    private Set<GameEventListener> eventListeners = new HashSet<>();
    //����������
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
        //�������ڲٿصķ������һ������
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
                //������ָ��£����Դ��¼������л�ȡһ���¼������ַ������Լ�����
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
        //�ж��Ƿ�����ǽ��
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
