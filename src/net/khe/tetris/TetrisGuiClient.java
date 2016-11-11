package net.khe.tetris;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by hyc on 2016/11/11.
 */
public class TetrisGuiClient extends TetrisOnline {
    private String host;
    private int pot;
    public TetrisGuiClient(int width, int height,String host,int pot) throws IOException{
        super(width, height);
        //System.out.println(host+" "+pot);
        this.host = host;
        this.pot = pot;
    }

    @Override
    protected OnlineGameComponent newOnlineGameComponent() throws IOException{
        Socket socket = new Socket(host,pot);
        return new TetrisClient(exec,player1,socket);
    }
}
