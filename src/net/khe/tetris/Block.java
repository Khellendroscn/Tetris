package net.khe.tetris;

import java.awt.*;
import java.io.Serializable;

/**
 * Created by hyc on 2016/11/7.
 */
public class Block implements Serializable{
    private Point coordin;
    private Color color = Color.BLACK;
    public Block(Point coordin){this.coordin = coordin;}
    public void move(Point.Dir dir){coordin = coordin.move(dir);}
    public Point getCoordin(){return coordin;}
    public void setColor(Color color){this.color = color;}
    public Color getColor(){return color;}
    public static void swap(Block b1,Block b2){
        Block temp = b1;
        b1 = b2;
        b2 = temp;
    }
    @Override
    public boolean equals(Object block){
        if(!(block instanceof Block))return false;
        Block b = (Block)block;
        return coordin.equals(b.getCoordin());
    }
    @Override
    public int hashCode(){
        return coordin.hashCode();
    }
    @Override
    public String toString(){
        return "Block: "+getCoordin()+" "+getColor();
    }
}
