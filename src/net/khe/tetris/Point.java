package net.khe.tetris;

import net.khe.util.EnumRandom;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by hyc on 2016/11/2.
 */
public class Point implements Serializable{
    enum Dir{
        NORTH,
        SOUTH,
        EAST,
        WEST;
        public Dir opposite(){
            Dir opp = null;
            switch (this){
                case NORTH:opp = SOUTH;break;
                case SOUTH:opp = NORTH;break;
                case EAST:opp = EAST;break;
                case WEST:opp = EAST;break;
            }
            return opp;
        }
    }
    private int x;
    private int y;
    public void setX(int x){this.x = x;}
    public void setY(int y){this.y = y;}
    public void setXY(int x,int y){this.x = x;this.y = y;}
    public int getX(){return x;}
    public int getY(){return y;}
    public Point(int x,int y){setX(x);setY(y);}
    public Point(){this(0,0);}
    public Point(Point point){this(point.getX(),point.getY());}
    public Point move(Dir dir){
        Point result=null;
        switch (dir){
            case NORTH:result = new Point(x,y-1);break;
            case SOUTH:result = new Point(x,y+1);break;
            case WEST:result = new Point(x-1,y);break;
            case EAST:result = new Point(x+1,y);break;
        }
        return result;
    }
    public Point[] getLinkPoints(){
        return new Point[]{
                new Point(x+1,y),
                new Point(x-1,y),
                new Point(x,y+1),
                new Point(x,y-1),
        };
    }
    @Override
    public String toString(){return "[ "+x+" , "+y+" ]";}
    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof Point)) return false;
        Point point = (Point)obj;
        return x==point.getX()&&y==point.getY();
    }
    @Override
    public int hashCode(){
        int result = 17;
        return result+37+x+y;
    }

    public static void main(String[] args) {
        Point point = new Point(0,0);
        for(int i=0;i<10;++i){
            point = point.move(EnumRandom.random(Dir.class));
            System.out.println(point);
        }
    }
}
