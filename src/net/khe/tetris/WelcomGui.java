package net.khe.tetris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Created by hyc on 2016/11/11.
 */
public class WelcomGui extends JFrame{
    private JButton singlePlayerButton = new JButton("������Ϸ");
    private JButton createRoomButton = new JButton("������Ϸ");
    private JButton joinButton = new JButton("������Ϸ");
    private JButton exitButton = new JButton("�˳���Ϸ");
    public static final int WIDTH = 13;
    public static final int HEIGHT = 20;
    public WelcomGui(){
        setTitle("��ӭ");
        Font font = new Font("΢���ź�",0,20);
        JLabel label = new JLabel("����˹����");
        label.setFont(new Font("΢���ź�",0,30));
        singlePlayerButton.setFont(font);
        createRoomButton.setFont(font);
        joinButton.setFont(font);
        exitButton.setFont(font);
        setLayout(new FlowLayout());
        add(label);
        add(new JPanel(){
            {
                setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
                add(singlePlayerButton);
                add(createRoomButton);
                add(joinButton);
                add(exitButton);
            }
        });
        addActions();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(200,300);
        setVisible(true);
    }
    private void addActions(){
        exitButton.addActionListener((ActionEvent)->{
            System.exit(0);
        });
        singlePlayerButton.addActionListener((ActionEvent)->{
            new GameGui2(13,20);
            dispose();
        });
        createRoomButton.addActionListener((ActionEvent)->{
            TetrisGuiServer server;
            try{
                server = new TetrisGuiServer(WIDTH,HEIGHT);
                server.initOnlineGameComponent();
                InputAddrDialog dialog = new InputAddrDialog(WelcomGui.this);
                dialog.setTitle("�������Ͷ˿�");
                dialog.setHost(server.getHost().toString());
                dialog.setPot(server.getPot());
                dialog.setEditable(false);
                dialog.acceptButton.addActionListener((ActionEvent event)->{
                    try{
                        server.waitForLink();
                        WelcomGui.this.dispose();
                        server.gameStart();
                    }catch (IOException e){
                        JOptionPane.showMessageDialog(
                                null,
                                "����ʧ��",
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                });
            }catch (IOException e){
                e.printStackTrace();
            }
        });
        joinButton.addActionListener((ActionEvent)->{
                InputAddrDialog dialog = new InputAddrDialog(WelcomGui.this);
                dialog.acceptButton.addActionListener((ActionEvent event)->{
                    String host = dialog.getHost();
                    int pot = dialog.getPot();
                    try{
                        TetrisGuiClient client =
                                new TetrisGuiClient(WIDTH,HEIGHT,host,pot);
                        client.initOnlineGameComponent();
                        client.waitForLink();
                        client.gameStart();
                        dispose();
                        WelcomGui.this.dispose();
                    }catch (IOException e){
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(
                                null,
                                "����ʧ��",
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                });
            }
        );
    }

    public static void main(String[] args) {
        new WelcomGui();
    }
}
