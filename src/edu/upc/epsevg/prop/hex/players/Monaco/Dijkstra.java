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
    
    public Dijkstra(HexGameStatus hgs) {
        super(hgs);
    }
    
    public int getDistance(HexGameStatus hgs, PlayerType p, Point sPoint, Point tPoint) {
        int mida = hgs.getSize();
        int[][] dist = new int[mida][mida];
        boolean[][] visited = new boolean[mida][mida];
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(node -> node.getDistance()));
        
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
                    if (hgs.getPos(nx, ny) == 0 || hgs.getPos(nx, ny) == PlayerType.getColor(p)) {
                        int newDist = dist[pCurrent.x][pCurrent.y] + 1;
                        if (newDist < dist[nx][ny]) {
                            dist[nx][ny] = newDist;
                            queue.add(new Node(nx, ny, newDist));
                        }
                    }
                }
            }
        }
        
        return -1;
    }
    
}
