package net.khe.tetris;

/**
 * Created by hyc on 2016/11/9.
 */
public interface Subject {
    public void addListener(GameEventListener listener);
    public void removeListener (GameEventListener listener);
    public void notifyListeners();
}
