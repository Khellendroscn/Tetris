package net.khe.tetris;

import java.util.concurrent.TimeUnit;

/**
 * Created by hyc on 2016/11/12.
 */
public class SleepEventListener extends TetrisEventListener {
    @Override
    protected GameEvent.Type listeningEvent() {
        return GameEvent.Type.SLEEP_EVENT;
    }

    @Override
    protected void actionPerformed(GameController2 controller, GameEvent event) {
        try {
            TimeUnit.MILLISECONDS.sleep(controller.getSleepTime());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
