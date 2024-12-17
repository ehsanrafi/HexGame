package edu.upc.epsevg.prop.hex.players.Monaco;

/**
 *
 * @author Ehsan i Iván
 */
public class Node {
    int x;
    int y;
    int dist;
    
    public Node() {
        this.x = Integer.MAX_VALUE;
        this.y = Integer.MAX_VALUE;
        this.dist = Integer.MAX_VALUE;
    }
    
    public Node(int x, int y, int dist) {
        this.x = x;
        this.y = y;
        this.dist = dist;
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public int getDistance() {
        return this.dist;
    }
    
    public void setX(int x) {
        this.x = x;
    }
    
    public void setY(int y) {
        this.y = y;
    }
    
    public void setDistance(int dist) {
        this.dist = dist;
    }
}
