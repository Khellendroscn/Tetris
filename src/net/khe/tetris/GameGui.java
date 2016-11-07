package net.khe.tetris;

import org.omg.PortableInterceptor.SUCCESSFUL;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static net.khe.util.Print.*;
/**
 * Created by hyc on 2016/11/7.
 */
public class GameGui extends JFrame {
    private GuiGameController gameController;
    private ExecutorService exec = Executors.newCachedThreadPool();
    private BufferedImage cache;
    private BufferedImage nextBlockCache;
    private JTextField scoreField = new JTextField(10);
    private final int blockSize = 32;
    private JPanel showPanel = new ShowPanel();
    private JPanel nextBlockPanel = new NextBlockPanel();
    private Toolkit kit = Toolkit.getDefaultToolkit();
    private boolean updatedFlag = false;
    //private JButton keyListenerButton;
    public GameGui(int width,int height){
        gameController = new GuiGameController(width,height);
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
        setSize(width*blockSize+130,height*blockSize+40);
        //getContentPane().addKeyListener(new EnterListener());
        setFont(font);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        gameController.update();
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
        g.drawLine(0,3*blockSize,cache.getWidth()*blockSize,3*blockSize);
        for(Point key:gameController.getBlocks().keySet()){
            Block block = gameController.getBlocks().get(key);
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
    private class GuiGameController
            extends TetrisGameController implements Runnable{
        public GuiGameController(int width, int height) {
            super(width, height);
        }
        @Override
        public synchronized void updateBlocks(){
            updatePanel();
            updatedFlag = true;
        }
        @Override
        public synchronized void updateScore(){
            scoreField.setText(""+gameController.getScore());
        }
        @Override
        public synchronized void gameOver(){
            JOptionPane.showMessageDialog(
                    GameGui.this,
                    "得分："+getScore(),
                    "Game Over",
                    JOptionPane.INFORMATION_MESSAGE
            );
            System.exit(0);
        }
        @Override
        public synchronized void updateNextBlock(){
            Graphics g = nextBlockCache.getGraphics();
            g.setColor(Color.GRAY);
            g.fillRect(0,0,nextBlockCache.getWidth(),nextBlockCache.getHeight());
            int firstx = gameController.getNextBlock().getBlocks()[0].getCoordin().getX();
            int firsty = gameController.getNextBlock().getBlocks()[0].getCoordin().getY();
            for(Block block:gameController.getNextBlock().getBlocks()){
                int x = (block.getCoordin().getX()-firstx)*blockSize;
                int y = (block.getCoordin().getY()-firsty)*blockSize;
                g.setColor(block.getColor());
                g.fillRect(x,y,blockSize,blockSize);
                g.setColor(Color.BLACK);
                g.drawRect(x,y,blockSize,blockSize);
            }
            nextBlockPanel.repaint();
        }
        @Override
        public void run(){
            while (!Thread.interrupted()){
                gameController.move(Point.Dir.SOUTH);
            }
        }
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
            if(updatedFlag){
                if(event instanceof KeyEvent){
                    KeyEvent e = (KeyEvent)event;
                    if(e.getID()==KeyEvent.KEY_PRESSED){
                        switch (e.getKeyCode()){
                            case KeyEvent.VK_UP:gameController.transform();break;
                            case KeyEvent.VK_DOWN:gameController.speedUp();break;
                            case KeyEvent.VK_LEFT:gameController.move(Point.Dir.WEST);break;
                            case KeyEvent.VK_RIGHT:gameController.move(Point.Dir.EAST);break;
                            case KeyEvent.VK_ESCAPE:System.exit(0);
                            default:break;
                        }
                        //updatedFlag = false;
                    }
                }
            }
        }
    }
    public static void main(String[] args) {
        GameGui gui = new GameGui(13,20);
    }
}
