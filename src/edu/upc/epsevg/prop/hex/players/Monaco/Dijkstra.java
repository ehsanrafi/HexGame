package edu.upc.epsevg.prop.hex.players.Monaco;

import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.PlayerType;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

/**
 *
 * @author Ehsan i Iván
 */
public class Dijkstra extends HexGameStatus {
    private final HexGameStatus hgs;
    //private final PlayerType pJugador;
    //private final PlayerType pEnemic;
    
    //private static final int START_NODE = -1;
    //private static final int END_NODE = -2;
    
    public Dijkstra(HexGameStatus hgs) {
        super(hgs);
        this.hgs = hgs;
        //this.pJugador = p;
        //this.pEnemic = PlayerType.opposite(p);
    }
    
    /*
    public int shortestPath() {
        return getDistance(new Point(START_NODE, START_NODE), new Point(END_NODE, END_NODE));
    }
    */
    
    /*
    public List<Point> getTopBorderNodes(HexGameStatus hgs) {
        List<Point> topBorder = new ArrayList<>();
        for (int x = 0; x < hgs.getSize(); x++) {
            topBorder.add(new Point(x, 0));
        }
        return topBorder;
    }
    
    public List<Point> getBottomBorderNodes(HexGameStatus hgs) {
        List<Point> bottomBorder = new ArrayList<>();
        for (int x = 0; x < hgs.getSize(); x++) {
            bottomBorder.add(new Point(x, hgs.getSize() - 1));
        }
        return bottomBorder;
    }
    
    public List<Point> getLeftBorderNodes(HexGameStatus hgs) {
        List<Point> leftBorder = new ArrayList<>();
        for (int y = 0; y < hgs.getSize(); y++) {
            leftBorder.add(new Point(0, y));
        }
        return leftBorder;
    }
    
    public List<Point> getRightBorderNodes(HexGameStatus hgs) {
        List<Point> rightBorder = new ArrayList<>();
        for (int y = 0; y < hgs.getSize(); y++) {
            rightBorder.add(new Point(hgs.getSize() - 1, y));
        }
        return rightBorder;
    }
    */
    
    public Set<Point> getTopBorderNodes(HexGameStatus hgs) {
        Set<Point> topBorder = new HashSet<>();
        for (int x = 0; x < hgs.getSize(); x++) {
            topBorder.add(new Point(x, 0));
        }
        return topBorder;
    }

    public Set<Point> getBottomBorderNodes(HexGameStatus hgs) {
        Set<Point> bottomBorder = new HashSet<>();
        for (int x = 0; x < hgs.getSize(); x++) {
            bottomBorder.add(new Point(x, hgs.getSize() - 1));
        }
        return bottomBorder;
    }

    public Set<Point> getLeftBorderNodes(HexGameStatus hgs) {
        Set<Point> leftBorder = new HashSet<>();
        for (int y = 0; y < hgs.getSize(); y++) {
            leftBorder.add(new Point(0, y));
        }
        return leftBorder;
    }

    public Set<Point> getRightBorderNodes(HexGameStatus hgs) {
        Set<Point> rightBorder = new HashSet<>();
        for (int y = 0; y < hgs.getSize(); y++) {
            rightBorder.add(new Point(hgs.getSize() - 1, y));
        }
        return rightBorder;
    }

    public int getDistance(PlayerType p) {
        int mida = hgs.getSize();
        int[][] dist = new int[mida][mida];
        boolean[][] visited = new boolean[mida][mida];
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(node -> node.getDistance()));
        
        for (int i = 0; i < mida; ++i) {
            for (int j = 0; j < mida; ++j) {
                dist[i][j] = Integer.MAX_VALUE;
            }
        }
        
        /*
        List<Point> startBorder = (p == PlayerType.PLAYER1) ? getLeftBorderNodes(hgs) : getTopBorderNodes(hgs);
        for (Point start : startBorder) {
            if (hgs.getPos(start.x, start.y) == PlayerType.getColor(p)) {
                dist[start.x][start.y] = 0;
                queue.add(new Node(start.x, start.y, 0));
            } else if (hgs.getPos(start.x, start.y) == 0) {
                dist[start.x][start.y] = 1;
                queue.add(new Node(start.x, start.y, 1));
            }
        }
        
        // Definir los nodos del borde final
        List<Point> endBorder = (p == PlayerType.PLAYER1) ? getRightBorderNodes(hgs) : getBottomBorderNodes(hgs);
        */
        
        Set<Point> startBorder = (p == PlayerType.PLAYER1) ? getLeftBorderNodes(hgs) : getTopBorderNodes(hgs);
        for (Point start : startBorder) {
            if (hgs.getPos(start.x, start.y) == PlayerType.getColor(p)) {
                dist[start.x][start.y] = 0;
                queue.add(new Node(start.x, start.y, 0));
            } else if (hgs.getPos(start.x, start.y) == 0) {
                dist[start.x][start.y] = 1;
                queue.add(new Node(start.x, start.y, 1));
            }
        }
        
        Set<Point> endBorder = (p == PlayerType.PLAYER1) ? getRightBorderNodes(hgs) : getBottomBorderNodes(hgs);
        
        int[][] dir = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, 1}, {1, -1}
        };
        
        while(!queue.isEmpty()) {
            Node nCurrent = queue.poll();
            Point pCurrent = new Point(nCurrent.x, nCurrent.y);
            
            if (visited[pCurrent.x][pCurrent.y]) continue;
            visited[pCurrent.x][pCurrent.y] = true;
            
            // Conectar al END_NODE si estamos en el borde final
            if (endBorder.contains(pCurrent)) {
                return nCurrent.getDistance();
            }
            
            for (int[] d : dir) {
                int nx = pCurrent.x + d[0];
                int ny = pCurrent.y + d[1];
                
                if (nx >= 0 && nx < mida && ny >= 0 && ny < mida && !visited[nx][ny]) {
                    //int c = (hgs.getPos(nx, ny) == 0) ? 1 : 0; //coste
                    int c = (hgs.getPos(nx, ny) == PlayerType.getColor(p)) ? 0 : 1;
                    if (hgs.getPos(nx, ny) == 0 || hgs.getPos(nx, ny) == PlayerType.getColor(p)) {
                        int newDist = dist[pCurrent.x][pCurrent.y] + c;
                        if (newDist < dist[nx][ny]) {
                            dist[nx][ny] = newDist;
                            queue.add(new Node(nx, ny, newDist));
                        }
                    }
                }
            }
        }
        
        return Integer.MAX_VALUE;
    }
}
