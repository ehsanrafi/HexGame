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
    
    public int getX(Node n) {
        return n.x;
    }
    
    public int getY(Node n) {
        return n.y;
    }
    
    public int getDistance(Node n) {
        return n.dist;
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
