package net.khe.tetris;

import net.khe.util.Factory;
import net.khe.util.Generator;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by hyc on 2016/11/7.
 */
public abstract class TetrisBlock {
    protected Block[] blocks;
    protected boolean hasTransformed = false;
    protected abstract void initBlocks(Point firstBlockCoordin);
    public Block[] getBlocks(){return blocks;}
    public void setBlocks(Block[] blocks){this.blocks = blocks;}
    public void setColor(Color color){
        for(Block block:blocks) block.setColor(color);
    }
    public abstract void transform();//变形
    public void move(Point.Dir dir){
        for(Block block:blocks)
            block.move(dir);
    }

    public static class RandomGenerator implements Generator<TetrisBlock>{
        private final Point firstBlockCoordin;
        private final Generator<Color> colorGenerator =
                new net.khe.util.RandomGenerator.Color();
        RandomGenerator(Point firstBlockCoordin){
            this.firstBlockCoordin = firstBlockCoordin;
        }
        private static List<Factory<? extends TetrisBlock>> blockTypes =
                new ArrayList<>(Arrays.asList(
                   new SquareTetrisBlock.TetrisBlockFactory(),
                   new LTetrisBlock.TetrisBlockFactory(),
                   new ITetrisBlock.TetrisBlockFactory(),
                   new STetrisBlock.TetrisBlockFactory()
                ));
        private static Random rand = new Random();
        @Override
        public TetrisBlock next() {
            TetrisBlock block =
                    blockTypes.get(rand.nextInt(blockTypes.size())).create();
            block.initBlocks(firstBlockCoordin);
            block.setColor(colorGenerator.next());
            if(rand.nextInt(2)==0){
                block.transform();
            }
            return block;
        }
    }
}
class SquareTetrisBlock extends TetrisBlock {//方形砖块
    @Override
    protected void initBlocks(Point firstBlockCoordin){
        int x = firstBlockCoordin.getX();
        int y = firstBlockCoordin.getY();
        Block[] blocks = {
                new Block(new Point(firstBlockCoordin)),
                new Block(new Point(x+1,y)),
                new Block(new Point(x,y+1)),
                new Block(new Point(x+1,y+1)),
        };
        this.blocks = blocks;
    }
    @Override
    public void transform(){ hasTransformed = !hasTransformed;}
    public static class
    TetrisBlockFactory implements Factory<SquareTetrisBlock>{
        @Override
        public SquareTetrisBlock create(){
            return new SquareTetrisBlock();
        }
    }
}
class LTetrisBlock extends TetrisBlock {//L型砖块
    @Override
    protected void initBlocks(Point firstBlockCoordin){
        int x = firstBlockCoordin.getX();
        int y = firstBlockCoordin.getY();
        Block[] blocks = {
                new Block(new Point(firstBlockCoordin)),
                new Block(new Point(x,++y)),
                new Block(new Point(x,++y)),
                new Block(new Point(++x,y)),
                new Block(new Point(++x,y)),
        };
        this.blocks = blocks;
    }
    @Override
    public void transform(){
        int x = blocks[0].getCoordin().getX();
        int y = blocks[0].getCoordin().getY();
        if(!hasTransformed){
            blocks[1].getCoordin().setXY(x+1,y);
            blocks[2].getCoordin().setXY(x+2,y);
            blocks[3].getCoordin().setXY(x,y+1);
            blocks[4].getCoordin().setXY(x,y+2);
        }else{
            blocks[1].getCoordin().setXY(x,++y);
            blocks[2].getCoordin().setXY(x,++y);
            blocks[3].getCoordin().setXY(++x,y);
            blocks[4].getCoordin().setXY(++x,y);
        }
        hasTransformed = !hasTransformed;

    }
    public static class
    TetrisBlockFactory implements Factory<LTetrisBlock>{
        @Override
        public LTetrisBlock create(){
            return new LTetrisBlock();
        }
    }
}
class ITetrisBlock extends TetrisBlock {//竖型砖块
    @Override
    protected void initBlocks(Point firstBlockCoordin){
        int x = firstBlockCoordin.getX();
        int y = firstBlockCoordin.getY();
        Block[] blocks = {
                new Block(new Point(firstBlockCoordin)),
                new Block(new Point(x,++y)),
                new Block(new Point(x,++y)),
                new Block(new Point(x,++y)),
                new Block(new Point(x,++y)),
        };
        this.blocks = blocks;
    }
    @Override
    public void transform(){
        int x = blocks[0].getCoordin().getX();
        int y = blocks[0].getCoordin().getY();
        if(!hasTransformed){
            blocks[1].getCoordin().setXY(++x,y);
            blocks[2].getCoordin().setXY(++x,y);
            blocks[3].getCoordin().setXY(++x,y);
            blocks[4].getCoordin().setXY(++x,y);
        }else{
            blocks[1].getCoordin().setXY(x,++y);
            blocks[2].getCoordin().setXY(x,++y);
            blocks[3].getCoordin().setXY(x,++y);
            blocks[4].getCoordin().setXY(x,++y);
        }
        hasTransformed = !hasTransformed;

    }
    public static class
    TetrisBlockFactory implements Factory<ITetrisBlock>{
        @Override
        public ITetrisBlock create(){
            return new ITetrisBlock();
        }
    }
}
class STetrisBlock extends TetrisBlock {//S型砖块
    @Override
    protected void initBlocks(Point firstBlockCoordin){
        int x = firstBlockCoordin.getX();
        int y = firstBlockCoordin.getY();
        Block[] blocks = {
                new Block(new Point(firstBlockCoordin)),
                new Block(new Point(x,++y)),
                new Block(new Point(++x,y)),
                new Block(new Point(x,++y)),
        };
        this.blocks = blocks;
    }
    @Override
    public void transform(){
        int x = blocks[0].getCoordin().getX();
        int y = blocks[0].getCoordin().getY();
        if(!hasTransformed){
            blocks[1].getCoordin().setXY(++x,y);
            blocks[2].getCoordin().setXY(x,++y);
            blocks[3].getCoordin().setXY(++x,y);
        }else{
            blocks[1].getCoordin().setXY(x,++y);
            blocks[2].getCoordin().setXY(++x,y);
            blocks[3].getCoordin().setXY(x,++y);
        }
        hasTransformed = !hasTransformed;

    }
    public static class
    TetrisBlockFactory implements Factory<STetrisBlock>{
        @Override
        public STetrisBlock create(){
            return new STetrisBlock();
        }
    }

}