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
 * ������Ϸ���
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
    //�������ظ÷�������ȡ�׽���
    protected abstract Socket link() throws IOException;
    private void listen(){
        //������һ����ҷ��͵��¼�
        try {
            while (!Thread.interrupted()){
                System.out.println("is socket closed? "+socket.isClosed());
                //���Ի�ȡ������
                GameEvent event = (GameEvent)is.readObject();
                System.out.println("get from socket: "+event);
                //���¼������¼�����
                player2.eventQueue.put(event);
            }
        } catch (IOException e) {
            throw new RuntimeException(new PlayerExitException("��һ������Ѿ��뿪��Ϸ"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            try {
                //�ر���
                if(is!=null) is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void talk(GameEvent event){
        System.out.println("talk");
        //����һ����ҷ����¼�
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
                //���������¼�
                return null;
            }

            @Override
            protected void actionPerformed(GameController2 controller, GameEvent event) {
                //�����¼���ת��
                if(isAcceptEvent(event)){
                    //System.out.println("try to send event");
                    talk(event);
                }else{
                    System.out.println(event.type);
                }
            }
            private boolean isAcceptEvent(GameEvent event){
                //����¼��Ƿ�����ת��
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
        //��ȡ�׽���
        socket = link();
        System.out.println(socket);
        //��ʼ�����2
        os = new ObjectOutputStream(socket.getOutputStream());
        os.writeObject(player1);
        os.flush();
        is = new ObjectInputStream(socket.getInputStream());
        try {
            player2 = (GameController2)is.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //��ʼ��������
        initListener();
        //�����߳�������һ�����
        exec.execute(()->listen());
    }
    public GameController2 getPlayer2(){return player2;}
}
class PlayerExitException extends Exception{
    public PlayerExitException(String what){
        super(what);
    }
}
