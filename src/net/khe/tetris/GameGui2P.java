package net.khe.tetris;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by hyc on 2016/11/10.
 * 两人游戏使用的gui
 */
public class GameGui2P extends JFrame{
    private GameController2 player1;
    private GameController2 player2;
    private Toolkit kit;
    public static int blockSize = 24;

    //components:
    private ShowPanel player1Panel;
    private ShowPanel player2Panel;
    private JPanel messagePanel;

    public GameGui2P(){

    }
    public void setPlayers(GameController2 p1,GameController2 p2) {
        player1 = p1;
        player2 = p2;
        player1Panel = new ShowPanel(p1);
        player2Panel = new ShowPanel(p2);
        addUpdateListener(player1Panel);
        addUpdateListener(player2Panel);
    }
    public void setKit(Toolkit kit){
        this.kit = kit;
    }
    public void setMessagePanel(JPanel messagePanel){
        this.messagePanel = messagePanel;
    }
    public void display(String title){
        setLayout(new BoxLayout(getContentPane(),BoxLayout.X_AXIS));
        add(player1Panel);
        add(messagePanel);
        add(player2Panel);
        setTitle(title);
        int width =
                player1Panel.getWidth()+
                player2Panel.getWidth()+
                messagePanel.getWidth();
        int height = player1Panel.getHeight();
        setSize(width,height);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }
    private void addUpdateListener(ShowPanel panel){
        panel.controller.addListener(new TetrisEventListener() {
            private ShowPanel showPanel = panel;
            @Override
            protected GameEvent.Type listeningEvent() {
                return GameEvent.Type.UPDATE;
            }

            @Override
            protected void actionPerformed(GameController2 controller, GameEvent event) {
                updatePanel(showPanel);
            }
        });
    }
    private void drawBlock(BufferedImage cache,Block block){
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
        g.setColor(Color.WHITE);
        g.fillRect(0,0,cache.getWidth(),cache.getHeight());
    }
    private void updatePanel(ShowPanel panel){
        clearCache(panel.cache);
        Graphics g = panel.cache.getGraphics();
        g.setColor(Color.BLACK);
        int line = panel.controller.gameOverLine*blockSize;
        g.drawLine(0,line,panel.getWidth(),line);
        Block[] blocks = panel.controller.getActiveBlock().getBlocks();
        for(Block block:blocks){
            drawBlock(panel.cache,block);
        }
        Map<Point,Block> map = panel.controller.getBlockMap();
        for(Point key:map.keySet()){
            drawBlock(panel.cache,map.get(key));
        }
        panel.repaint();
    }
    class ShowPanel extends JPanel{
        public BufferedImage cache;
        public GameController2 controller;
        public ShowPanel(GameController2 controller){
            this.controller = controller;
            setSize(controller.width*blockSize+100,controller.height*blockSize+40);
            cache = new BufferedImage(getWidth(),getHeight(),
                    BufferedImage.TYPE_INT_ARGB);
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(getWidth(),getHeight()));
        }
        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            g.drawImage(cache,0,0,cache.getWidth(),
                    cache.getHeight(),null);
        }
    }
}
