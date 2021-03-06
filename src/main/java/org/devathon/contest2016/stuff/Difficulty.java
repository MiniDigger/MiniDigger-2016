package org.devathon.contest2016.stuff;

/**
 * Created by Martin on 05.11.2016.
 */
public enum Difficulty {
    
    EASY(5, 5), NORMAL(10, 10), HARD(15, 15);
    
    private int xSize;
    private int zSize;
    
    Difficulty(int xSize, int zSize) {
        this.xSize = xSize;
        this.zSize = zSize;
    }
    
    public int getXSize() {
        return xSize;
    }
    
    public int getZSize() {
        return zSize;
    }
}

