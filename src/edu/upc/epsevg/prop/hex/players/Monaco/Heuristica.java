package edu.upc.epsevg.prop.hex.players.Monaco;

import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.MoveNode;
import edu.upc.epsevg.prop.hex.PlayerType;
import java.awt.Point;

/**
 *
 * @author Ehsan i Iván
 */
public class Heuristica {
    private final HexGameStatus s;
    private final PlayerType Jugador;

    public Heuristica(HexGameStatus hgs, PlayerType p) {
        this.s = hgs;
        this.Jugador = p;
    }

    public int getEvaluation(HexGameStatus s) {
        
        Dijkstra dGraf = new Dijkstra(s);
        
        //Numero de fichas para ganar
        int PlayerScore = dGraf.getDistance(Jugador);
        int EnemicScore = dGraf.getDistance(PlayerType.opposite(Jugador));
        
        int twoBridgeEvaluation = evaluateTwoBridgeState(Jugador);
        /**/
        
        int PlayerEvaluation = Math.max(1, 100 - PlayerScore);
        int EnemicEvaluation = Math.max(1, 100 - EnemicScore);
        
        return 12 * (PlayerEvaluation - EnemicEvaluation) + twoBridgeEvaluation;
    }
    
    public int evaluateTwoBridgeState(PlayerType Player) {
        int v = 0;
        int enemyColor = PlayerType.getColor(PlayerType.opposite(Player));
        int[][] directions = {
            {-1, -1}, {1, -2}, {2, -1}, {1, 1}, {-1, 2}, {-2, 1}
        };

        for (int i = 0; i < s.getSize(); ++i) {
            for (int j = 0; j < s.getSize(); ++j) {
                Point pCurrent = new Point(i, j);
                if (s.getPos(pCurrent) == PlayerType.getColor(Player)) {
                    for (int[] dir : directions) {
                        int x = i + dir[0];
                        int y = j + dir[1];
                        if (x >= 0 && x < s.getSize() && y >= 0 && y < s.getSize() && s.getPos(x, y) == PlayerType.getColor(Player)) {
                            if (dir[0] == -1 && dir[1] == -1) {
                                v += (s.getPos(i, j - 1) == 0 && s.getPos(i - 1, j) == 0) ? 2 : 0;
                            }
                            else if (dir[0] == 1 && dir[1] == -2) {
                                v += (s.getPos(i, j - 1) == 0 && s.getPos(i + 1, j - 1) == 0) ? 2 : 0;
                            }
                            else if (dir[0] == 2 && dir[1] == -1) {
                                v += (s.getPos(i + 1, j) == 0 && s.getPos(i + 1, j - 1) == 0) ? 2 : 0;
                            }
                            else if (dir[0] == 1 && dir[1] == 1) {
                                v += (s.getPos(i + 1, j) == 0 && s.getPos(i, j + 1) == 0) ? 2 : 0;
                            }
                            else if (dir[0] == -1 && dir[1] == 2) {
                                v += (s.getPos(i, j + 1) == 0 && s.getPos(i - 1, j + 1) == 0) ? 2 : 0;
                            }
                            else if (dir[0] == -2 && dir[1] == 1) {
                                v += (s.getPos(i - 1, j + 1) == 0 && s.getPos(i - 1, j) == 0) ? 2 : 0;
                            }
                        }
                    }
                }
            }
        }

        return v;
    }
}
