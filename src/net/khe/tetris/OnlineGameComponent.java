package net.khe.tetris;

import javax.management.ObjectInstance;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

/**
 * Created by hyc on 2016/11/11.
 * 线上游戏组件
 */
public abstract class OnlineGameComponent {
    protected GameController2 player1;
    protected GameController2 player2;
    protected ExecutorService exec;
    protected boolean linked = false;
    private Socket socket;
    private ObjectOutputStream os;
    private ObjectInputStream is;
    public OnlineGameComponent
            (ExecutorService exec,GameController2 p1){
        this.exec = exec;
        this.player1 = p1;
    }
    public boolean isLinked(){return linked;}
    //子类重载该方法来获取套接字
    protected abstract Socket link() throws IOException;
    private void listen(){
        //收听另一个玩家发送的事件
        try {
            while (!Thread.interrupted()){
                System.out.println("is socket closed? "+socket.isClosed());
                //尝试获取输入流
                GameEvent event = (GameEvent)is.readObject();
                System.out.println("get from socket: "+event);
                //将事件加入事件队列
                player2.eventQueue.put(event);
            }
        } catch (IOException e) {
            throw new RuntimeException(new PlayerExitException("另一个玩家已经离开游戏"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            try {
                //关闭流
                if(is!=null) is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void talk(GameEvent event){
        System.out.println("talk");
        //向另一个玩家发送事件
        try {
            os.writeObject(event);
            os.flush();
            System.out.println("talk succeed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void initListener(){
        System.out.println("player1 == null? "+player1 == null);
        player1.addListener(new TetrisEventListener() {
            @Override
            protected GameEvent.Type listeningEvent() {
                //监听所有事件
                return null;
            }

            @Override
            protected void actionPerformed(GameController2 controller, GameEvent event) {
                //下落事件不转发
                if(isAcceptEvent(event)){
                    //System.out.println("try to send event");
                    talk(event);
                }else{
                    System.out.println(event.type);
                }
            }
            private boolean isAcceptEvent(GameEvent event){
                //检查事件是否允许转发
                switch (event.type){
                    case MOVE:
                        if(event instanceof MoveEvent){
                            MoveEvent e = (MoveEvent) event;
                            if(e.dir== Point.Dir.SOUTH) return false;
                            else return true;
                        }else return false;
                    case TRANSFORM:return true;
                    case SPEED_UP:return true;
                    case BLOCK_CHANGED_EVENT:return true;
                    default:return false;
                }
            }
        });
        player2.addListener(new TetrisEventListener() {
            @Override
            protected GameEvent.Type listeningEvent() {
                return GameEvent.Type.BLOCK_CHANGED_EVENT;
            }

            @Override
            protected void actionPerformed(GameController2 controller, GameEvent event) {
                BlockChangedEvent e = (BlockChangedEvent) event;
                controller.setActiveBlock(e.activeBlock);
            }
        });
    }
    public void start()throws IOException{
        //获取套接字
        socket = link();
        System.out.println(socket);
        //初始化玩家2
        os = new ObjectOutputStream(socket.getOutputStream());
        os.writeObject(player1);
        os.flush();
        is = new ObjectInputStream(socket.getInputStream());
        try {
            player2 = (GameController2)is.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //初始化监听器
        initListener();
        //启动线程收听另一个玩家
        exec.execute(()->listen());
    }
    public GameController2 getPlayer2(){return player2;}
}
class PlayerExitException extends Exception{
    public PlayerExitException(String what){
        super(what);
    }
}
