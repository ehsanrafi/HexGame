package edu.upc.epsevg.prop.hex.players.Monaco;

import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.PlayerType;
import java.awt.Point;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 *
 * @author Ehsan i Iván
 */
public class Dijkstra extends HexGameStatus {
    private final HexGameStatus hgs;
    private final PlayerType p;
    
    
    public Dijkstra(HexGameStatus hgs, PlayerType p) {
        super(hgs);
        this.hgs = hgs;
        this.p = p;
    }
    
    /*
    public int shortestPath() {
        return getDistance(hgs, p, new Point(-1, -1), new Point(-2, -2));
    }
    */
    
    public int getDistance(Point sPoint, Point tPoint) {
        int mida = hgs.getSize();
        int[][] dist = new int[mida][mida];
        boolean[][] visited = new boolean[mida][mida];
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(node -> node.getDistance()));
        
        //comprovació de que el nodo final no esté ocupado por el otro
        if((hgs.getPos(sPoint) != 0 && hgs.getPos(sPoint) != PlayerType.getColor(p)) ||
           (hgs.getPos(tPoint) != 0 && hgs.getPos(tPoint) != PlayerType.getColor(p))) {
            return Integer.MAX_VALUE;
        }
        
        for (int i = 0; i < mida; ++i) {
            for (int j = 0; j < mida; ++j) {
                dist[i][j] = Integer.MAX_VALUE;
            }
        }
        
        dist[sPoint.x][sPoint.y] = 0;
        queue.add(new Node(sPoint.x, sPoint.y, 0));
        
        int[][] dir = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, 1}, {1, -1}
        };
        
        while(!queue.isEmpty()) {
            Node nCurrent = queue.poll();
            Point pCurrent = new Point(nCurrent.x, nCurrent.y);
            
            if (visited[pCurrent.x][pCurrent.y]) continue;
            visited[pCurrent.x][pCurrent.y] = true;
            
            if(pCurrent.equals(tPoint)) {
                return nCurrent.getDistance();
            }
            
            for (int[] d : dir) {
                int nx = pCurrent.x + d[0];
                int ny = pCurrent.y + d[1];
                
                if (nx >= 0 && nx < mida && ny >= 0 && ny < mida && !visited[nx][ny]) {
                    int c = hgs.getPos(nx, ny) == 0 ? 1 : 0; //coste
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
