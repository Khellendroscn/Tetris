package net.khe.tetris;

/**
 * Created by hyc on 2016/11/9.
 * 俄罗斯方块事件监听器抽象类
 * 实现了GameEventListener接口
 * 子类需要实现listeningEvent抽象方法
 * 该方法返回一个GameEvent.Type，也就是监听器正在监听的事件类型
 * update方法会使用listeningEvent方法来验证事件类型
 * update方法会在验证事件类型后将参数转发到actionPerformed方法
 * 子类必须实现actionPerformed方法来定义监听器的行为
 */
public abstract class TetrisEventListener implements GameEventListener {
    //子类重载该方法来说明监听的事件类型
    protected abstract GameEvent.Type listeningEvent();
    //子类重载该方法来处理事件
    protected abstract void actionPerformed
            (GameController2 controller,GameEvent event);
    @Override
    public void update(Subject subject,GameEvent event){
        //check
        if(!(subject instanceof GameController2)) return;
        if(!event.type.equals(listeningEvent())) return;
        //perform
        actionPerformed((GameController2)subject,event);
    }
}
