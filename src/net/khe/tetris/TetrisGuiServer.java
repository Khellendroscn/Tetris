package net.khe.tetris;

import javax.swing.*;
import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by hyc on 2016/11/11.
 */
public class TetrisGuiServer extends TetrisOnline {
    public TetrisGuiServer(int width, int height) throws IOException{
        super(width, height);
    }

    @Override
    protected OnlineGameComponent newOnlineGameComponent() {
        try {
            return new TetrisServer(exec,player1);
        } catch (IOException e) {
            exec.shutdownNow();
            JOptionPane.showMessageDialog(
                    gui,
                    "服务器初始化错误",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            System.exit(1);
            return null;
        }
    }
    public InetAddress getHost(){
        TetrisServer server = (TetrisServer)onlineGameComponent;
        return server.getHost();
    }
    public int getPot(){
        TetrisServer server = (TetrisServer)onlineGameComponent;
        return server.getPot();
    }
}
