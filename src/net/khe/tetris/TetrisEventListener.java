package net.khe.tetris;

/**
 * Created by hyc on 2016/11/9.
 * ����˹�����¼�������������
 * ʵ����GameEventListener�ӿ�
 * ������Ҫʵ��listeningEvent���󷽷�
 * �÷�������һ��GameEvent.Type��Ҳ���Ǽ��������ڼ������¼�����
 * update������ʹ��listeningEvent��������֤�¼�����
 * update����������֤�¼����ͺ󽫲���ת����actionPerformed����
 * �������ʵ��actionPerformed�������������������Ϊ
 */
public abstract class TetrisEventListener implements GameEventListener {
    //�������ظ÷�����˵���������¼�����
    protected abstract GameEvent.Type listeningEvent();
    //�������ظ÷����������¼�
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
