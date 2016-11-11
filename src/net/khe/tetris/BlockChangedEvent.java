package net.khe.tetris;

/**
 * Created by hyc on 2016/11/11.
 */
public class BlockChangedEvent extends GameEvent {
    public final TetrisBlock activeBlock;
    public BlockChangedEvent(TetrisBlock activeBlock) {
        super(Type.BLOCK_CHANGED_EVENT);
        this.activeBlock = activeBlock;
    }
}
