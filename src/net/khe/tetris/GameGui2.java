package net.khe.tetris;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static net.khe.util.Print.println;

/**
 * Created by hyc on 2016/11/7.
 */
public class GameGui2 extends JFrame {
    private GameController2 gameController;
    private ExecutorService exec = Executors.newCachedThreadPool();
    private BufferedImage cache;
    private BufferedImage nextBlockCache;
    private JTextField scoreField = new JTextField(10);
    private final int blockSize = 32;
    private JPanel showPanel = new ShowPanel();
    private JPanel nextBlockPanel = new NextBlockPanel();
    private Toolkit kit = Toolkit.getDefaultToolkit();
    public GameGui2(int width, int height){
        gameController = new GameController2(width,height);
        cache = new BufferedImage(width*blockSize,height*blockSize,
                BufferedImage.TYPE_INT_ARGB);
        nextBlockCache = new BufferedImage(5*blockSize,5*blockSize,
                BufferedImage.TYPE_INT_ARGB);
        setTitle("俄罗斯方块");
        Font font = new Font("微软雅黑",0,16);
        JPanel rightPanel = new JPanel(){
            {
                setLayout(new GridLayout(0,1));
                scoreField.setEditable(false);
                add(new JPanel(){
                    {
                        setFont(font);
                        JLabel label = new JLabel("得分");
                        label.setFont(font);
                        scoreField.setFont(font);
                        add(BorderLayout.NORTH,label);
                        add(BorderLayout.CENTER,scoreField);
                    }
                });
                JLabel label = new JLabel("下一个：");
                label.setFont(font);
                add(label);
                nextBlockPanel.setBackground(Color.GRAY);
                add(nextBlockPanel);
                JTextArea explain = new JTextArea(6,3);
                explain.setText("说明：\n回车键开始\n上下左右控制");
                explain.setEditable(false);
                add(explain);
            }
        };
        rightPanel.setBorder(new EtchedBorder());
        add(BorderLayout.EAST,rightPanel);
        kit.addAWTEventListener(new GameStartListener(),AWTEvent.KEY_EVENT_MASK);
        showPanel.setBackground(Color.WHITE);
        add(BorderLayout.CENTER,showPanel);
        setSize(width*blockSize+220,height*blockSize+40);
        //getContentPane().addKeyListener(new EnterListener());
        setFont(font);
        initController();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

    }
    private synchronized void drawBlock(Graphics g,Block block){
        int x = block.getCoordin().getX()*blockSize;
        int y = block.getCoordin().getY()*blockSize;
        g.setColor(block.getColor());
        g.fillRect(x,y,blockSize,blockSize);
        g.setColor(Color.BLACK);
        g.drawRect(x,y,blockSize,blockSize);
    }
    private synchronized void updatePanel(){
        Graphics g = cache.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0,0,cache.getWidth(),cache.getHeight());
        g.setColor(Color.BLACK);
        int l = gameController.gameOverLine;
        g.drawLine(0,l*blockSize,cache.getWidth()*blockSize,l*blockSize);
        for(Point key:gameController.getBlockMap().keySet()){
            Block block = gameController.getBlockMap().get(key);
            drawBlock(g,block);
        }
        for(Block block:gameController.getActiveBlock().getBlocks()){
            drawBlock(g,block);
        }
        showPanel.repaint();
    }

    private class ShowPanel extends JPanel{
        @Override
        protected void paintComponent(Graphics g){
            super.paintComponent(g);
            g.drawImage(cache,0,0,cache.getWidth(),cache.getHeight(),null);
        }
    }
    private class NextBlockPanel extends JPanel{
        @Override
        protected void paintComponent(Graphics g){
            println("repaint");
            super.paintComponent(g);
            g.drawImage(nextBlockCache,0,0,nextBlockCache.getWidth(),nextBlockCache.getHeight(),null);
        }
    }
    private void initController(){
        gameController.addListener(new TetrisEventListener() {
            @Override
            protected GameEvent.Type listeningEvent() {
                return GameEvent.Type.GAMEOVER;
            }

            @Override
            protected void actionPerformed(GameController2 controller, GameEvent event) {
                exec.shutdownNow();
                JOptionPane.showMessageDialog(
                        GameGui2.this,
                        "Score: "+scoreField.getText(),
                        "Game Over",
                        JOptionPane.INFORMATION_MESSAGE
                );
                System.exit(0);
            }
        });
        gameController.addListener(new TetrisEventListener() {
            @Override
            protected GameEvent.Type listeningEvent() {
                return GameEvent.Type.UPDATE;
            }

            @Override
            protected void actionPerformed(GameController2 controller, GameEvent event) {
                updatePanel();
            }
        });
        gameController.addListener(new TetrisEventListener() {
            @Override
            protected GameEvent.Type listeningEvent() {
                return GameEvent.Type.NEXT_BLOCK_UPDATE;
            }

            @Override
            protected void actionPerformed(GameController2 controller, GameEvent event) {
                Graphics g = nextBlockCache.getGraphics();
                g.setColor(Color.GRAY);
                g.fillRect(0,0,nextBlockCache.getWidth(),nextBlockCache.getHeight());
                Block firstBlock = gameController.getNextBlock().getBlocks()[0];
                for(Block block:gameController.getNextBlock().getBlocks()){
                    int x = block.getCoordin().getX()-firstBlock.getCoordin().getX();
                    int y = block.getCoordin().getY()-firstBlock.getCoordin().getY();
                    Block b = new Block(new Point(x,y));
                    b.setColor(firstBlock.getColor());
                    drawBlock(g,b);
                }
                nextBlockPanel.repaint();
            }
        });
        gameController.addListener(new TetrisEventListener() {
            @Override
            protected GameEvent.Type listeningEvent() {
                return GameEvent.Type.SCORE_UPDATE;
            }

            @Override
            protected void actionPerformed(GameController2 controller, GameEvent event) {
                scoreField.setText(""+gameController.getScore());
            }
        });
    }
    private class GameStartListener implements AWTEventListener{
        @Override
        public void eventDispatched(AWTEvent event) {
            if(event instanceof KeyEvent){
                KeyEvent keyEvent = (KeyEvent) event;
                if(keyEvent.getID()==KeyEvent.KEY_RELEASED){
                    if(keyEvent.getKeyCode()==KeyEvent.VK_ENTER){
                        exec.execute(gameController);
                        kit.removeAWTEventListener(this);
                        kit.addAWTEventListener(new GameControlListener(),AWTEvent.KEY_EVENT_MASK);
                    }
                }
            }
        }
    }
    private class GameControlListener implements AWTEventListener{
        @Override
        public void eventDispatched(AWTEvent event){
            if(event instanceof KeyEvent){
                KeyEvent e = (KeyEvent)event;
                if(e.getID()==KeyEvent.KEY_PRESSED){
                    BlockingQueue<GameEvent> eq = gameController.eventQueue;
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
                            case KeyEvent.VK_R:gameController.reset();break;
                            case KeyEvent.VK_ESCAPE:System.exit(0);break;
                            default:break;
                        }
                    }catch (InterruptedException err){
                        println("Gui get key interrupted");
                    }
                }
            }
        }
    }
    public static void main(String[] args) {
        GameGui2 gui = new GameGui2(13,20);
    }
}
