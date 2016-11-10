package net.khe.tetris;

/**
 * Created by hyc on 2016/11/9.
 */
public class SpeedUpEventListener extends TetrisEventListener {
    //���������¼�
    @Override
    protected GameEvent.Type listeningEvent() {
        return GameEvent.Type.SPEED_UP;
    }

    @Override
    protected void actionPerformed(GameController2 controller, GameEvent event) {
        //���ٵȴ�ʱ��
        if(controller.couldSpeedUp()){
            controller.setSleepTime(10);
            controller.setSpeedUpLock(false);
        }
    }
}
