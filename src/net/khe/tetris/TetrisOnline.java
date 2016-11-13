package net.khe.tetris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import static net.khe.util.Print.println;

/**
 * Created by hyc on 2016/11/11.
 */
public abstract class TetrisOnline {
    protected GameController2 player1;
    protected GameController2 player2;
    public GameGui2P gui = new GameGui2P();
    protected OnlineGameComponent onlineGameComponent;
    protected ExecutorService exec =
            Executors.newCachedThreadPool(new HandleThreadFactory());
    protected Toolkit kit = Toolkit.getDefaultToolkit();
    private GameMessagePanel messagePanel;
    private JDialog waitDialog = new WaitDialog();
    public TetrisOnline(int width, int height)throws IOException{
        player1 = new GameController2(width,height);
        gui.setKit(kit);
        kit.addAWTEventListener(new GameControlListener(),AWTEvent.KEY_EVENT_MASK);
    }
    protected abstract OnlineGameComponent newOnlineGameComponent()throws IOException;
    public void gameStart()throws IOException{
        waitDialog.setVisible(true);
        exec.execute(()->{
            try {
                //分配新线程来等待连接
                onlineGameComponent.start();
                //连接成功，关闭等待对话框
                waitDialog.dispose();
                //初始化其他组件
                player2 = onlineGameComponent.getPlayer2();
                messagePanel = new GameMessagePanel(player1,player2);
                gui.setMessagePanel(messagePanel);
                gui.setPlayers(player1,player2);
                //添加监听器
                addListeners(player1,messagePanel.player1ScoreField);
                addListeners(player2,messagePanel.player2ScoreField);
                gui.display("俄罗斯方块 双人对战版");
                exec.execute(player1);
                exec.execute(player2);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(
                        null,
                        "服务器初始化错误",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
    public void initOnlineGameComponent()throws IOException{
        onlineGameComponent = newOnlineGameComponent();
    }
    private void addListeners(GameController2 player,JTextField scoreField){
        player.addListener(new TetrisEventListener() {
            @Override
            protected GameEvent.Type listeningEvent() {
                return GameEvent.Type.GAMEOVER;
            }

            @Override
            protected void actionPerformed(GameController2 controller, GameEvent event) {
                //exec.shutdownNow();
                String winner;
                int p1Score = new Integer(messagePanel.player1ScoreField.getText());
                int p2Score = new Integer(messagePanel.player2ScoreField.getText());
                winner = p1Score>p2Score?"1p":"2p";
                String message = p1Score == p2Score?"平局":winner+"获胜";
                JOptionPane.showMessageDialog(
                        gui,
                        message,
                        "游戏结束",
                        JOptionPane.INFORMATION_MESSAGE
                );
                System.exit(0);
            }
        });
        player.addListener(new TetrisEventListener() {
            private JTextField score = scoreField;
            @Override
            protected GameEvent.Type listeningEvent() {
                return GameEvent.Type.SCORE_UPDATE;
            }

            @Override
            protected void actionPerformed(GameController2 controller, GameEvent event) {
                score.setText(""+controller.getScore());
            }
        });
    }
    public boolean isLinked(){return onlineGameComponent.isLinked();}
    private class GameControlListener implements AWTEventListener {
        @Override
        public void eventDispatched(AWTEvent event){
            if(event instanceof KeyEvent){
                KeyEvent e = (KeyEvent)event;
                if(e.getID()==KeyEvent.KEY_PRESSED){
                    BlockingQueue<GameEvent> eq = player1.eventQueue;
                    try{
                        switch (e.getKeyCode()){
                            case KeyEvent.VK_UP:
                                eq.put(new GameEvent(GameEvent.Type.TRANSFORM));
                                break;
                            case KeyEvent.VK_DOWN:
                                eq.put(new GameEvent(GameEvent.Type.SPEED_UP));
                                break;
                            case KeyEvent.VK_LEFT:
                                eq.put(new MoveEvent(Point.Dir.WEST));
                                break;
                            case KeyEvent.VK_RIGHT:
                                eq.put(new MoveEvent(Point.Dir.EAST));
                                break;
                            default:break;
                        }
                    }catch (InterruptedException err){
                        println("Gui get key interrupted");
                    }
                }
            }
        }
    }
    class WaitDialog extends JDialog{
        private JButton cancelButton = new JButton("取消");
        public WaitDialog(){
            add(BorderLayout.CENTER,new JLabel("连接中，请稍等..."));
            add(BorderLayout.SOUTH,cancelButton);
            cancelButton.addActionListener((ActionEvent)->{
                System.exit(0);
            });
            setSize(200,100);
        }
    }
    class PlayerExitExceptionHandler
            implements Thread.UncaughtExceptionHandler{

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            //处理玩家强制退出异常
            if(e.getCause() instanceof PlayerExitException){
                JOptionPane.showMessageDialog(
                        gui,
                        "另一个玩家已退出游戏",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                System.exit(0);
            }
        }
    }
    class HandleThreadFactory implements ThreadFactory{
        public Thread newThread(Runnable r){
            Thread t = new Thread(r);
            t.setUncaughtExceptionHandler(new PlayerExitExceptionHandler());
            return t;
        }
    }
}
