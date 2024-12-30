package edu.upc.epsevg.prop.hex.players.Monaco;

import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.PlayerType;
import java.awt.Point;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Classe que implementa l'algorisme de Dijkstra per calcular les distàncies
 * en un tauler del joc de Hex. Aquesta classe és utilitzada per determinar
 * el camí més curt entre els costats oposats del tauler.
 *
 * @author Ehsan i Iván
 */
public class Dijkstra {
    /**
     * Estat del joc Hex.
     */
    private final HexGameStatus hgs;
    
    /**
     * Constructor de la classe Dijkstra.
     *
     * @param hgs Estat actual del joc Hex.
     */
    public Dijkstra(HexGameStatus hgs) {
        this.hgs = hgs;
    }
    
    /**
     * Obtenen els nodes de la vora superior del tauler.
     *
     * @param hgs Estat del joc.
     * @return Un conjunt de punts que representen els nodes de la vora superior.
     */
    public Set<Point> getTopBorderNodes(HexGameStatus hgs) {
        Set<Point> topBorder = new HashSet<>();
        for (int x = 0; x < hgs.getSize(); x++) {
            topBorder.add(new Point(x, 0));
        }
        return topBorder;
    }

    /**
     * Obtenen els nodes de la vora inferior del tauler.
     *
     * @param hgs Estat del joc.
     * @return Un conjunt de punts que representen els nodes de la vora inferior.
     */
    public Set<Point> getBottomBorderNodes(HexGameStatus hgs) {
        Set<Point> bottomBorder = new HashSet<>();
        for (int x = 0; x < hgs.getSize(); x++) {
            bottomBorder.add(new Point(x, hgs.getSize() - 1));
        }
        return bottomBorder;
    }

    /**
     * Obtenen els nodes de la vora esquerra del tauler.
     *
     * @param hgs Estat del joc.
     * @return Un conjunt de punts que representen els nodes de la vora esquerra.
     */
    public Set<Point> getLeftBorderNodes(HexGameStatus hgs) {
        Set<Point> leftBorder = new HashSet<>();
        for (int y = 0; y < hgs.getSize(); y++) {
            leftBorder.add(new Point(0, y));
        }
        return leftBorder;
    }

    /**
     * Obtenen els nodes de la vora dreta del tauler.
     *
     * @param hgs Estat del joc.
     * @return Un conjunt de punts que representen els nodes de la vora dreta.
     */
    public Set<Point> getRightBorderNodes(HexGameStatus hgs) {
        Set<Point> rightBorder = new HashSet<>();
        for (int y = 0; y < hgs.getSize(); y++) {
            rightBorder.add(new Point(hgs.getSize() - 1, y));
        }
        return rightBorder;
    }

    /**
     * Calcula la distància mínima entre els costats oposats del tauler per a un jugador.
     *
     * @param p Tipus de jugador (PLAYER1 o PLAYER2).
     * @return La distància mínima entre els costats oposats, o Integer.MAX_VALUE si no hi ha camí.
     */
    public int getDistance(PlayerType p) {
        int mida = hgs.getSize();
        int[][] dist = new int[mida][mida];
        boolean[][] visited = new boolean[mida][mida];
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(node -> node.getDistance()));
        
        // Inicialitzar distàncies a infinit.
        for (int i = 0; i < mida; ++i) {
            for (int j = 0; j < mida; ++j) {
                dist[i][j] = Integer.MAX_VALUE;
            }
        }
        
        // Obtenir la vora inicial i configurar els nodes inicials.
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
        
        // Obtenir la vora final.
        Set<Point> endBorder = (p == PlayerType.PLAYER1) ? getRightBorderNodes(hgs) : getBottomBorderNodes(hgs);
        
        // Direccions possibles en el tauler hexagonal.
        int[][] dir = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, 1}, {1, -1}
        };
        
        // Algorisme de Dijkstra.
        while(!queue.isEmpty()) {
            Node nCurrent = queue.poll();
            Point pCurrent = new Point(nCurrent.x, nCurrent.y);
            
            if (visited[pCurrent.x][pCurrent.y]) continue;
            visited[pCurrent.x][pCurrent.y] = true;
            
            if (endBorder.contains(pCurrent)) {
                return nCurrent.getDistance();
            }
            
            for (int[] d : dir) {
                int nx = pCurrent.x + d[0];
                int ny = pCurrent.y + d[1];
                
                if (nx >= 0 && nx < mida && ny >= 0 && ny < mida && !visited[nx][ny]) {
                    int c = (hgs.getPos(nx, ny) == 0) ? 1 : 0;
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
