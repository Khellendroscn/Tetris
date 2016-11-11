package net.khe.tetris;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;

/**
 * Created by hyc on 2016/11/11.
 */
public class TetrisServer extends OnlineGameComponent {
    private ServerSocket server;
    public static int defaultPot = 7926;
    public TetrisServer(ExecutorService exec, GameController2 p1)
    throws IOException{
        super(exec, p1);
        server = new ServerSocket(defaultPot);
    }

    @Override
    protected Socket link() throws IOException{
        return server.accept();
    }

    public InetAddress getHost(){
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }
    public int getPot(){
        return server.getLocalPort();
    }
}
