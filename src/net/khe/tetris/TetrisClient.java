package net.khe.tetris;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

/**
 * Created by hyc on 2016/11/11.
 */
public class TetrisClient extends OnlineGameComponent {
    private Socket server;
    public TetrisClient
            (ExecutorService exec, GameController2 p1,Socket server) {
        super(exec, p1);
        this.server = server;
    }

    @Override
    protected Socket link() throws IOException {
        return server;
    }
}
