package net.khe.tetris;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by hyc on 2016/11/11.
 */
public class GameMessagePanel extends JPanel {
    public JTextField player1ScoreField = new JTextField("0",5);
    public JTextField player2ScoreField = new JTextField("0",5);
    public NextBlockPanel nextBlockPanel;
    private GameController2 player1;
    private GameController2 player2;
    public static int blockSize = 32;
    public GameMessagePanel(GameController2 p1, GameController2 p2){
        player1 = p1;
        player2 = p2;
        Font font = new Font("微软雅黑",0,20);
        player1ScoreField.setEditable(false);
        player1ScoreField.setFont(font);
        player2ScoreField.setEditable(false);
        player2ScoreField.setFont(font);
        JLabel scoreLabel1 = new JLabel("1p得分: ");
        scoreLabel1.setFont(font);
        JLabel scoreLabel2 = new JLabel("2p得分: ");
        scoreLabel2.setFont(font);
        nextBlockPanel = new NextBlockPanel(player1);
        JLabel nextBlockLabel = new JLabel("下一个: ");
        nextBlockLabel.setFont(font);
        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        add(scoreLabel1);
        add(player1ScoreField);
        add(scoreLabel2);
        add(player2ScoreField);
        add(nextBlockLabel);
        add(nextBlockPanel);
        int width = nextBlockPanel.getWidth();
        int height = nextBlockPanel.getHeight()+200;
        addNextBlockUpdateListeners(nextBlockPanel);
    }
    protected void addNextBlockUpdateListeners(NextBlockPanel panel){
        panel.controller.addListener(new TetrisEventListener() {
            private NextBlockPanel nextBlockPanel = panel;
            @Override
            protected GameEvent.Type listeningEvent() {
                return GameEvent.Type.NEXT_BLOCK_UPDATE;
            }

            @Override
            protected void actionPerformed(GameController2 controller, GameEvent event) {
                //System.out.println("got NextBlockUpdate event");
                updateNextBlockPanel(nextBlockPanel);
            }
        });
    }
    private void drawBlock(BufferedImage cache,Block block){
        //System.out.println(block);
        Graphics g = cache.getGraphics();
        g.setColor(block.getColor());
        int x = block.getCoordin().getX()*blockSize;
        int y = block.getCoordin().getY()*blockSize;
        g.fillRect(x,y,blockSize,blockSize);
        g.setColor(Color.BLACK);
        g.drawRect(x,y,blockSize,blockSize);
    }
    private void clearCache(BufferedImage cache){
        Graphics g = cache.getGraphics();
        g.setColor(Color.GRAY);
        g.fillRect(0,0,cache.getWidth(),cache.getHeight());
    }
    private void updateNextBlockPanel(NextBlockPanel panel){
        clearCache(panel.cache);
        Block firstBlock = panel.controller.getNextBlock().getBlocks()[0];
        int firstx = firstBlock.getCoordin().getX();
        int firsty = firstBlock.getCoordin().getY();
        for(Block block:panel.controller.getNextBlock().getBlocks()){
            int x = block.getCoordin().getX()-firstx+1;
            int y = block.getCoordin().getY()-firsty+1;
            Block temp = new Block(new Point(x,y));
            temp.setColor(block.getColor());
            drawBlock(panel.cache,temp);
        }
        panel.repaint();
    }
    class NextBlockPanel extends JPanel{
        GameController2 controller;
        public BufferedImage cache;
        public NextBlockPanel(GameController2 controller){
            this.controller = controller;
            cache = new BufferedImage(7*blockSize,7*blockSize,
                    BufferedImage.TYPE_INT_ARGB);
            setSize(cache.getWidth(),cache.getHeight());
            setBackground(Color.GRAY);
            setPreferredSize(new Dimension(getWidth(),getHeight()));
        }
        @Override
        public void paintComponent(Graphics g){
            //System.out.println("NextBlockPanel repaint");
            super.paintComponent(g);
            g.drawImage(cache,0,0,cache.getWidth(),cache.getHeight(),null);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("test");
        JPanel messagePanel = new GameMessagePanel
                (new GameController2(10,10),new GameController2(10,10));
        frame.add(messagePanel);
        frame.setSize(messagePanel.getWidth(),messagePanel.getHeight());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
