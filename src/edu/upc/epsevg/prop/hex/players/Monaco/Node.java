package edu.upc.epsevg.prop.hex.players.Monaco;

/**
 * La classe Node representa un node en un sistema de coordenades (x, y) amb una distància associada.
 *
 * @author Ehsan i Iván
 */
public class Node {
    int x;
    int y;
    int dist;
    
    /**
     * Constructor per defecte que inicialitza les coordenades x, y i distància a un valor màxim.
     */
    public Node() {
        this.x = Integer.MAX_VALUE;
        this.y = Integer.MAX_VALUE;
        this.dist = Integer.MAX_VALUE;
    }
    
    /**
     * Constructor que inicialitza el node amb les coordenades x, y i distància específiques.
     *
     * @param x La coordenada x del node.
     * @param y La coordenada y del node.
     * @param dist La distància associada al node.
     */
    public Node(int x, int y, int dist) {
        this.x = x;
        this.y = y;
        this.dist = dist;
    }
    
    /**
     * Obté la coordenada x del node.
     *
     * @return La coordenada x del node.
     */
    public int getX() {
        return this.x;
    }
    
    /**
     * Obté la coordenada y del node.
     *
     * @return La coordenada y del node.
     */
    public int getY() {
        return this.y;
    }
    
    /**
     * Obté la distància associada al node.
     *
     * @return La distància associada al node.
     */
    public int getDistance() {
        return this.dist;
    }
    
    /**
     * Estableix la coordenada x del node.
     *
     * @param x La nova coordenada x del node.
     */
    public void setX(int x) {
        this.x = x;
    }
    
    /**
     * Estableix la coordenada y del node.
     *
     * @param y La nova coordenada y del node.
     */
    public void setY(int y) {
        this.y = y;
    }
    
    /**
     * Estableix la distància associada al node.
     *
     * @param dist La nova distància del node.
     */
    public void setDistance(int dist) {
        this.dist = dist;
    }
}
